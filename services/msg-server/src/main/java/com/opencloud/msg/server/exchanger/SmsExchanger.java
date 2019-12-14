package com.opencloud.msg.server.exchanger;

import com.opencloud.msg.client.model.BaseMessage;
import com.opencloud.msg.client.model.BatchSmsMessage;
import com.opencloud.msg.client.model.SmsMessage;
import com.opencloud.msg.server.service.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * @author woodev
 */
@Slf4j
public class SmsExchanger implements MessageExchanger {
    private SmsSender smsSender;

    public SmsExchanger(SmsSender smsSender) {
        if (smsSender != null) {
            log.info("init sms sender");
        }
        this.smsSender = smsSender;
    }

    private String signName = "签名";

    @Override
    public boolean support(Object message) {
        //调用SendSms发送短信
        if (message instanceof SmsMessage) {
            return true;
        }
        //调用SendBatchSms接口批量发送短信
        if (message instanceof BatchSmsMessage) {
            return true;
        }
        return false;
    }

    @Override
    public boolean exchange(BaseMessage message) {
        Assert.notNull(smsSender, "短信接口没有初始化");
        return smsSender.send(message);
    }
}
