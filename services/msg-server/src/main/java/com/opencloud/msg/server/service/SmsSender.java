package com.opencloud.msg.server.service;

import com.opencloud.msg.client.model.BaseMessage;

/**
 * @author woodev
 */
public interface SmsSender {
    /**
     * 发送短信
     *
     * @param parameter
     * @return
     */
    Boolean send(BaseMessage parameter);
}
