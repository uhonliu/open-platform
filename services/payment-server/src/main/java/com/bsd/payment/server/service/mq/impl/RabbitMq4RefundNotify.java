package com.bsd.payment.server.service.mq.impl;

import com.bsd.payment.server.service.mq.Mq4RefundNotify;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.bsd.payment.server.service.mq.MqConfig.REFUND_NOTIFY_EXCHANGE_NAME;
import static com.bsd.payment.server.service.mq.MqConfig.REFUND_NOTIFY_QUEUE_NAME;

@Component("rabbitMq4RefundNotify")
public class RabbitMq4RefundNotify extends Mq4RefundNotify {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void init() {
        DirectExchange exchange = new DirectExchange(REFUND_NOTIFY_EXCHANGE_NAME);
        exchange.setDelayed(true);
        Queue queue = new Queue(REFUND_NOTIFY_QUEUE_NAME);
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
        rabbitTemplate.convertAndSend(REFUND_NOTIFY_QUEUE_NAME, msg);
    }

    @Override
    public void send(String msg, long delay) {
        _log.info("发送MQ延时消息:msg={},delay={}", msg, delay);
        rabbitTemplate.convertAndSend(REFUND_NOTIFY_EXCHANGE_NAME, REFUND_NOTIFY_QUEUE_NAME, msg, message -> {
            message.getMessageProperties().setDelay((int) delay);
            return message;
        });
    }

    @RabbitListener(queues = REFUND_NOTIFY_QUEUE_NAME)
    public void onMessage(String msg) {
        receive(msg);
    }
}
