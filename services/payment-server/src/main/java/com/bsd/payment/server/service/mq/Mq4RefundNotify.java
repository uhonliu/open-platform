package com.bsd.payment.server.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.entity.RefundOrder;
import com.bsd.payment.server.service.IPayChannel4AliService;
import com.bsd.payment.server.service.IPayChannel4WxService;
import com.bsd.payment.server.service.Notify4Refund;
import com.bsd.payment.server.service.Service4Refund;
import com.bsd.payment.server.util.MyLog;
import com.bsd.payment.server.util.RpcUtil;
import com.bsd.payment.server.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 业务通知MQ实现
 * @date 2017-10-30
 * @Copyright: www.xxpay.org
 */
public abstract class Mq4RefundNotify {
    @Autowired
    private IPayChannel4WxService payChannel4WxService;

    @Autowired
    private IPayChannel4AliService payChannel4AliService;

    @Autowired
    private Service4Refund service4Refund;

    @Autowired
    private Notify4Refund notify4Refund;

    protected static final MyLog _log = MyLog.getLog(Mq4RefundNotify.class);


    /**
     * 发送延迟消息
     *
     * @param msg
     * @param delay,msg
     */
    public abstract void send(String msg, long delay);


    public abstract void send(String msg);


    public void receive(String msg) {
        _log.info("处理退款任务.msg={}", msg);
        JSONObject msgObj = JSON.parseObject(msg);
        String refundOrderId = msgObj.getString("refundOrderId");
        String channelName = msgObj.getString("channelName");
        RefundOrder refundOrder = service4Refund.selectRefundOrder(refundOrderId);
        if (refundOrder == null) {
            _log.warn("查询退款订单为空,不能退款.refundOrderId={}", refundOrderId);
            return;
        }
        if (refundOrder.getStatus() != PayConstant.REFUND_STATUS_INIT) {
            _log.warn("退款状态不是初始({})或失败({}),不能退款.refundOrderId={}", PayConstant.REFUND_STATUS_INIT, PayConstant.REFUND_STATUS_FAIL, refundOrderId);
            return;
        }
        int result = service4Refund.updateStatus4Ing(refundOrderId, "");
        if (result != 1) {
            _log.warn("更改退款为退款中({})失败,不能退款.refundOrderId={}", PayConstant.REFUND_STATUS_REFUNDING, refundOrderId);
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("refundOrder", refundOrder);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map resultMap;
        if (PayConstant.CHANNEL_NAME_WX.equalsIgnoreCase(channelName)) {
            resultMap = payChannel4WxService.doWxRefundReq(jsonParam);
        } else if (PayConstant.CHANNEL_NAME_ALIPAY.equalsIgnoreCase(channelName)) {
            resultMap = payChannel4AliService.doAliRefundReq(jsonParam);
        } else {
            _log.warn("不支持的退款渠道,停止退款处理.refundOrderId={},channelName={}", refundOrderId, channelName);
            return;
        }
        if (!RpcUtil.isSuccess(resultMap)) {
            _log.warn("发起退款返回异常,停止退款处理.refundOrderId={}", refundOrderId);
            return;
        }
        Map bizResult = (Map) resultMap.get("bizResult");
        Boolean isSuccess = false;
        if (bizResult.get("isSuccess") != null) {
            isSuccess = Boolean.parseBoolean(bizResult.get("isSuccess").toString());
        }
        if (isSuccess) {
            // 更新退款状态为成功
            String channelOrderNo = StrUtil.toString(bizResult.get("channelOrderNo"));
            result = service4Refund.updateStatus4Success(refundOrderId, channelOrderNo);
            _log.info("更新退款订单状态为成功({}),refundOrderId={},返回结果:{}", PayConstant.REFUND_STATUS_SUCCESS, refundOrderId, result);
            refundOrder.setStatus(PayConstant.REFUND_STATUS_SUCCESS);
            refundOrder.setResult(PayConstant.REFUND_RESULT_SUCCESS);
            // 发送商户通知
            notify4Refund.doNotify(refundOrder, true);
        } else {
            // 更新退款状态为失败
            String channelErrCode = StrUtil.toString(bizResult.get("channelErrCode"));
            String channelErrMsg = StrUtil.toString(bizResult.get("channelErrMsg"));
            result = service4Refund.updateStatus4Fail(refundOrderId, channelErrCode, channelErrMsg);
            refundOrder.setStatus(PayConstant.REFUND_STATUS_FAIL);
            refundOrder.setResult(PayConstant.REFUND_RESULT_FAIL);
            _log.info("更新退款订单状态为失败({}),refundOrderId={},返回结果:{}", PayConstant.REFUND_STATUS_FAIL, refundOrderId, result);
            // 发送商户通知
            notify4Refund.doNotify(refundOrder, true);
        }
    }
}
