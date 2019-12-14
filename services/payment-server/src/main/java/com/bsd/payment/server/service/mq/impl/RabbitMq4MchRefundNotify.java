package com.bsd.payment.server.service.mq.impl;

import com.bsd.payment.server.service.mq.Mq4MchRefundNotify;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import static com.bsd.payment.server.service.mq.MqConfig.MCH_REFUND_NOTIFY_EXCHANGE_NAME;
import static com.bsd.payment.server.service.mq.MqConfig.MCH_REFUND_NOTIFY_QUEUE_NAME;

@Component("rabbitMq4MchRefundNotify")
public class RabbitMq4MchRefundNotify extends Mq4MchRefundNotify {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void init() {
        DirectExchange exchange = new DirectExchange(MCH_REFUND_NOTIFY_EXCHANGE_NAME);
        exchange.setDelayed(true);
        Queue queue = new Queue(MCH_REFUND_NOTIFY_QUEUE_NAME);
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
        rabbitTemplate.convertAndSend(MCH_REFUND_NOTIFY_QUEUE_NAME, msg);
    }

    @Override
    public void send(String msg, long delay) {
        _log.info("发送MQ延时消息:msg={},delay={}", msg, delay);
        rabbitTemplate.convertAndSend(MCH_REFUND_NOTIFY_EXCHANGE_NAME, MCH_REFUND_NOTIFY_QUEUE_NAME, msg, message -> {
            message.getMessageProperties().setDelay((int) delay);
            return message;
        });
    }

    @RabbitListener(queues = MCH_REFUND_NOTIFY_QUEUE_NAME)
    public void onMessage(String msg) {
        receive(msg);
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    @Override
    public String httpPost(String url) {
        StringBuffer sb = new StringBuffer();
        try {
            URL console = new URL(url);
            if ("https".equals(console.getProtocol())) {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                        new java.security.SecureRandom());
                HttpsURLConnection con = (HttpsURLConnection) console.openConnection();
                con.setSSLSocketFactory(sc.getSocketFactory());
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(60 * 1000);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()), 1024 * 1024);
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                in.close();
            } else if ("http".equals(console.getProtocol())) {
                HttpURLConnection con = (HttpURLConnection) console.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(60 * 1000);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()), 1024 * 1024);
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                in.close();
            } else {
                _log.error("not do protocol. protocol=%s", console.getProtocol());
            }
        } catch (Exception e) {
            _log.error(e, "httpPost exception. url:%s", url);
        }
        return sb.toString();
    }
}
