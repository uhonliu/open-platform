package com.bsd.dingtalk.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsd.dingtalk.server.configuration.DingtalkProperties;
import com.bsd.dingtalk.server.util.AccessTokenUtil;
import com.bsd.dingtalk.server.util.MessageHelper;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackDeleteCallBackRequest;
import com.dingtalk.api.request.OapiCallBackRegisterCallBackRequest;
import com.dingtalk.api.response.OapiCallBackRegisterCallBackResponse;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.dingtalk.oapi.lib.aes.Utils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 回调信息处理
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Slf4j
@Api(tags = "回调信息处理")
@EnableConfigurationProperties({DingtalkProperties.class})
@RestController
public class CallbackController {
    @Autowired
    private DingtalkProperties dingtalkProperties;

    /**
     * 创建套件后，验证回调URL创建有效事件（第一次保存回调URL之前）
     */
    private static final String CHECK_URL = "check_url";

    /**
     * 审批任务回调
     */
    private static final String BPMS_TASK_CHANGE = "bpms_task_change";

    /**
     * 审批实例回调
     */
    private static final String BPMS_INSTANCE_CHANGE = "bpms_instance_change";

    /**
     * 相应钉钉回调时的值
     */
    private static final String CALLBACK_RESPONSE_SUCCESS = "success";

    @ApiOperation(value = "审批回调", notes = "审批回调")
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> callback(@RequestParam(value = "signature", required = false) String signature,
                                        @RequestParam(value = "timestamp", required = false) String timestamp,
                                        @RequestParam(value = "nonce", required = false) String nonce,
                                        @RequestBody(required = false) JSONObject json) {
        String params = " signature:" + signature + " timestamp:" + timestamp + " nonce:" + nonce + " json:" + json;
        try {
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(dingtalkProperties.getToken(), dingtalkProperties.getEncodingAesKey(), dingtalkProperties.getCorpid());

            //从post请求的body中获取回调信息的加密数据进行解密处理
            String encryptMsg = json.getString("encrypt");
            String plainText = dingTalkEncryptor.getDecryptMsg(signature, timestamp, nonce, encryptMsg);
            JSONObject obj = JSON.parseObject(plainText);

            //根据回调数据类型做不同的业务处理
            String eventType = obj.getString("EventType");
            if (BPMS_TASK_CHANGE.equals(eventType)) {
                log.info("收到审批任务进度更新: " + plainText);
                //todo: 实现审批的业务逻辑，如发消息
            } else if (BPMS_INSTANCE_CHANGE.equals(eventType)) {
                log.info("收到审批实例状态更新: " + plainText);
                //todo: 实现审批的业务逻辑，如发消息
                Map<String, String> map = Maps.newHashMap();
                map.put("content", "123");
                String processInstanceId = obj.getString("processInstanceId");
                if (obj.containsKey("result") && "agree".equals(obj.getString("result"))) {
                    MessageHelper.sendMessageToOriginator(AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret()), Long.valueOf(dingtalkProperties.getAgentid()), processInstanceId, map);
                }
            } else {
                // 其他类型事件处理
            }

            // 返回success的加密信息表示回调处理成功
            return dingTalkEncryptor.getEncryptedMap(CALLBACK_RESPONSE_SUCCESS, System.currentTimeMillis(), Utils.getRandomStr(8));
        } catch (Exception e) {
            //失败的情况，应用的开发者应该通过告警感知，并干预修复
            log.error("process callback failed！" + params, e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        DingtalkProperties dingtalkProperties = new DingtalkProperties();
        String accessToken = AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret());

        // 先删除企业已有的回调
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/delete_call_back");
        OapiCallBackDeleteCallBackRequest request = new OapiCallBackDeleteCallBackRequest();
        request.setHttpMethod("GET");
        client.execute(request, accessToken);

        // 重新为企业注册回调
        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/register_call_back");
        OapiCallBackRegisterCallBackRequest registerRequest = new OapiCallBackRegisterCallBackRequest();
        registerRequest.setUrl(dingtalkProperties.getCallbackUrlHost() + "/callback");
        registerRequest.setAesKey(dingtalkProperties.getEncodingAesKey());
        registerRequest.setToken(dingtalkProperties.getToken());
        registerRequest.setCallBackTag(Arrays.asList("bpms_instance_change", "bpms_task_change"));
        OapiCallBackRegisterCallBackResponse registerResponse = client.execute(registerRequest, accessToken);
        if (registerResponse.isSuccess()) {
            System.out.println("回调注册成功了！！！");
        }
    }
}
