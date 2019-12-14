package com.bsd.payment.server.service;

import com.alibaba.fastjson.JSONObject;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.model.entity.PayOrder;
import com.bsd.payment.server.service.mq.Mq4PayNotify;
import com.bsd.payment.server.util.DateUtils;
import com.bsd.payment.server.util.MyLog;
import com.bsd.payment.server.util.PayDigestUtil;
import com.bsd.payment.server.util.XXPayUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 支付通知处理基类
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
@Component
public class Notify4Pay extends BaseService {
    private static final MyLog _log = MyLog.getLog(Notify4Pay.class);

    @Resource(name = "rabbitMq4PayNotify")
    private Mq4PayNotify mq4PayNotify;

    /**
     * 创建响应URL
     *
     * @param payOrder
     * @param backType 1：前台页面；2：后台接口
     * @return
     */
    public String createNotifyUrl(PayOrder payOrder, String backType) {
        String mchId = payOrder.getMchId();
        MchInfo mchInfo = super.baseSelectMchInfo(mchId);
        String resKey = mchInfo.getResKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrderId", payOrder.getPayOrderId() == null ? "" : payOrder.getPayOrderId());           // 支付订单号
        paramMap.put("mchId", payOrder.getMchId() == null ? "" : payOrder.getMchId());                        // 商户ID
        paramMap.put("mchOrderNo", payOrder.getMchOrderNo() == null ? "" : payOrder.getMchOrderNo());        // 商户订单号
        paramMap.put("channelCode", payOrder.getChannelCode() == null ? "" : payOrder.getChannelCode());              // 渠道编码
        paramMap.put("amount", payOrder.getAmount() == null ? "" : payOrder.getAmount());                        // 支付金额
        paramMap.put("currency", payOrder.getCurrency() == null ? "" : payOrder.getCurrency());                 // 货币类型
        paramMap.put("status", payOrder.getStatus() == null ? "" : payOrder.getStatus());                    // 支付状态
        paramMap.put("clientIp", payOrder.getClientIp() == null ? "" : payOrder.getClientIp());                // 客户端IP
        paramMap.put("device", payOrder.getDevice() == null ? "" : payOrder.getDevice());                        // 设备
        paramMap.put("subject", payOrder.getSubject() == null ? "" : payOrder.getSubject());                        // 商品标题
        paramMap.put("channelOrderNo", payOrder.getChannelOrderNo() == null ? "" : payOrder.getChannelOrderNo()); // 渠道订单号
        paramMap.put("param1", payOrder.getParam1() == null ? "" : payOrder.getParam1());                        // 扩展参数1
        paramMap.put("param2", payOrder.getParam2() == null ? "" : payOrder.getParam2());                        // 扩展参数2
        paramMap.put("paySuccTime", payOrder.getPaySuccTime() == null ? "" : DateUtils.getTimeStrDefault(payOrder.getPaySuccTime()));            // 支付成功时间
        paramMap.put("backType", backType == null ? "" : backType);
        // 先对原文签名
        String reqSign = PayDigestUtil.getSign(paramMap, resKey);
        paramMap.put("sign", reqSign);   // 签名
        // 签名后再对有中文参数编码
        try {
            paramMap.put("device", URLEncoder.encode(payOrder.getDevice() == null ? "" : payOrder.getDevice(), PayConstant.RESP_UTF8));
            paramMap.put("subject", URLEncoder.encode(payOrder.getSubject() == null ? "" : payOrder.getSubject(), PayConstant.RESP_UTF8));
            paramMap.put("param1", URLEncoder.encode(payOrder.getParam1() == null ? "" : payOrder.getParam1(), PayConstant.RESP_UTF8));
            paramMap.put("param2", URLEncoder.encode(payOrder.getParam2() == null ? "" : payOrder.getParam2(), PayConstant.RESP_UTF8));
        } catch (UnsupportedEncodingException e) {
            _log.error("URL Encode exception.", e);
            return null;
        }
        String param = XXPayUtil.genUrlParams(paramMap);
        StringBuffer sb = new StringBuffer();
        sb.append(payOrder.getNotifyUrl()).append("?").append(param);
        return sb.toString();
    }

    /**
     * 处理支付结果后台服务器通知
     */
    public void doNotify(PayOrder payOrder) {
        _log.info(">>>>>> PAY开始回调通知业务系统 <<<<<<");
        // 发起后台通知业务系统
        JSONObject object = createNotifyInfo(payOrder);
        try {
            mq4PayNotify.send(object.toJSONString());
        } catch (Exception e) {
            _log.error("payOrderId={},sendMessage error.", payOrder != null ? payOrder.getPayOrderId() : "", e);
        }
        _log.info(">>>>>> PAY回调通知业务系统完成 <<<<<<");
    }

    public JSONObject createNotifyInfo(PayOrder payOrder) {
        JSONObject object = new JSONObject();
        object.put("method", "GET");
        object.put("url", createNotifyUrl(payOrder, "2"));
        object.put("orderId", payOrder.getPayOrderId());
        object.put("count", payOrder.getNotifyCount());
        object.put("createTime", System.currentTimeMillis());
        return object;
    }
}
