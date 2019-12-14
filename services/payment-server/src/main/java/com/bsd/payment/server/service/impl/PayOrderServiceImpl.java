package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.configuration.channel.alipay.AlipayProperties;
import com.bsd.payment.server.configuration.channel.wechat.WxPayProperties;
import com.bsd.payment.server.configuration.channel.wechat.WxPayUtil;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.domain.BaseParam;
import com.bsd.payment.server.enumm.RetEnum;
import com.bsd.payment.server.mapper.PayOrderMapper;
import com.bsd.payment.server.model.entity.PayChannel;
import com.bsd.payment.server.model.entity.PayOrder;
import com.bsd.payment.server.service.*;
import com.bsd.payment.server.util.*;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
@Service
public class PayOrderServiceImpl extends BaseService implements IPayOrderService {
    private static final MyLog _log = MyLog.getLog(PayOrderServiceImpl.class);

    @Autowired
    private INotifyPayService notifyPayService;

    @Autowired
    private IPayChannel4WxService payChannel4WxService;

    @Autowired
    private IPayChannel4AliService payChannel4AliService;

    @Resource
    private PayOrderMapper payOrderMapper;

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    Notify4Pay notify4Pay;

    @Autowired
    private WxPayProperties wxPayProperties;

    @Override
    public int createPayOrder(JSONObject payOrder) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrder", payOrder);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map<String, Object> result = createPayOrder(jsonParam);
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    @Override
    public JSONObject queryPayOrder(String mchId, String payOrderId, String mchOrderNo, String executeNotify) {
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> result;
        if (StringUtils.isNotBlank(payOrderId)) {
            paramMap.put("mchId", mchId);
            paramMap.put("payOrderId", payOrderId);
            String jsonParam = RpcUtil.createBaseParam(paramMap);
            result = selectPayOrderByMchIdAndPayOrderId(jsonParam);
        } else {
            paramMap.put("mchId", mchId);
            paramMap.put("mchOrderNo", mchOrderNo);
            String jsonParam = RpcUtil.createBaseParam(paramMap);
            result = selectPayOrderByMchIdAndMchOrderNo(jsonParam);
        }
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return null;
        }
        boolean isNotify = Boolean.parseBoolean(executeNotify);
        JSONObject payOrder = JSONObject.parseObject(s);
        if (isNotify) {
            paramMap = new HashMap<>();
            paramMap.put("payOrderId", payOrderId);
            String jsonParam = RpcUtil.createBaseParam(paramMap);
            result = notifyPayService.sendBizPayNotify(jsonParam);
            s = RpcUtil.mkRet(result);
            _log.info("业务查单完成,并再次发送业务支付通知.发送结果:{}", s);
        }
        return payOrder;
    }

    @Override
    public String doWxPayReq(String tradeType, JSONObject payOrder, String resKey) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tradeType", tradeType);
        paramMap.put("payOrder", payOrder);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map<String, Object> result = payChannel4WxService.doWxPayReq(jsonParam);
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return XXPayUtil.makeRetData(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_SUCCESS, "", PayConstant.RETURN_VALUE_FAIL, "0111", "调用微信支付失败"), resKey);
        }
        Map<String, Object> map = XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_SUCCESS, "", PayConstant.RETURN_VALUE_SUCCESS, null);
        map.putAll((Map) result.get("bizResult"));
        return XXPayUtil.makeRetData(map, resKey);
    }

    @Override
    public String doAliPayReq(String channelCode, JSONObject payOrder, String resKey) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrder", payOrder);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map<String, Object> result;
        switch (channelCode) {
            case PayConstant.PAY_CHANNEL_ALIPAY_MOBILE:
                result = payChannel4AliService.doAliPayMobileReq(jsonParam);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_PC:
                result = payChannel4AliService.doAliPayPcReq(jsonParam);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_WAP:
                result = payChannel4AliService.doAliPayWapReq(jsonParam);
                break;
            case PayConstant.PAY_CHANNEL_ALIPAY_QR:
                result = payChannel4AliService.doAliPayQrReq(jsonParam);
                break;
            default:
                result = null;
                break;
        }
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return XXPayUtil.makeRetData(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_SUCCESS, "", PayConstant.RETURN_VALUE_FAIL, "0111", "调用支付宝支付失败"), resKey);
        }
        Map<String, Object> map = XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_SUCCESS, "", PayConstant.RETURN_VALUE_SUCCESS, null);
        map.putAll((Map) result.get("bizResult"));
        return XXPayUtil.makeRetData(map, resKey);
    }

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<PayOrder> findListPage(PageParams pageParams) {
        PayOrder query = pageParams.mapToObject(PayOrder.class);
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Object createTimeStart = pageParams.getRequestMap().get("createTimeStart");
        Object createTimeEnd = pageParams.getRequestMap().get("createTimeEnd");
        Object paySuccTimeStart = pageParams.getRequestMap().get("paySuccTimeStart");
        Object paySuccTimeEnd = pageParams.getRequestMap().get("paySuccTimeTimeEnd");
        boolean isCreateTime = ObjectValidUtil.isNotEmptyBatch(createTimeStart, createTimeEnd);
        boolean isPaySuccTime = ObjectValidUtil.isNotEmptyBatch(paySuccTimeStart, paySuccTimeEnd);
        if (isCreateTime) {
            //创建时间范围
            String startTime = dateFormat.format(createTimeStart);
            String endTime = dateFormat.format(createTimeEnd);
            if (startTime.compareTo(endTime) == 1) {
                throw new OpenAlertException(" 创建开始时间不能大于创建结束时间");
            }
        }
        if (isPaySuccTime) {
            //成功下单时间范围
            String succTimeStart = dateFormat.format(paySuccTimeStart);
            String succTimeEnd = dateFormat.format(paySuccTimeEnd);
            if (succTimeStart.compareTo(succTimeEnd) == 1) {
                throw new OpenAlertException("订单支付成功开始时间不能大于订单支付成功结束时间");
            }
        }
        queryWrapper.lambda()
                .select(PayOrder::getPayOrderId,
                        PayOrder::getMchId,
                        PayOrder::getChannelCode,
                        PayOrder::getStatus,
                        PayOrder::getAmount,
                        PayOrder::getSubject,
                        PayOrder::getMchOrderNo,
                        PayOrder::getBody,
                        PayOrder::getChannelOrderNo,
                        PayOrder::getDevice,
                        PayOrder::getPaySuccTime,
                        PayOrder::getChannelMchId,
                        PayOrder::getClientIp,
                        PayOrder::getCurrency,
                        PayOrder::getNotifyCount,
                        PayOrder::getNotifyUrl,
                        PayOrder::getErrCode,
                        PayOrder::getErrMsg,
                        PayOrder::getExpireTime,
                        PayOrder::getLastNotifyTime,
                        PayOrder::getCreateTime,
                        PayOrder::getUpdateTime)
                .eq(ObjectUtils.isNotEmpty(query.getMchOrderNo()), PayOrder::getMchOrderNo, query.getMchOrderNo())
                .likeRight(ObjectUtils.isNotEmpty(query.getMchId()), PayOrder::getMchId, query.getMchId())
                .eq(ObjectUtils.isNotNull(query.getStatus()), PayOrder::getStatus, query.getStatus())
                .likeRight(ObjectUtils.isNotEmpty(query.getPayOrderId()), PayOrder::getPayOrderId, query.getPayOrderId())
                .eq(ObjectUtils.isNotEmpty(query.getChannelCode()), PayOrder::getChannelCode, query.getChannelCode())
                .eq(ObjectUtils.isNotEmpty(query.getChannelMchId()), PayOrder::getChannelMchId, query.getChannelMchId())
                .eq(ObjectUtils.isNotEmpty(query.getChannelOrderNo()), PayOrder::getChannelOrderNo, query.getChannelOrderNo())
                .ge(ObjectUtils.isNotEmpty(createTimeStart), PayOrder::getCreateTime, createTimeStart)
                .le(ObjectUtils.isNotEmpty(createTimeEnd), PayOrder::getCreateTime, createTimeEnd)
                .ge(ObjectUtils.isNotEmpty(paySuccTimeStart), PayOrder::getPaySuccTime, paySuccTimeStart)
                .le(ObjectUtils.isNotEmpty(paySuccTimeEnd), PayOrder::getPaySuccTime, paySuccTimeEnd)
                .orderByDesc(PayOrder::getCreateTime);
        return payOrderMapper.selectPage(pageParams, queryWrapper);
    }

    @Override
    public PayOrder findPayOrder(String payOrderId) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(PayOrder::getPayOrderId, payOrderId);
        return payOrderMapper.selectOne(queryWrapper);
    }

    @Override
    public Map createPayOrder(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("新增支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        JSONObject payOrderObj = baseParam.isNullValue("payOrder") ? null : JSONObject.parseObject(bizParamMap.get("payOrder").toString());
        if (payOrderObj == null) {
            _log.warn("新增支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        PayOrder payOrder = BeanConvertUtils.map2Bean(payOrderObj, PayOrder.class);
        if (payOrder == null) {
            _log.warn("新增支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }

        // 检查是否有状态为 支付成功|业务处理完成 的支付单
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getMchId, payOrder.getMchId())
                .eq(PayOrder::getMchOrderNo, payOrder.getMchOrderNo())
                .in(PayOrder::getStatus, PayConstant.PAY_STATUS_SUCCESS, PayConstant.PAY_STATUS_COMPLETE);
        List<PayOrder> payOrderList = payOrderMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(payOrderList)) {
            _log.warn("新增支付订单失败, {}. jsonParam={}", RetEnum.RET_DB_EXISTS.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_DB_EXISTS);
        }

        int result = super.baseCreatePayOrder(payOrder);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map selectPayOrder(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("根据支付订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String payOrderId = baseParam.isNullValue("payOrderId") ? null : bizParamMap.get("payOrderId").toString();
        if (ObjectValidUtil.isInvalid(payOrderId)) {
            _log.warn("根据支付订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        PayOrder payOrder = super.baseSelectPayOrder(payOrderId);
        if (payOrder == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(payOrder);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public Map selectPayOrderByMchIdAndPayOrderId(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("根据商户号和支付订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String mchId = baseParam.isNullValue("mchId") ? null : bizParamMap.get("mchId").toString();
        String payOrderId = baseParam.isNullValue("payOrderId") ? null : bizParamMap.get("payOrderId").toString();
        if (ObjectValidUtil.isInvalid(mchId, payOrderId)) {
            _log.warn("根据商户号和支付订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        PayOrder payOrder = super.baseSelectPayOrderByMchIdAndPayOrderId(mchId, payOrderId);
        if (payOrder == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(payOrder);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public Map selectPayOrderByMchIdAndMchOrderNo(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("根据商户号和商户订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String mchId = baseParam.isNullValue("mchId") ? null : bizParamMap.get("mchId").toString();
        String mchOrderNo = baseParam.isNullValue("mchOrderNo") ? null : bizParamMap.get("mchOrderNo").toString();
        if (ObjectValidUtil.isInvalid(mchId, mchOrderNo)) {
            _log.warn("根据商户号和商户订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        PayOrder payOrder = super.baseSelectPayOrderByMchIdAndMchOrderNo(mchId, mchOrderNo);
        if (payOrder == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(payOrder);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public Map updateStatus4Ing(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改支付订单状态为支付中失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String payOrderId = baseParam.isNullValue("payOrderId") ? null : bizParamMap.get("payOrderId").toString();
        String channelOrderNo = baseParam.isNullValue("channelOrderNo") ? null : bizParamMap.get("channelOrderNo").toString();
        if (ObjectValidUtil.isInvalid(payOrderId, channelOrderNo)) {
            _log.warn("修改支付订单状态为支付中失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = super.baseUpdateStatus4Ing(payOrderId, channelOrderNo);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map updateStatus4Success(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改支付订单状态为支付成功失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String payOrderId = baseParam.isNullValue("payOrderId") ? null : bizParamMap.get("payOrderId").toString();
        if (ObjectValidUtil.isInvalid(payOrderId)) {
            _log.warn("修改支付订单状态为支付成功失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = super.baseUpdateStatus4Success(payOrderId, null);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map updateStatus4Complete(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改支付订单状态为支付完成失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String payOrderId = baseParam.isNullValue("payOrderId") ? null : bizParamMap.get("payOrderId").toString();
        if (ObjectValidUtil.isInvalid(payOrderId)) {
            _log.warn("修改支付订单状态为支付完成失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = super.baseUpdateStatus4Complete(payOrderId);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map updateNotify(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改支付订单通知次数失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String payOrderId = baseParam.isNullValue("payOrderId") ? null : bizParamMap.get("payOrderId").toString();
        Byte count = baseParam.isNullValue("count") ? null : Byte.parseByte(bizParamMap.get("count").toString());
        if (ObjectValidUtil.isInvalid(payOrderId, count)) {
            _log.warn("修改支付订单通知次数失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = super.baseUpdateNotify(payOrderId, count);
        return RpcUtil.createBizResult(baseParam, result);
    }

    //将支付未完成的支付订单置为过期
    @Override
    public int updateStatus4Expired(int seconds) {
        PayOrder payOrder = new PayOrder();
        payOrder.setStatus(PayConstant.PAY_STATUS_EXPIRED);
        payOrder.setUpdateTime(new Date());
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().in(PayOrder::getStatus, PayConstant.PAY_STATUS_INIT, PayConstant.PAY_STATUS_PAYING)
                .and(obj1 -> obj1.lt(PayOrder::getCreateTime, new Date(System.currentTimeMillis() - 3600 * 24 * 1000))
                        .or(obj2 -> obj2.isNotNull(PayOrder::getExpireTime)
                                .lt(PayOrder::getExpireTime, new Date(System.currentTimeMillis() - seconds))));
        return payOrderMapper.update(payOrder, wrapper);
    }

    /**
     * 同步支付结果
     */
    @Override
    public int synPayResult(int seconds) {
        int count = 0;
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(PayOrder::getStatus, PayConstant.PAY_STATUS_PAYING)
                .lt(PayOrder::getCreateTime, new Date(System.currentTimeMillis() - seconds * 1000));
        List<PayOrder> list = payOrderMapper.selectList(queryWrapper);
        for (PayOrder payOrder : list) {
            if (PayConstant.PAY_CHANNEL_ALIPAY_MOBILE.equals(payOrder.getChannelCode())
                    || PayConstant.PAY_CHANNEL_ALIPAY_PC.equals(payOrder.getChannelCode())
                    || PayConstant.PAY_CHANNEL_ALIPAY_QR.equals(payOrder.getChannelCode())
                    || PayConstant.PAY_CHANNEL_ALIPAY_WAP.equals(payOrder.getChannelCode())) {
                if (queryAliPayResult(payOrder)) {
                    count++;
                }
            } else if (PayConstant.PAY_CHANNEL_WX_APP.equals(payOrder.getChannelCode())
                    || PayConstant.PAY_CHANNEL_WX_JSAPI.equals(payOrder.getChannelCode())
                    || PayConstant.PAY_CHANNEL_WX_MWEB.equals(payOrder.getChannelCode())
                    || PayConstant.PAY_CHANNEL_WX_NATIVE.equals(payOrder.getChannelCode())) {
                if (queryWxPayResult(payOrder)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Boolean queryAliPayResult(PayOrder payOrder) {
        String payOrderId = payOrder.getPayOrderId();
        if (StringUtil.isEmpty(payOrderId)) {
            throw new OpenAlertException("支付订单号不能为空");
        }

        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(payOrderId);
        queryRequest.setBizModel(model);

        PayChannel payChannel = super.baseSelectPayChannel(payOrder.getMchId(), payOrder.getChannelCode());
        alipayProperties.init(payChannel.getParam());

        String logPrefix = "【处理支付宝支付结果查询】";
        AlipayTradeQueryResponse response = null;
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayProperties.getUrl(), alipayProperties.getAppid(), alipayProperties.getPrivateKey(), AlipayProperties.FORMAT, AlipayProperties.CHARSET, alipayProperties.getPublicKey(), AlipayProperties.SIGNTYPE);
            response = alipayClient.execute(queryRequest);
            _log.info("{}订单查询结果：{}", logPrefix, response.getBody());
        } catch (AlipayApiException e) {
            _log.error("{}支付宝查询订单{}失败！{}", logPrefix, payOrderId, e);
        }

        if (response == null) {
            _log.error("{}支付宝未获取订单{}详情！", logPrefix, payOrderId);
        }

        if (response.isSuccess()) {
            if (PayConstant.AlipayConstant.TRADE_STATUS_SUCCESS.equals(response.getTradeStatus()) || PayConstant.AlipayConstant.TRADE_STATUS_FINISHED.equals(response.getTradeStatus())
                    || PayConstant.AlipayConstant.TRADE_STATUS_CLOSED.equals(response.getTradeStatus())) {
                int updatePayOrderRows;
                byte payStatus = payOrder.getStatus(); // 0：订单生成，1：支付中，-1：支付失败，2：支付成功，3：业务处理完成，-2：订单过期
                if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                    if (PayConstant.AlipayConstant.TRADE_STATUS_CLOSED.equals(response.getTradeStatus())) {
                        updatePayOrderRows = super.baseUpdateStatus4Failed(payOrder.getPayOrderId());
                        payOrder.setStatus(PayConstant.PAY_STATUS_FAILED);
                        _log.error("支付宝订单" + payOrderId + "交易失败，交易状态：" + response.getTradeStatus());
                    } else {
                        updatePayOrderRows = super.baseUpdateStatus4Success(payOrder.getPayOrderId(), response.getTradeNo());
                        payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
                        payOrder.setChannelOrderNo(response.getTradeNo());
                    }
                    if (updatePayOrderRows != 1) {
                        _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(), payOrder.getStatus());
                    }
                    _log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), payOrder.getStatus());
                    notify4Pay.doNotify(payOrder);
                    return true;
                }
            } else {
                // 其他状态
                _log.info("{}支付状态trade_status={},不做业务处理", logPrefix, response.getTradeStatus());
            }
        } else {
            _log.error("支付宝订单" + payOrderId + "查询失败！");
        }

        return false;
    }

    @Override
    public Boolean queryWxPayResult(PayOrder payOrder) {
        String payOrderId = payOrder.getPayOrderId();
        if (StringUtil.isEmpty(payOrderId)) {
            throw new OpenAlertException("支付订单号不能为空");
        }
        String mchId = payOrder.getMchId();
        String channelCode = payOrder.getChannelCode();
        PayChannel payChannel = super.baseSelectPayChannel(mchId, channelCode);
        WxPayConfig wxPayConfig = WxPayUtil.getWxPayConfig(payChannel.getParam(), "", wxPayProperties.getCertRootPath(), wxPayProperties.getNotifyUrl());
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);

        Map<String, Object> map = new HashMap<>();
        WxPayOrderQueryResult result;
        String logPrefix = "【处理微信支付结果查询】";
        try {
            result = wxPayService.queryOrder(null, payOrderId);
            _log.info("{} >>> 成功", logPrefix);
            map.putAll((Map) JSON.toJSON(result));
            map.put("isSuccess", true);
            map.put("payOrderId", payOrderId);
            _log.info(map.toString());
        } catch (WxPayException e) {
            _log.error(e, "查询微信订单失败");
            //出现业务错误
            _log.info("{} 返回失败", logPrefix);
            _log.info("err_code:{}", e.getErrCode());
            _log.info("err_code_des:{}", e.getErrCodeDes());
            map.put("channelErrCode", e.getErrCode());
            map.put("channelErrMsg", e.getErrCodeDes());
            map.put("isSuccess", false);
        }
        //订单信息返回成功
        if (map.get("isSuccess").equals(true)) {
            // 0：订单生成，1：支付中，-1：支付失败，2：支付成功，3：业务处理完成，-2：订单过期
            byte payStatus = payOrder.getStatus();
            int updatePayOrderRows = 0;
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                //未支付 || 已关闭
                if ("CLOSED".equals(map.get("tradeState").toString())) {
                    updatePayOrderRows = super.baseUpdateStatus4Failed(payOrder.getPayOrderId());
                    payOrder.setStatus(PayConstant.PAY_STATUS_FAILED);
                } else if (!"NOTPAY".equals(map.get("tradeState").toString())) {
                    updatePayOrderRows = super.baseUpdateStatus4Success(payOrder.getPayOrderId(), map.get("transactionId").toString());
                    payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
                }
                if (updatePayOrderRows == 1) {
                    _log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), payOrder.getStatus());

                    // 业务系统后端通知
                    notify4Pay.doNotify(payOrder);
                    return true;
                }

                _log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(), payOrder.getStatus());
            } else {
                //错误状态
                _log.info("{}支付状态return_code={},不做业务处理", logPrefix, map.get("returnCode"));
            }
        } else {
            _log.error("微信订单" + payOrderId + "查询失败！");
        }
        return false;
    }
}
