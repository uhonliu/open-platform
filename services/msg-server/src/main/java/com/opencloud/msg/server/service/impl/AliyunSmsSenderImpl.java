package com.opencloud.msg.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.opencloud.msg.client.model.BaseMessage;
import com.opencloud.msg.client.model.BatchSmsMessage;
import com.opencloud.msg.client.model.SmsMessage;
import com.opencloud.msg.server.service.SmsSender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author woodev
 */
@Slf4j
public class AliyunSmsSenderImpl implements SmsSender {
    private String accessKeyId;

    private String accessKeySecret;

    private final static String OK = "OK";

    private final static String CODE = "Code";

    public AliyunSmsSenderImpl() {
        log.info("init aliyunSMS sender:" + this);
    }

    @Override
    public Boolean send(BaseMessage baseMessage) {
        if (baseMessage instanceof SmsMessage) {
            SmsMessage smsMessage = (SmsMessage) baseMessage;
            return doSendSms(smsMessage);
        } else if (baseMessage instanceof BatchSmsMessage) {
            BatchSmsMessage batchSmsMessage = (BatchSmsMessage) baseMessage;
            return doSendBatchSms(batchSmsMessage);
        } else {
            return false;
        }
    }

    /**
     * SendBatchSms接口是短信批量发送接口，支持在一次请求中分别向多个不同的手机号码发送不同签名的短信。
     * 手机号码等参数均为JSON格式，字段个数相同，一一对应，短信服务根据字段在JSON中的顺序判断发往指定手机号码的签名。
     *
     * @param batchSmsMessage
     * @return
     */
    private Boolean doSendBatchSms(BatchSmsMessage batchSmsMessage) {
        log.info("SendBatchSms 发送短信开始");
        boolean result = false;
        try {
            // 地域ID
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendBatchSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");

            request.putQueryParameter("PhoneNumberJson", batchSmsMessage.getPhoneNumberJson());
            request.putQueryParameter("SignNameJson", batchSmsMessage.getSignNameJson());
            request.putQueryParameter("TemplateCode", batchSmsMessage.getTemplateCode());
            request.putQueryParameter("TemplateParamJson", batchSmsMessage.getTemplateParamJson());

            CommonResponse response = client.getCommonResponse(request);
            log.info(response.toString());
            JSONObject json = JSONObject.parseObject(response.getData());
            result = OK.equalsIgnoreCase(json.getString(CODE));
            log.info("result:{}", response.getData());
        } catch (Exception e) {
            log.error("发送短信失败：{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * SendSms接口是短信发送接口，支持在一次请求中向多个不同的手机号码发送同样内容的短信。
     *
     * @param smsMessage
     * @return
     */
    private boolean doSendSms(SmsMessage smsMessage) {
        log.info("SendSms 发送短信开始");
        boolean result = false;
        try {
            // 地域ID
            DefaultProfile profile = DefaultProfile.getProfile(
                    "cn-hangzhou",
                    accessKeyId,
                    accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", smsMessage.getPhoneNum());
            request.putQueryParameter("SignName", smsMessage.getSignName());
            request.putQueryParameter("TemplateCode", smsMessage.getTplCode());
            request.putQueryParameter("TemplateParam", smsMessage.getTplParams());
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.toString());
            JSONObject json = JSONObject.parseObject(response.getData());
            result = OK.equalsIgnoreCase(json.getString(CODE));
            log.info("result:{}", response.getData());
        } catch (Exception e) {
            log.error("发送短信失败：{}", e.getMessage(), e);
        }
        return result;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
}
