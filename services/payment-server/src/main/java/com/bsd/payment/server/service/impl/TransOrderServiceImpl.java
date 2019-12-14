package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.mapper.MchInfoMapper;
import com.bsd.payment.server.mapper.MchNotifyMapper;
import com.bsd.payment.server.mapper.PayChannelMapper;
import com.bsd.payment.server.mapper.TransOrderMapper;
import com.bsd.payment.server.model.TransOrderPo;
import com.bsd.payment.server.model.dto.QueryTransResultDTO;
import com.bsd.payment.server.model.dto.TransResultDTO;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.model.entity.MchNotify;
import com.bsd.payment.server.model.entity.PayChannel;
import com.bsd.payment.server.model.entity.TransOrder;
import com.bsd.payment.server.service.INotifyTransService;
import com.bsd.payment.server.service.IPayService;
import com.bsd.payment.server.service.ITransOrderService;
import com.bsd.payment.server.service.mq.MqService;
import com.bsd.payment.server.util.MySeq;
import com.bsd.payment.server.util.ObjectValidUtil;
import com.bsd.payment.server.util.RegexUtils;
import com.bsd.payment.server.util.XXPayUtil;
import com.google.common.collect.Maps;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangyankai
 * @date 2019/8/20
 */
@Slf4j
@Service
public class TransOrderServiceImpl extends BaseServiceImpl<TransOrderMapper, TransOrder> implements ITransOrderService {
    @Resource
    private TransOrderMapper transOrderMapper;

    @Resource
    private MchInfoMapper mchInfoMapper;

    @Resource
    private PayChannelMapper payChannelMapper;

    @Autowired
    private INotifyTransService notifyTransService;

    @Resource(name = "rabbitMq4TransQueryServiceImpl")
    private MqService rabbitMq4TransQueryServiceImpl;

    @Autowired
    private Map<String, IPayService> payServiceMap;

