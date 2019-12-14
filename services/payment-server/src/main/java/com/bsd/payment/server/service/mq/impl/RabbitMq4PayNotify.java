package com.bsd.payment.server.service.mq.impl;

import com.bsd.payment.server.service.mq.Mq4PayNotify;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.bsd.payment.server.service.mq.MqConfig.PAY_NOTIFY_EXCHANGE_NAME;
import static com.bsd.payment.server.service.mq.MqConfig.PAY_NOTIFY_QUEUE_NAME;

@Component("rabbitMq4PayNotify")
public class RabbitMq4PayNotify extends Mq4PayNotify {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void init() {
        DirectExchange exchange = new DirectExchange(PAY_NOTIFY_EXCHANGE_NAME);
        exchange.setDelayed(true);
        Queue queue = new Queue(PAY_NOTIFY_QUEUE_NAME);
        Binding binding = BindingBuilder.bind(queue).to(exchange).withQueueName();
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Override
    public void send(String msg) {
        _log.info("发送MQ消息:msg={}", msg);
        rabbitTemplate.convertAndSend(PAY_NOTIFY_QUEUE_NAME, msg);
    }

    @Override
    public void send(String msg, long delay) {
        _log.info("发送MQ延时消息:msg={},delay={}", msg, delay);
        rabbitTemplate.convertAndSend(PAY_NOTIFY_EXCHANGE_NAME, PAY_NOTIFY_QUEUE_NAME, msg, message -> {
            message.getMessageProperties().setDelay((int) delay);
            return message;
        });
    }

    @RabbitListener(queues = PAY_NOTIFY_QUEUE_NAME)
    public void onMessage(String msg) {
        receive(msg);
    }
}
