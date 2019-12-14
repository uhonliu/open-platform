package com.bsd.payment.server.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * RabbitMq抽象类
 *
 * @Author: linrongxin
 * @Date: 2019/8/30 14:43
 */
@Slf4j
@Component
public abstract class AbstractRabbitMqService implements MqService {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        DirectExchange exchange = new DirectExchange(getExchangeName());
        exchange.setDelayed(true);
        Queue queue = new Queue(getQueueName());
        //绑定exchange 与 queue
        Binding binding = BindingBuilder.bind(queue).to(exchange).withQueueName();
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }

    /**
     * 获取QueueName
     *
     * @return
     */
    protected abstract String getQueueName();


    /**
     * 获取ExchangeName
     *
     * @return
     */
    protected abstract String getExchangeName();


    /**
     * 发送消息
     *
     * @param msg
     */
    @Override
    public void send(String msg) {
        log.info("发送MQ消息:msg={}", msg);
        rabbitTemplate.convertAndSend(getQueueName(), msg);
    }

    /**
     * 发送延迟消息
     *
     * @param msg
     * @param delay
     */
    @Override
    public void send(String msg, long delay) {
        log.info("发送MQ延时消息:msg={},delay={}", msg, delay);
        rabbitTemplate.convertAndSend(getExchangeName(), getQueueName(), msg, message -> {
            message.getMessageProperties().setDelay((int) delay);
            return message;
        });
    }
}
