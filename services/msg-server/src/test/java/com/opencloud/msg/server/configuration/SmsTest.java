package com.opencloud.msg.server.configuration;

import com.opencloud.common.test.BaseTest;
import com.opencloud.msg.client.model.SmsMessage;
import com.opencloud.msg.server.dispatcher.MessageDispatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: liuyadu
 * @date: 2018/11/27 14:45
 * @description:
 */
public class SmsTest extends BaseTest {
    @Autowired
    private MessageDispatcher dispatcher;

    @Test
    public void testSms() {
        SmsMessage smsNotify = new SmsMessage();
        smsNotify.setPhoneNum("18510152531");
        smsNotify.setSignName("测试");
        smsNotify.setTplCode("测试内容");
        this.dispatcher.dispatch(smsNotify);
        try {
            Thread.sleep(50000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
