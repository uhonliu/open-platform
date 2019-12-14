package com.bsd.dingtalk.server.util;

import com.bsd.dingtalk.server.constants.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * 钉钉接口封装
 *
 * @author liujianhong
 * @date 2019-06-27
 */
@Slf4j
public class AccessTokenUtil {
    /**
     * 获取登录url
     *
     * @param appkey      appkey
     * @param redirectUri 返回url
     * @return String
     */
    public static String getAuthorizationUrl(String appkey, String redirectUri) {
        return "https://oapi.dingtalk.com/connect/qrconnect?appid=" + appkey + "&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=" + redirectUri;
    }

    /**
     * 获取access_token
     *
     * @param appkey    appkey
     * @param appsecret appsecret
     * @return String
     */
    public static String getToken(String appkey, String appsecret) throws RuntimeException {
        try {
            String accessToken = null;
            DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_TOKKEN);
            OapiGettokenRequest request = new OapiGettokenRequest();

            request.setAppkey(appkey);
            request.setAppsecret(appsecret);
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            if (response.getErrcode() == 0) {
                accessToken = response.getAccessToken();
            }
            return accessToken;
        } catch (ApiException e) {
            log.error("getAccessToken failed", e);
            throw new RuntimeException();
        }
    }

    /**
     * 获取用户ID
     *
     * @param accessToken 访问令牌
     * @param authCode    临时code
     * @return String
     */
    public static String getUserId(String accessToken, String authCode) throws RuntimeException {
        try {
            //获取用户信息
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_INFO);
            OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
            request.setCode(authCode);
            request.setHttpMethod("GET");
            OapiUserGetuserinfoResponse response = client.execute(request, accessToken);
            if (response.getErrcode() != 0) {
                return null;
            }
            return response.getUserid();
        } catch (ApiException e) {
            log.error("getUserId failed", e);
            throw new RuntimeException();
        }
    }
}
