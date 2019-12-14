package com.opencloud.msg.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.opencloud.common.gen.SnowflakeIdGenerator;
import com.opencloud.msg.client.exchange.DelayExchangeBuilder;
import com.opencloud.msg.client.model.WebHookMessage;
import com.opencloud.msg.client.model.entity.WebHookLogs;
import com.opencloud.msg.server.configuration.RabbitConfiguration;
import com.opencloud.msg.server.service.DelayMessageService;
import com.opencloud.msg.server.service.WebHookLogsService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * 消息发送实现类
 *
 * @author liuyadu
 */
@Service
public class DelayMessageServiceImpl implements DelayMessageService {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private WebHookLogsService httpNotifyLogsService;
    @Autowired
    private SnowflakeIdGenerator generator;

    /**
     * 最大延迟不能超过15天
     */
    private final static long MAX_DELAY = 15 * 24 * 3600 * 1000;


    /**
     * 发送延迟消息
     *
     * @param routeKey 路由KEY
     * @param msg      消息内容
     * @param times    延迟时间 毫秒
     */
    @Override
    public void delay(String routeKey, String msg, long times) {
        delay(routeKey, null, msg, times);
    }

    /**
     * 延迟消息放入延迟队列中
     *
     * @param routeKey 路由KEY
     * @param msgId
     * @param msg      消息内容
     * @param times    延迟时间 毫秒
     */
    @Override
    public void delay(String routeKey, String msgId, String msg, long times) {
        if (times > MAX_DELAY) {
            throw new IllegalArgumentException("延迟时间最大不能超过15天");
        }
        String delay = String.valueOf(times);
        amqpTemplate.convertAndSend(DelayExchangeBuilder.DEFAULT_DELAY_EXCHANGE, routeKey, msg, message -> {
            String messageId = msgId;
            if (StringUtils.isEmpty(messageId)) {
                messageId = String.valueOf(generator.nextId());
            }
            message.getMessageProperties().setMessageId(messageId);
            message.getMessageProperties().setTimestamp(new Date());
            message.getMessageProperties().setType("x-delayed-message");
            //添加消息到队列时添加 headers={'x-delay': 8000}
            message.getMessageProperties().setDelay(Integer.parseInt(delay));
            // x-delay 这个版本请求头获取不到, 自定义了一个delay-times 来获取延迟时间
            message.getMessageProperties().setHeader("delay-times", delay);
            return message;
        });
    }

    /**
     * 发送Http通知
     * 首次是即时推送，重试通知时间间隔为 5s、10s、2min、5min、10min、30min、1h、2h、6h、15h，直到你正确回复状态 200 并且返回 success 或者超过最大重发次数
     *
     * @param url  通知地址
     * @param type 通知类型:自定义字符串,可以为空
     * @param data 请求数据
     */
    @Override
    public void send(String url, String type, Map<String, String> data) throws Exception {
        if (StringUtils.isEmpty(url)) {
            throw new Exception("url is not empty");
        }
        if (data == null) {
            data = Maps.newHashMap();
        }
        WebHookMessage msg = new WebHookMessage(url, type, data);
        delay(RabbitConfiguration.HTTP_NOTIFY_QUEUE_RK, JSONObject.toJSONString(msg), 0);
    }

    /**
     * 发送Http通知
     *
     * @param notify
     * @throws Exception
     */
    @Override
    public void send(WebHookMessage notify) throws Exception {
        send(notify.getUrl(), notify.getType(), notify.getData());
    }

    /**
     * 手动重新通知
     *
     * @param msgId
     */
    @Override
    public void send(String msgId) throws Exception {
        WebHookLogs log = httpNotifyLogsService.getLog(msgId);
        if (log == null) {
            throw new Exception("消息msgId={}不存在！");
        }
        Map<String, String> data = JSONObject.parseObject(log.getData(), Map.class);
        WebHookMessage msg = new WebHookMessage(log.getUrl(), log.getType(), data);
        delay(RabbitConfiguration.HTTP_NOTIFY_QUEUE_RK, log.getMsgId(), JSONObject.toJSONString(msg), 0);
    }
}