    @Resource
    private MchNotifyMapper mchNotifyMapper;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<TransOrder> findListPage(PageParams pageParams) {
        TransOrder query = pageParams.mapToObject(TransOrder.class);
        QueryWrapper<TransOrder> queryWrapper = new QueryWrapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Object createTimeStart = pageParams.getRequestMap().get("createTimeStart");
        Object createTimeEnd = pageParams.getRequestMap().get("createTimeEnd");
        Object transSuccTimeStart = pageParams.getRequestMap().get("transSuccTimeStart");
        Object transSuccTimeEnd = pageParams.getRequestMap().get("transSuccTimeEnd");

        boolean isCreateTime = ObjectValidUtil.isNotEmptyBatch(createTimeStart, createTimeEnd);
        boolean isTransSuccTime = ObjectValidUtil.isNotEmptyBatch(transSuccTimeStart, transSuccTimeEnd);
        if (isCreateTime) {
            //创建时间范 围
            String timeStart = dateFormat.format(createTimeStart);
            String timeEnd = dateFormat.format(createTimeEnd);
            if (timeStart.compareTo(timeEnd) == 1) {
                throw new OpenAlertException("创建开始时间不能大于创建结束时间");
            }
        }
        if (isTransSuccTime) {
            String succTimeStart = dateFormat.format(transSuccTimeStart);
            String succTimeEnd = dateFormat.format(transSuccTimeEnd);
            if (succTimeStart.compareTo(succTimeEnd) == 1) {
                throw new OpenAlertException("订单转账开始成功时间不能大于订单转账结束成功时间");
            }
        }
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getTransOrderId()), TransOrder::getTransOrderId, query.getTransOrderId())
                .likeRight(ObjectUtils.isNotEmpty(query.getMchId()), TransOrder::getMchId, query.getMchId())
                .eq(ObjectUtils.isNotNull(query.getStatus()), TransOrder::getStatus, query.getStatus())
                .eq(ObjectUtils.isNotEmpty(query.getMchTransNo()), TransOrder::getMchTransNo, query.getMchTransNo())
                .eq(ObjectUtils.isNotEmpty(query.getChannelCode()), TransOrder::getChannelCode, query.getChannelCode())
                .eq(ObjectUtils.isNotEmpty(query.getChannelMchId()), TransOrder::getChannelMchId, query.getChannelMchId())
                .eq(ObjectUtils.isNotEmpty(query.getChannelOrderNo()), TransOrder::getChannelOrderNo, query.getChannelOrderNo())
                .eq(ObjectUtils.isNotEmpty(query.getChannelUser()), TransOrder::getChannelUser, query.getChannelUser())
                .likeRight(ObjectUtils.isNotEmpty(query.getUserName()), TransOrder::getUserName, query.getUserName())
                .ge(ObjectUtils.isNotEmpty(createTimeStart), TransOrder::getCreateTime, createTimeStart)
                .le(ObjectUtils.isNotEmpty(createTimeEnd), TransOrder::getCreateTime, createTimeEnd)
                .ge(ObjectUtils.isNotEmpty(transSuccTimeStart), TransOrder::getTransSuccTime, transSuccTimeStart)
                .le(ObjectUtils.isNotEmpty(transSuccTimeEnd), TransOrder::getTransSuccTime, transSuccTimeEnd)
                .orderByDesc(TransOrder::getCreateTime);
        return transOrderMapper.selectPage(pageParams, queryWrapper);
    }

    @Override
    public TransOrder findTransOrder(String transOrderId) {
        QueryWrapper<TransOrder> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(TransOrder::getTransOrderId, transOrderId);
        return transOrderMapper.selectOne(queryWrapper);
    }


    /**
     * 保存转账订单
     *
     * @param transOrder
     * @param sign
     * @return
     */
    @Override
    public boolean saveTransOrder(TransOrder transOrder, String sign, String ip) {
        //检查转账参数
        boolean isCheck = check(transOrder, sign);
        if (isCheck) {
            //设置转账订单创建时间
            transOrder.setCreateTime(new Date());
            //设置初始转账结果
            transOrder.setResult(PayConstant.TRANS_RESULT_INIT);
            //设置初始转账状态
            transOrder.setStatus(PayConstant.TRANS_STATUS_INIT);
            //保存转账订单信息
            transOrder.setTransOrderId(MySeq.getTrans());//生成转账订单号
            if (StringUtils.isEmpty(transOrder.getCurrency())) {
                transOrder.setCurrency("cny");
            }
            if (StringUtils.isEmpty(transOrder.getClientIp())) {
                transOrder.setClientIp(ip);
            }
            int count = transOrderMapper.insertSelective(transOrder);
            return count > 0;
        }
        return false;
    }


    /**
     * 处理转账订单结果
     *
     * @param resultBody
     * @param transOrder
     */
    @Override
    public void handleTransResult(ResultBody<TransResultDTO> resultBody, TransOrderPo transOrder) {
        TransResultDTO transResultDTO = resultBody.getData();
        if (resultBody.isOk()) {
            //转账成功
            updateStatus4Success(transOrder.getTransOrderId(), transResultDTO.getChannelOrderNo(), transResultDTO.getTransSuccTime());
        } else {
            if (PayConstant.TRANS_STATUS_TRANING == transResultDTO.getTransStatus()) {
                //转账业务处理中,还未有结果,异步队列中进行查询,每次延迟 count * 15 秒
                rabbitMq4TransQueryServiceImpl.send(JSON.toJSONString(transOrder), transOrder.getQueryCount() * 15 * 1000);
                return;
            }
            //转账失败
            updateStatus4Fail(transOrder.getTransOrderId(), transResultDTO.getChannelErrCode(), transResultDTO.getChannelErrMsg());
        }
        //商户通知地址非空情况下,异步通知商户转账结果
        if (StringUtils.isNotEmpty(transOrder.getNotifyUrl())) {
            MchNotify mchNotify = mchNotifyMapper.selectByPrimaryKey(transOrder.getTransOrderId());
            if (mchNotify == null) {
                notifyTransService.doNotify(transOrder, true);
            }
        } else {
            updateStatus4Complete(transOrder.getTransOrderId());
        }
    }

    /**
     * 转账订单状态修改为成功
     *
     * @param transOrderId
     * @param channelOrderNo
     * @param sucTime
     * @return
     */
    @Override
    public int updateStatus4Success(String transOrderId, String channelOrderNo, Date sucTime) {
        TransOrder transOrder = new TransOrder();
        transOrder.setChannelOrderNo(channelOrderNo);
        transOrder.setTransSuccTime(sucTime);
        transOrder.setStatus(PayConstant.TRANS_STATUS_SUCCESS);
        transOrder.setResult(PayConstant.TRANS_RESULT_SUCCESS);
        transOrder.setUpdateTime(new Date());
        int count = transOrderMapper.update(transOrder,
                Wrappers.<TransOrder>lambdaUpdate().eq(TransOrder::getTransOrderId, transOrderId)
                        .eq(TransOrder::getStatus, PayConstant.TRANS_STATUS_TRANING));
        return count;
    }

    /**
     * 转账订单状态修改成失败
     *
     * @param transOrderId
     * @param channelErrCode
     * @param channelErrMsg
     * @return
     */
    @Override
    public int updateStatus4Fail(String transOrderId, String channelErrCode, String channelErrMsg) {
        TransOrder transOrder = new TransOrder();
        transOrder.setStatus(PayConstant.TRANS_STATUS_FAIL);
        transOrder.setResult(PayConstant.TRANS_RESULT_FAIL);
        transOrder.setChannelErrCode(channelErrCode);
        transOrder.setChannelErrMsg(channelErrMsg);
        transOrder.setUpdateTime(new Date());
        int count = transOrderMapper.update(transOrder,
                Wrappers.<TransOrder>lambdaUpdate().eq(TransOrder::getTransOrderId, transOrderId)
                        .eq(TransOrder::getStatus, PayConstant.TRANS_STATUS_TRANING));
        return count;
    }

    /**
     * 转账订单状态修改为完成
     *
     * @param transOrderId
     * @return
     */
    @Override
    public int updateStatus4Complete(String transOrderId) {
        TransOrder transOrder = new TransOrder();
        transOrder.setTransOrderId(transOrderId);
        transOrder.setStatus(PayConstant.TRANS_STATUS_COMPLETE);
        transOrder.setUpdateTime(new Date());
        int count = transOrderMapper.update(transOrder, Wrappers.<TransOrder>lambdaUpdate()
                .eq(TransOrder::getTransOrderId, transOrderId)
                .and(x -> x.eq(TransOrder::getStatus, PayConstant.TRANS_STATUS_SUCCESS).or()
                        .eq(TransOrder::getStatus, PayConstant.TRANS_STATUS_FAIL)));
        return count;
    }

    /**
     * 转账订单状态修改为转账中
     *
     * @param transOrderId
     * @return
     */
    @Override
    public int updateStatus4Traning(String transOrderId) {
        TransOrder transOrder = new TransOrder();
        transOrder.setTransOrderId(transOrderId);
        transOrder.setStatus(PayConstant.TRANS_STATUS_TRANING);
        transOrder.setTransSuccTime(new Date());
        int count = transOrderMapper.update(transOrder, Wrappers.<TransOrder>lambdaUpdate()
                .eq(TransOrder::getTransOrderId, transOrderId)
                .eq(TransOrder::getStatus, PayConstant.TRANS_STATUS_INIT));
        return count;
    }

    /**
     * 根据转账订单号获取转账信息
     *
     * @param transOrderId
     * @return
     */
    @Override
    public TransOrder getByTransOrderId(String transOrderId) {
        return transOrderMapper.selectOne(Wrappers.<TransOrder>lambdaQuery().eq(TransOrder::getTransOrderId, transOrderId));
    }

    @Override
    public TransOrder getByMchTransNo(String mchTransNo) {
        return transOrderMapper.selectOne(Wrappers.<TransOrder>lambdaQuery().eq(TransOrder::getMchTransNo, mchTransNo));
    }

    /**
     * 查询转账订单结果
     *
     * @param transOrderId
     * @return
     */
    @Override
    public QueryTransResultDTO queryTransOrderResult(String transOrderId) {
        //查询转账订单
        TransOrder transOrder = getByTransOrderId(transOrderId);
        if (transOrder == null) {
            throw new OpenAlertException("查询不到转账订单信息");
        }
        //创建查询结果DTO
        QueryTransResultDTO queryTransResultDTO = new QueryTransResultDTO();
        queryTransResultDTO.setTransOrderId(transOrderId);
        //转账订单状态 0.不确认结果 1.等待手动处理 2.确认成功 3.确认失败
        byte result = transOrder.getResult();
        if (PayConstant.TRANS_RESULT_INIT == result || PayConstant.TRANS_RESULT_REFUNDING == result) {
            //转账订单结果不确定,调用第三方提供的查询接口进行查询
            queryTransResultDTO.setTransResult(getTransOrderResult(transOrder));
        } else if (PayConstant.TRANS_RESULT_SUCCESS == result || PayConstant.TRANS_RESULT_FAIL == result) {
            //已经确定成功失败的订单,设置查询结果
            queryTransResultDTO.setTransResult(result);
        } else {
            throw new OpenAlertException("订单状态异常");
        }
        return queryTransResultDTO;
    }

    /**
     * 获取转账订单结果
     *
     * @param transOrder
     * @return
     */
    private byte getTransOrderResult(TransOrder transOrder) {
        //获取支付通道
        PayChannel payChannel = payChannelMapper.selectOne(Wrappers.<PayChannel>lambdaQuery()
                .eq(PayChannel::getMchId, transOrder.getMchId())
                .eq(PayChannel::getChannelCode, transOrder.getChannelCode()));
        if (payChannel == null) {
            return PayConstant.TRANS_RESULT_INIT;
        }

        //获取通道服务
        IPayService payService = payServiceMap.get(payChannel.getChannelName() + PayConstant.PAY_CHANNEL_SERVICE_SUFFIX);
        if (payService == null) {
            return PayConstant.TRANS_RESULT_INIT;
        }

        //调用第三方查询转账结果
        ResultBody<TransResultDTO> resultBody = payService.getTransReq(transOrder, payChannel.getParam());
        if (PayConstant.TRANS_STATUS_TRANING == resultBody.getData().getTransStatus()) {
            //转账中,返回不确认结果
            return PayConstant.TRANS_RESULT_INIT;
        }

        //查询结果为成功或者失败,需要处理结果,再返回
        TransOrderPo transOrderPo = new TransOrderPo();
        BeanUtils.copyProperties(transOrder, transOrderPo);
        handleTransResult(resultBody, transOrderPo);

        return resultBody.getData().getTransStatus();
    }


    /**
     * 检查转账订单参数
     *
     * @param transOrder
     * @param sign
     * @return
     */
    private boolean check(TransOrder transOrder, String sign) {
        //验证参数,springmvc已经做了非空处理,此处校验业务参数
        String notifyUrl = transOrder.getNotifyUrl();
        if (StringUtils.isNotEmpty(notifyUrl)) {
            //设置了回调地址,校验地址格式是否正确
            boolean isUrl = RegexUtils.isUrl(notifyUrl);
            if (!isUrl) {
                throw new OpenAlertException("通知地址URL格式错误");
            }
        }

        //验证商户信息
        MchInfo mchInfo = mchInfoMapper.selectByPrimaryKey(transOrder.getMchId());
        if (mchInfo == null) {
            throw new OpenAlertException("查询不到对应的商户信息");
        }
        if (mchInfo.getState() != 1) {
            throw new OpenAlertException("商户状态为不可用");
        }
        if (StringUtils.isEmpty(mchInfo.getReqKey())) {
            throw new OpenAlertException("商户请求私钥未设置");
        }
        if (StringUtils.isEmpty(mchInfo.getResKey())) {
            throw new OpenAlertException("商户响应私钥未设置");
        }

        //验证支付通道信息
        List<PayChannel> payChannels = payChannelMapper.selectByMchIdAndChannelCode(mchInfo.getMchId(), transOrder.getChannelCode());
        if (payChannels == null) {
            throw new OpenAlertException("未找到商户对应的支付通道信息");
        }
        if (payChannels.size() != 1) {
            throw new OpenAlertException("支付通道编码存在多个支付通道信息");
        }
        PayChannel payChannel = payChannels.get(0);
        if (payChannel.getState() != 1) {
            throw new OpenAlertException("支付通道状态不可用");
        }

        //校验支付通道名称
        String channelName = payChannel.getChannelName();
        if (!PayConstant.CHANNEL_NAME_WX.equalsIgnoreCase(channelName) && !PayConstant.CHANNEL_NAME_ALIPAY.equalsIgnoreCase(channelName)) {
            throw new OpenAlertException("支付通道名称错误");
        }

        //获取验证签名的map
        Map<String, Object> params = getSignMap(transOrder, sign);
        //验证签名
        boolean verifySuc = XXPayUtil.verifyPaySign(params, mchInfo.getReqKey());
        if (!verifySuc) {
            throw new OpenAlertException("转账订单验证签名失败");
        }
        //货币代码校验
        String currency = transOrder.getCurrency();
        if (StringUtils.isNotEmpty(currency) && !"cny".equals(currency)) {
            throw new OpenAlertException("货币代码有误,货币代码不支持");
        }

        //检查订单是否已经存在
        TransOrder dbTransOrder = getByMchTransNo(transOrder.getMchTransNo());
        if (dbTransOrder != null) {
            throw new OpenAlertException("商户转账订单号已经存在");
        }

        return true;
    }

    /**
     * 获取参与签名的map
     *
     * @param transOrder
     * @param sign
     * @return
     */
    private Map<String, Object> getSignMap(TransOrder transOrder, String sign) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("mchId", transOrder.getMchId());
        paramMap.put("mchTransNo", transOrder.getMchTransNo());
        paramMap.put("channelCode", transOrder.getChannelCode());
        paramMap.put("amount", transOrder.getAmount());
        paramMap.put("currency", transOrder.getCurrency());
        paramMap.put("clientIp", transOrder.getClientIp());
        paramMap.put("device", transOrder.getDevice());
        paramMap.put("extra", transOrder.getExtra());
        paramMap.put("param1", transOrder.getParam1());
        paramMap.put("param2", transOrder.getParam2());
        paramMap.put("notifyUrl", transOrder.getNotifyUrl());
        paramMap.put("channelUser", transOrder.getChannelUser());
        paramMap.put("userName", transOrder.getUserName());
        paramMap.put("remarkInfo", transOrder.getRemarkInfo());
        paramMap.put("sign", sign);
        return paramMap;
    }
}
