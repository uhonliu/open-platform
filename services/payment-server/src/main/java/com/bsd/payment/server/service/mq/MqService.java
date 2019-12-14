package com.bsd.payment.server.service.mq;

/**
 * MQ接口类
 *
 * @Author: linrongxin
 * @Date: 2019/8/30 14:40
 */
public interface MqService {
    /**
     * 发送MQ消息
     *
     * @param msg
     */
    void send(String msg);

    /**
     * 发送MQ延迟消息
     *
     * @param msg
     * @param delay
     */
    void send(String msg, long delay);

    /**
     * 接收MQ消息
     *
     * @param msg
     */
    void receive(String msg);
}
