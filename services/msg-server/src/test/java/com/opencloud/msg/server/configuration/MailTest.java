package com.opencloud.msg.server.configuration;

import com.opencloud.common.test.BaseTest;
import com.opencloud.msg.client.model.EmailMessage;
import com.opencloud.msg.server.dispatcher.MessageDispatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: liuyadu
 * @date: 2018/11/27 14:45
 * @description:
 */
public class MailTest extends BaseTest {
    @Autowired
    private MessageDispatcher dispatcher;

    @Test
    public void testMail() {
        EmailMessage message = new EmailMessage();
        message.setTo(new String[]{"515608851@qq.com"});
        message.setSubject("测试");
        message.setContent("测试内容");
        this.dispatcher.dispatch(message);
        try {
            Thread.sleep(50000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
