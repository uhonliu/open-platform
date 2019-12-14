package com.bsd.payment.server.service.mq.impl;

import com.alibaba.fastjson.JSON;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.entity.MchNotify;
import com.bsd.payment.server.service.IMchNotifyService;
import com.bsd.payment.server.service.ITransOrderService;
import com.bsd.payment.server.service.mq.AbstractRabbitMqService;
import com.bsd.payment.server.service.mq.MqConfig;
import com.bsd.payment.server.service.mq.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Date;

/**
 * 商户转账结果通知
 *
 * @Author: linrongxin
 * @Date: 2019/9/2 15:07
 */
@Slf4j
@Service("rabbitMq4MchTransNotifyServiceImpl")
public class RabbitMq4MchTransNotifyServiceImpl extends AbstractRabbitMqService implements MqService {
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private IMchNotifyService mchNotifyServiceImpl;

    @Resource
    private ITransOrderService transOrderServiceImpl;

    /**
     * 延迟间隔时间数组15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h
     */
    private static final long[] INTERVAL_ARRAY = {
            0,
            15,
            15,
            30,
            3 * 60,
            10 * 60,
            20 * 60,
            30 * 60,
            30 * 60,
            30 * 60,
            60 * 60,
            3 * 60 * 60,
            3 * 60 * 60,
            3 * 60 * 60,
            6 * 60 * 60,
            6 * 60 * 60
    };

    @Override
    protected String getQueueName() {
        return MqConfig.MCH_TRANS_NOTIFY_QUEUE_NAME;
    }

    @Override
    protected String getExchangeName() {
        return MqConfig.MCH_TRANS_NOTIFY_EXCHANGE_NAME;
    }

    @Override
    @RabbitListener(queues = MqConfig.MCH_TRANS_NOTIFY_QUEUE_NAME)
    public void receive(String msg) {
        //反序列化数据
        MchNotify mchNotify = JSON.parseObject(msg, MchNotify.class);
        //发送通知请求
        boolean isNotifySuc = doNotifyRequest(mchNotify);
        //通知次数加1
        mchNotify.setNotifyCount((byte) (mchNotify.getNotifyCount().byteValue() + 1));
        //处理通知结果
        if (isNotifySuc) {
            //通知成功
            handleSucNotify(mchNotify);
        } else {
            //通知失败
            handleFailNotify(mchNotify);
        }
    }

    /**
     * 通知失败处理
     *
     * @param mchNotify
     */
    private void handleFailNotify(MchNotify mchNotify) {
        //修改商户转账通知信息为失败
        try {
            int count = mchNotifyServiceImpl.updateMchNotifyStatus(mchNotify.getOrderId(), PayConstant.MCH_NOTIFY_STATUS_FAIL, mchNotify.getResult(), mchNotify.getNotifyCount());
            log.info("更新转账订单通知payOrderId={},结果->{}", mchNotify.getOrderId(), count == 1 ? "成功" : "失败");
        } catch (Exception ex) {
            log.error("更新转账订单通知信息异常:{},msg:{}", mchNotify, ex.getMessage());
        }
        //通知失败超过16次,不再处理
        if (mchNotify.getNotifyCount() >= 16) {
            return;
        }
        //下一次发送的延迟时间
        long delayTime = INTERVAL_ARRAY[mchNotify.getNotifyCount()] * 1000;
        //发送到MQ延迟队列中
        this.send(JSON.toJSONString(mchNotify), delayTime);
    }

    /**
     * 成功通知处理
     *
     * @param mchNotify
     */
    private void handleSucNotify(MchNotify mchNotify) {
        //1.0修改转账订单状态为成功
        try {
            int modifyStatusCount = transOrderServiceImpl.updateStatus4Complete(mchNotify.getOrderId());
            log.info("修改payOrderId={},订单状态为处理完成->{}", mchNotify.getOrderId(), modifyStatusCount == 1 ? "成功" : "失败");
        } catch (Exception ex) {
            log.error("修改订单状态为处理完成异常:{}", ex.getMessage());
        }
        //2.0修改商户转账通知信息为成功
        try {
            int count = mchNotifyServiceImpl.updateMchNotifyStatus(mchNotify.getOrderId(), PayConstant.MCH_NOTIFY_STATUS_SUCCESS, mchNotify.getResult(), mchNotify.getNotifyCount());
            log.info("更新转账订单通知payOrderId={},结果->{}", mchNotify.getOrderId(), count == 1 ? "成功" : "失败");
        } catch (Exception ex) {
            log.error("更新转账订单通知信息异常:{},msg:{}", mchNotify, ex.getMessage());
        }
    }

    /**
     * 通知请求
     *
     * @param mchNotify
     * @return
     */
    private boolean doNotifyRequest(MchNotify mchNotify) {
        //设置最后一次通知时间
        mchNotify.setLastNotifyTime(new Date());
        log.info("商户转账异步通知开始,orderId:{},cout:{}", mchNotify.getOrderId(), mchNotify.getNotifyCount());
        try {
            URI uri = new URI(mchNotify.getNotifyUrl());
            String result = restTemplate.postForObject(uri, null, String.class);
            log.info("商户转账结果通知请求url:{},orderId:{},cout:{},result:{}", mchNotify.getNotifyUrl(), mchNotify.getOrderId(), mchNotify.getNotifyCount(), result);
            if ("success".equalsIgnoreCase(result.trim())) {
                mchNotify.setResult(result);
                return true;
            }
        } catch (Exception e) {
            mchNotify.setResult(e.getMessage());
            log.error("商户转账结果通知异常:{}", e.getMessage());
        }
        log.info("商户转账异步通知结束,orderId:{},cout:{}", mchNotify.getOrderId(), mchNotify.getNotifyCount());
        return false;
    }
}
