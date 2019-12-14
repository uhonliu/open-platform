package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.mapper.MchInfoMapper;
import com.bsd.payment.server.mapper.MchNotifyMapper;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.model.entity.MchNotify;
import com.bsd.payment.server.model.entity.TransOrder;
import com.bsd.payment.server.service.INotifyTransService;
import com.bsd.payment.server.service.mq.MqService;
import com.bsd.payment.server.util.DateUtils;
import com.bsd.payment.server.util.PayDigestUtil;
import com.bsd.payment.server.util.XXPayUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * @Author: linrongxin
 * @Date: 2019/9/2 15:22
 */
@Slf4j
@Service
public class NotifyTransServiceImpl implements INotifyTransService {
    @Resource
    private MchNotifyMapper mchNotifyMapper;

    @Resource
    private MchInfoMapper mchInfoMapper;

    @Resource(name = "rabbitMq4MchTransNotifyServiceImpl")
    private MqService rabbitMq4MchTransNotifyServiceImpl;

    @Override
    public void doNotify(TransOrder transOrder, boolean isFirst) {
        MchNotify mchNotify = createMchNotify(transOrder, isFirst);
        if (mchNotify != null) {
            rabbitMq4MchTransNotifyServiceImpl.send(JSON.toJSONString(mchNotify));
        }
    }

    /**
     * 创建商户通知信息
     *
     * @param transOrder
     * @return
     */
    private MchNotify createMchNotify(TransOrder transOrder, boolean isFirst) {
        if (isFirst) {
            MchNotify mchNotify = new MchNotify();
            mchNotify.setOrderId(transOrder.getTransOrderId());//平台转账订单号
            mchNotify.setMchId(transOrder.getMchId());//商户ID
            mchNotify.setMchOrderNo(transOrder.getMchTransNo());//商户订单号
            mchNotify.setOrderType(PayConstant.MCH_NOTIFY_TYPE_TRANS);//商户通知类型:支付订单
            mchNotify.setNotifyUrl(createNotifyUrl(transOrder));//通知URL
            mchNotify.setNotifyCount((byte) 0);//通知次数0
            mchNotify.setCreateTime(new Date());
            int count = mchNotifyMapper.insertSelective(mchNotify);
            if (count <= 0) {
                return null;
            }
            return mchNotify;
        } else {
            return mchNotifyMapper.selectByPrimaryKey(transOrder.getTransOrderId());
        }
    }

    private String createNotifyUrl(TransOrder transOrder) {
        MchInfo mchInfo = mchInfoMapper.selectByPrimaryKey(transOrder.getMchId());
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("transOrderId", ObjectUtils.defaultIfNull(transOrder.getTransOrderId(), ""));            // 转账订单号
        paramMap.put("mchId", ObjectUtils.defaultIfNull(transOrder.getMchId(), ""));                            // 商户ID
        paramMap.put("mchOrderNo", ObjectUtils.defaultIfNull(transOrder.getMchTransNo(), ""));                // 商户订单号
        paramMap.put("amount", ObjectUtils.defaultIfNull(transOrder.getAmount(), ""));                        // 支付金额
        paramMap.put("currency", ObjectUtils.defaultIfNull(transOrder.getCurrency(), ""));                    // 货币类型
        paramMap.put("status", ObjectUtils.defaultIfNull(transOrder.getStatus(), ""));                        // 转账状态
        paramMap.put("result", ObjectUtils.defaultIfNull(transOrder.getResult(), ""));                        // 转账结果
        paramMap.put("clientIp", ObjectUtils.defaultIfNull(transOrder.getClientIp(), ""));                    // 客户端IP
        paramMap.put("device", ObjectUtils.defaultIfNull(transOrder.getDevice(), ""));                        // 设备
        paramMap.put("param1", ObjectUtils.defaultIfNull(transOrder.getParam1(), ""));                        // 扩展参数1
        paramMap.put("param2", ObjectUtils.defaultIfNull(transOrder.getParam2(), ""));                        // 扩展参数2
        String transSuccTimeStr = transOrder.getTransSuccTime() != null ? DateUtils.getTimeStrDefault(transOrder.getTransSuccTime()) : "";//转账成功时间
        paramMap.put("transSuccTime", ObjectUtils.defaultIfNull(transSuccTimeStr, ""));            // 转账成功时间
        // 先对原文签名
        String reqSign = PayDigestUtil.getSign(paramMap, mchInfo.getResKey());
        paramMap.put("sign", reqSign);   // 签名
        // 签名后再对有中文参数编码
        try {
            paramMap.put("device", URLEncoder.encode(ObjectUtils.defaultIfNull(transOrder.getDevice(), ""), PayConstant.RESP_UTF8));
            paramMap.put("param1", URLEncoder.encode(ObjectUtils.defaultIfNull(transOrder.getParam1(), ""), PayConstant.RESP_UTF8));
            paramMap.put("param2", URLEncoder.encode(ObjectUtils.defaultIfNull(transOrder.getParam2(), ""), PayConstant.RESP_UTF8));
        } catch (UnsupportedEncodingException e) {
            log.error("URL Encode exception.", e);
            return null;
        }
        String param = XXPayUtil.genUrlParams(paramMap);
        StringBuffer sb = new StringBuffer();
        sb.append(transOrder.getNotifyUrl()).append("?").append(param);
        return sb.toString();
    }
}
