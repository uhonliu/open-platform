package com.opencloud.msg.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.msg.client.model.BatchSmsMessage;
import com.opencloud.msg.client.model.SmsMessage;
import com.opencloud.msg.client.service.ISmsClient;
import com.opencloud.msg.server.dispatcher.MessageDispatcher;
import com.opencloud.msg.server.utils.RegexUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推送通知
 *
 * @author woodev
 */
@RestController
@Api(value = "短信", tags = "短信")
public class SmsController implements ISmsClient {
    @Autowired
    private MessageDispatcher dispatcher;

    /**
     * 短信通知
     * smsMessage
     *
     * @return
     */
    @ApiOperation(value = "发送短信", notes = "发送短信")
    @PostMapping(value = "/sms")
    @Override
    public ResultBody<String> send(@RequestBody SmsMessage smsMessage) {
        this.dispatcher.dispatch(smsMessage);
        return ResultBody.ok();
    }

    @Override
    @PostMapping(value = "/sms/feign", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResultBody feignSendSms(SmsMessage message) {
        this.dispatcher.dispatch(message);
        return ResultBody.ok();
    }

    /**
     * 批量发送短信
     *
     * @param batchSmsMessage
     * @return
     */
    @ApiOperation(value = "批量发送短信", notes = "SendBatchSms接口是短信批量发送接口，支持在一次请求中分别向多个不同的手机号码发送不同签名的短信")
    @PostMapping(value = "/sms/batch")
    public ResultBody sendBatchSms(BatchSmsMessage batchSmsMessage) {
        boolean isCheck = checkBatchSmsMessage(batchSmsMessage);
        if (!isCheck) {
            ResultBody.failed().msg("参数校验存在错误");
        }
        //转发批量发送短信请求到线程池中
        this.dispatcher.dispatch(batchSmsMessage);
        return ResultBody.ok();
    }

    /**
     * 校验批量发送短信错误
     *
     * @param batchSmsMessage
     * @return
     */
    private boolean checkBatchSmsMessage(BatchSmsMessage batchSmsMessage) {
        /**
         * 非空校验
         */
        //号码json传非空校验
        int phoneArraySzie = 0;
        String phoneNumberJson = batchSmsMessage.getPhoneNumberJson();
        try {
            JSONArray phoneArray = JSON.parseArray(phoneNumberJson);
            if (StringUtils.isEmpty(phoneNumberJson) || phoneArray == null || phoneArray.size() == 0) {
                throw new OpenAlertException("phoneNumberJson json数组不能为空");
            }
            //号码格式检查
            if (phoneArray != null && phoneArray.size() > 0) {
                for (int i = 0; i < phoneArray.size(); i++) {
                    String phone = phoneArray.get(i).toString();
                    boolean isPhoneNum = RegexUtils.isMobileExact(phone);
                    if (!isPhoneNum) {
                        throw new OpenAlertException("phoneNumberJson中存在号码格式错误");
                    }
                }
            }
            phoneArraySzie = phoneArray.size();
        } catch (Exception ex) {
            throw new OpenAlertException("phoneNumberJson json格式错误");
        }

        int signArraySize = 0;
        try {
            //签名json数组串
            String signNameJson = batchSmsMessage.getSignNameJson();
            JSONArray signArray = JSON.parseArray(signNameJson);
            if (StringUtils.isEmpty(signNameJson) || signArray == null || signArray.size() == 0) {
                throw new OpenAlertException("signNameJson json数组不能为空");
            }
            signArraySize = signArray.size();
        } catch (Exception ex) {
            throw new OpenAlertException("signNameJson json格式错误");
        }

        //短信模板CODE
        String templateCode = batchSmsMessage.getTemplateCode();
        if (StringUtils.isEmpty(templateCode)) {
            throw new OpenAlertException("templateCode参数不能为空");
        }

        int templateParamArraySize = 0;
        try {
            //短信模板变量对应的实际值
            String templateParamJson = batchSmsMessage.getTemplateParamJson();
            JSONArray templateParamArray = JSON.parseArray(templateParamJson);
            if (StringUtils.isEmpty(templateParamJson) || templateParamArray == null || templateParamArray.size() == 0) {
                throw new OpenAlertException("templateParamJson json数组不能为空");
            }
            templateParamArraySize = templateParamArray.size();
        } catch (Exception ex) {
            throw new OpenAlertException("templateParamJson json格式错误");
        }

        //长度校验检查
        if (!(phoneArraySzie == signArraySize && signArraySize == templateParamArraySize)) {
            throw new OpenAlertException("号码json数组,签名json数组,短信模板变量数组,长度不一致");
        }

        return true;
    }
}
