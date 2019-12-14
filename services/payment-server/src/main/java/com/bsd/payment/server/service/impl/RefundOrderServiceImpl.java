package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.domain.BaseParam;
import com.bsd.payment.server.enumm.RetEnum;
import com.bsd.payment.server.mapper.RefundOrderMapper;
import com.bsd.payment.server.model.entity.RefundOrder;
import com.bsd.payment.server.service.*;
import com.bsd.payment.server.service.mq.Mq4RefundNotify;
import com.bsd.payment.server.util.*;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangyankai
 * @date 2019/8/20
 */
@Service
public class RefundOrderServiceImpl implements IRefundOrderService {
    private static final MyLog _log = MyLog.getLog(RefundOrderServiceImpl.class);

    @Resource
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private Mq4RefundNotify mq4RefundNotify;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private IMchInfoService mchInfoService;

    @Autowired
    private IPayChannelService payChannelService;

    @Autowired
    private Service4Refund service4Refund;


    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<RefundOrder> findListPage(PageParams pageParams) {
        RefundOrder query = pageParams.mapToObject(RefundOrder.class);
        QueryWrapper<RefundOrder> queryWrapper = new QueryWrapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Object createTimeStart = pageParams.getRequestMap().get("createTimeStart");
        Object createTimeEnd = pageParams.getRequestMap().get("createTimeEnd");
        Object refundSuccTimeStart = pageParams.getRequestMap().get("refundSuccTimeStart");
        Object refundSuccTimeEnd = pageParams.getRequestMap().get("refundSuccTimeEnd");

        boolean isCreateTime = ObjectValidUtil.isNotEmptyBatch(createTimeStart, createTimeEnd);
        boolean isRefundSuccTime = ObjectValidUtil.isNotEmptyBatch(refundSuccTimeStart, refundSuccTimeEnd);
        if (isCreateTime) {
            //创建时间范围
            String timeStart = dateFormat.format(refundSuccTimeStart);
            String timeEnd = dateFormat.format(refundSuccTimeEnd);
            if (timeStart.compareTo(timeEnd) == 1) {
                throw new OpenAlertException("创建开始时间不能 大于创建结束时间");
            }
        }
        if (isRefundSuccTime) {
            //退款时间范围
            String succTimeStart = dateFormat.format(refundSuccTimeStart);
            String succTimeEnd = dateFormat.format(refundSuccTimeEnd);
            if (succTimeStart.compareTo(succTimeEnd) == 1) {
                throw new OpenAlertException("订单退款开始成功时间不能大于订单退款结束成功时间");
            }
        }
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getRefundOrderId()), RefundOrder::getRefundOrderId, query.getRefundOrderId())
                .likeRight(ObjectUtils.isNotEmpty(query.getMchId()), RefundOrder::getMchId, query.getMchId())
                .eq(ObjectUtils.isNotNull(query.getStatus()), RefundOrder::getStatus, query.getStatus())
                .eq(ObjectUtils.isNotEmpty(query.getPayOrderId()), RefundOrder::getPayOrderId, query.getPayOrderId())
                .eq(ObjectUtils.isNotEmpty(query.getChannelCode()), RefundOrder::getChannelCode, query.getChannelCode())
                .eq(ObjectUtils.isNotEmpty(query.getChannelMchId()), RefundOrder::getChannelMchId, query.getChannelMchId())
                .eq(ObjectUtils.isNotEmpty(query.getChannelOrderNo()), RefundOrder::getChannelOrderNo, query.getChannelOrderNo())
                .eq(ObjectUtils.isNotEmpty(query.getChannelUser()), RefundOrder::getChannelUser, query.getChannelUser())
                .eq(ObjectUtils.isNotEmpty(query.getChannelPayOrderNo()), RefundOrder::getChannelPayOrderNo, query.getChannelPayOrderNo())
                .eq(ObjectUtils.isNotEmpty(query.getMchRefundNo()), RefundOrder::getMchRefundNo, query.getMchRefundNo())
                .likeRight(ObjectUtils.isNotEmpty(query.getUserName()), RefundOrder::getUserName, query.getUserName())
                .ge(ObjectUtils.isNotEmpty(createTimeStart), RefundOrder::getCreateTime, createTimeStart)
                .le(ObjectUtils.isNotEmpty(createTimeStart), RefundOrder::getCreateTime, createTimeEnd)
                .ge(ObjectUtils.isNotEmpty(refundSuccTimeStart), RefundOrder::getRefundSuccTime, refundSuccTimeStart)
                .le(ObjectUtils.isNotEmpty(refundSuccTimeEnd), RefundOrder::getRefundSuccTime, refundSuccTimeEnd)
                .orderByDesc(RefundOrder::getCreateTime);
        return refundOrderMapper.selectPage(pageParams, queryWrapper);
    }

    @Override
    public RefundOrder findRefundOrder(String refundOrderId) {
        QueryWrapper<RefundOrder> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(RefundOrder::getRefundOrderId, refundOrderId);
        return refundOrderMapper.selectOne(queryWrapper);
    }

    @Override
    public int create(JSONObject refundOrder) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("refundOrder", refundOrder);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map<String, Object> result = create(jsonParam);
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    @Override
    public Map create(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("新增退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        JSONObject refundOrderObj = baseParam.isNullValue("refundOrder") ? null : JSONObject.parseObject(bizParamMap.get("refundOrder").toString());
        if (refundOrderObj == null) {
            _log.warn("新增退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        RefundOrder refundOrder = BeanConvertUtils.map2Bean(refundOrderObj, RefundOrder.class);
        if (refundOrder == null) {
            _log.warn("新增退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = service4Refund.createRefundOrder(refundOrder);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map select(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("根据退款订单号查询退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String refundOrderId = baseParam.isNullValue("refundOrderId") ? null : bizParamMap.get("refundOrderId").toString();
        if (ObjectValidUtil.isInvalid(refundOrderId)) {
            _log.warn("根据退款订单号查询退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        RefundOrder refundOrder = service4Refund.selectRefundOrder(refundOrderId);
        if (refundOrder == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(refundOrder);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public Map selectByMchIdAndRefundOrderId(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("根据商户号和退款订单号查询退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String mchId = baseParam.isNullValue("mchId") ? null : bizParamMap.get("mchId").toString();
        String refundOrderId = baseParam.isNullValue("refundOrderId") ? null : bizParamMap.get("refundOrderId").toString();
        if (ObjectValidUtil.isInvalid(mchId, refundOrderId)) {
            _log.warn("根据商户号和退款订单号查询退款订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        RefundOrder refundOrder = service4Refund.selectByMchIdAndRefundOrderId(mchId, refundOrderId);
        if (refundOrder == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(refundOrder);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public Map selectByMchIdAndMchRefundNo(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("根据 商户号和商户订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String mchId = baseParam.isNullValue("mchId") ? null : bizParamMap.get("mchId").toString();
        String mchRefundNo = baseParam.isNullValue("mchRefundNo") ? null : bizParamMap.get("mchRefundNo").toString();
        if (ObjectValidUtil.isInvalid(mchId, mchRefundNo)) {
            _log.warn("根据 商户号和商户订单号查询支付订单失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        RefundOrder refundOrder = service4Refund.selectByMchIdAndMchRefundNo(mchId, mchRefundNo);
        if (refundOrder == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(refundOrder);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public Map updateStatus4Ing(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改退款订单状态失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String refundOrderId = baseParam.isNullValue("refundOrderId") ? null : bizParamMap.get("refundOrderId").toString();
        String channelOrderNo = baseParam.isNullValue("channelOrderNo") ? null : bizParamMap.get("channelOrderNo").toString();
        if (ObjectValidUtil.isInvalid(refundOrderId)) {
            _log.warn("修改退款订单状态失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = service4Refund.updateStatus4Ing(refundOrderId, channelOrderNo);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map updateStatus4Success(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改 退款订单状态失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String refundOrderId = baseParam.isNullValue("refundOrderId") ? null : bizParamMap.get("refundOrderId").toString();
        if (ObjectValidUtil.isInvalid(refundOrderId)) {
            _log.warn("修改 退款订单状态失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = service4Refund.updateStatus4Success(refundOrderId);
        return RpcUtil.createBizResult(baseParam, result);
    }

    @Override
    public Map updateStatus4Complete(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("修改退款订单状态 失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String refundOrderId = baseParam.isNullValue("refundOrderId") ? null : bizParamMap.get("refundOrderId").toString();
        if (ObjectValidUtil.isInvalid(refundOrderId)) {
            _log.warn("修改退款订单状态 失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = service4Refund.updateStatus4Complete(refundOrderId);
        return RpcUtil.createBizResult(baseParam, result);
    }


    @Override
    public Map sendRefundNotify(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("发送退款订单处理失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String msg = baseParam.isNullValue("msg") ? null : bizParamMap.get("msg").toString();
        if (ObjectValidUtil.isInvalid(msg)) {
            _log.warn("发送退款订单处理失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        int result = 1;
        try {
            mq4RefundNotify.send(msg);
        } catch (Exception e) {
            _log.error(e, "");
            result = 0;
        }
        return RpcUtil.createBizResult(baseParam, result);
    }


    @Override
    public void sendRefundNotify(String refundOrderId, String channelName) {
        JSONObject object = new JSONObject();
        object.put("refundOrderId", refundOrderId);
        object.put("channelName", channelName);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("msg", object);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        sendRefundNotify(jsonParam);
    }

    /**
     * 创建退款订单
     *
     * @param params
     * @return
     */
    @Override
    public String createRefundOrder(JSONObject params) {
        _log.info("###### 开始接收商户统一退款请求 ######");
        String logPrefix = "【商户统一退款】";
        try {
            JSONObject po = JSONObject.parseObject(String.valueOf(params));
            JSONObject refundContext = new JSONObject();
            JSONObject refundOrder = null;
            // 验证参数有效性
            Object object = validateParams(po, refundContext);
            if (object instanceof String) {
                _log.info("{}参数校验不通过:{}", logPrefix, object);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, object.toString(), null, null));
            }
            if (object instanceof JSONObject) {
                refundOrder = (JSONObject) object;
            }
            if (refundOrder == null) {
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心退款失败", null, null));
            }
            int result = create(refundOrder);
            _log.info("{}创建退款订单,结果:{}", logPrefix, result);
            if (result != 1) {
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "创建退款订单失败", null, null));
            }
            // 发送异步退款消息
            String refundOrderId = refundOrder.getString("refundOrderId");
            String channelName = refundContext.getString("channelName");
            sendRefundNotify(refundOrderId, channelName);
            _log.info("{}发送退款任务完成,transOrderId={}", logPrefix, refundOrderId);

            Map<String, Object> map = XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_SUCCESS, "", PayConstant.RETURN_VALUE_SUCCESS, null);
            map.put("refundOrderId", refundOrderId);
            map.put("channelName", channelName);
            return XXPayUtil.makeRetData(map, refundContext.getString("resKey"));
        } catch (Exception e) {
            _log.error(e, "");
            return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心系统异常", null, null));
        }
    }

    /**
     * 验证创建订单请求参数,参数通过返回JSONObject对象,否则返回错误文本信息
     *
     * @param params
     * @return
     */
    private Object validateParams(JSONObject params, JSONObject refundContext) {
        // 验证请求参数,参数有问题返回错误提示
        String errorMessage;
        // 支付参数
        String mchId = params.getString("mchId");                // 商户ID
        String payOrderId = params.getString("payOrderId");     // 支付订单号
        String mchOrderNo = params.getString("mchOrderNo");     // 商户支付单号
        String mchRefundNo = params.getString("mchRefundNo");    // 商户退款单号
        String channelCode = params.getString("channelCode");        // 渠道ID
        String amount = params.getString("amount");            // 退款金额（单位分）
        String currency = params.getString("currency");         // 币种
        String clientIp = params.getString("clientIp");            // 客户端IP
        String device = params.getString("device");            // 设备
        String extra = params.getString("extra");                // 特定渠道发起时额外参数
        String param1 = params.getString("param1");            // 扩展参数1
        String param2 = params.getString("param2");            // 扩展参数2
        String notifyUrl = params.getString("notifyUrl");        // 转账结果回调URL
        String sign = params.getString("sign");                // 签名
        String channelUser = params.getString("channelUser");    // 渠道用户标识,如微信openId,支付宝账号
        String userName = params.getString("userName");            // 用户姓名
        String remarkInfo = params.getString("remarkInfo");        // 备注
        // 验证请求参数有效性（必选项）
        if (org.apache.commons.lang3.StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(payOrderId) && org.apache.commons.lang3.StringUtils.isBlank(mchOrderNo)) {
            errorMessage = "request params[payOrderId,mchOrderNo] error.";
            return errorMessage;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(mchRefundNo)) {
            errorMessage = "request params[mchRefundNo] error.";
            return errorMessage;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(channelCode)) {
            errorMessage = "request params[channelCode] error.";
            return errorMessage;
        }
        if (!NumberUtils.isDigits(amount)) {
            errorMessage = "request params[amount] error.";
            return errorMessage;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(currency)) {
            errorMessage = "request params[currency] error.";
            return errorMessage;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(notifyUrl)) {
            errorMessage = "request params[notifyUrl] error.";
            return errorMessage;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(channelUser)) {
            errorMessage = "request params[channelUser] error.";
            return errorMessage;
        }

        // 签名信息
        if (org.apache.commons.lang3.StringUtils.isEmpty(sign)) {
            errorMessage = "request params[sign] error.";
            return errorMessage;
        }

        // 查询商户信息
        JSONObject mchInfo = mchInfoService.getByMchId(mchId);
        if (mchInfo == null) {
            errorMessage = "Can't found mchInfo[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (mchInfo.getByte("state") != 1) {
            errorMessage = "mchInfo not available [mchId=" + mchId + "] record in db.";
            return errorMessage;
        }

        String reqKey = mchInfo.getString("reqKey");
        if (org.apache.commons.lang3.StringUtils.isBlank(reqKey)) {
            errorMessage = "reqKey is null[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        refundContext.put("resKey", mchInfo.getString("resKey"));

        // 查询商户对应的支付渠道
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            errorMessage = "Can't found payChannel[channelId=" + channelCode + ",mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (payChannel.getByte("state") != 1) {
            errorMessage = "channel not available [channelId=" + channelCode + ",mchId=" + mchId + "]";
            return errorMessage;
        }
        refundContext.put("channelName", payChannel.getString("channelName"));

        // 验证签名数据
        boolean verifyFlag = XXPayUtil.verifyPaySign(params, reqKey);
        if (!verifyFlag) {
            errorMessage = "Verify XX refund sign failed.";
            return errorMessage;
        }

        // 验证支付订单是否存在
        JSONObject payOrder = payOrderService.queryPayOrder(mchId, payOrderId, mchOrderNo, "false");
        if (payOrder == null) {
            errorMessage = "payOrder is not exist.";
            return errorMessage;
        }

        String channelPayOrderNo = payOrder.getString("channelOrderNo");    // 渠道测支付单号
        Long payAmount = payOrder.getLong("amount");

        // 验证参数通过,返回JSONObject对象
        JSONObject refundOrder = new JSONObject();
        refundOrder.put("refundOrderId", MySeq.getRefund());
        refundOrder.put("payOrderId", payOrderId);
        refundOrder.put("channelPayOrderNo", channelPayOrderNo);
        refundOrder.put("mchId", mchId);
        refundOrder.put("mchRefundNo", mchRefundNo);
        refundOrder.put("channelCode", channelCode);
        refundOrder.put("refundAmount", Long.parseLong(amount));    // 退款金额
        refundOrder.put("payAmount", payAmount);                    // 退款金额
        refundOrder.put("currency", currency);
        refundOrder.put("clientIp", clientIp);
        refundOrder.put("device", device);
        refundOrder.put("channelUser", channelUser);
        refundOrder.put("userName", userName);
        refundOrder.put("remarkInfo", remarkInfo);
        refundOrder.put("extra", extra);
        refundOrder.put("channelMchId", payChannel.getString("channelMchId"));
        refundOrder.put("param1", param1);
        refundOrder.put("param2", param2);
        refundOrder.put("notifyUrl", notifyUrl);
        return refundOrder;
    }


    /**
     * 验证创建订单请求参数,参数通过返回JSONObject对象,否则返回错误文本信息
     *
     * @param map
     * @return
     */
    @Override
    public String validateQueryParams(Map map, JSONObject payContext) {
        // 验证请求参数,参数有问题返回错误提示
        String errorMessage;
        // 支付参数
        String mchId = (String) map.get("mchId");// 商户ID
        String mchOrderNo = (String) map.get("mchOrderNo");    // 商户订单号
        String payOrderId = (String) map.get("payOrderId");    // 支付订单号refundOrderId
        String refundOrderId = (String) map.get("refundOrderId");    // 退款订单号

        // 验证请求参数有效性（必选项）
        if (StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(refundOrderId)) {
            errorMessage = "request params[refundOrderId] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(mchOrderNo) && StringUtils.isBlank(payOrderId)) {
            errorMessage = "request params[mchOrderNo or payOrderId] error.";
            return errorMessage;
        }

        // 查询商户信息
        JSONObject mchInfo = mchInfoService.getByMchId(mchId);
        if (mchInfo == null) {
            errorMessage = "Can't found mchInfo[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (mchInfo.getByte("state") != 1) {
            errorMessage = "mchInfo not available [mchId=" + mchId + "] record in db.";
            return errorMessage;
        }

        String reqKey = mchInfo.getString("reqKey");
        if (StringUtils.isBlank(reqKey)) {
            errorMessage = "reqKey is null[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        payContext.put("resKey", mchInfo.getString("resKey"));

        return "success";
    }
}
