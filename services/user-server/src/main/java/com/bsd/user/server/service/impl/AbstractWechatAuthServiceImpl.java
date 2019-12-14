package com.bsd.user.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bsd.user.server.service.WechatAuthService;
import com.opencloud.common.security.http.OpenRestTemplate;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 微信授权服务抽象类
 *
 * @Author: linrongxin
 * @Date: 2019/8/23 17:32
 */
@Slf4j
@Service
abstract class AbstractWechatAuthServiceImpl implements WechatAuthService {
    @Autowired
    protected OpenOAuth2ClientProperties openOAuth2ClientProperties;

    /**
     * 微信 登陆页面的URL
     */
    private final static String AUTHORIZATION_URL = "%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
    /**
     * 获取token的URL
     */
    private final static String ACCESS_TOKEN_URL = "%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    /**
     * 微信小程序登录凭证校验
     */
    private final static String AUTH_CODE2SESSION_URL = "%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 获取用户信息的 URL，oauth_consumer_key 为 apiKey
     */
    private static final String USER_INFO_URL = "%s?access_token=%s&openid=%s&lang=zh_CN";


    @Autowired
    private OpenRestTemplate restTemplate;

    /**
     * 根据回调的code获取accessToken结果集
     *
     * @param code
     * @return
     */
    @Override
    public JSONObject getAccessTokenResult(String code) {
        JSONObject result = (JSONObject) getAccessTokenInfo(code, false);
        return result;
    }

    /**
     * 根据回调的code获取accessToken
     *
     * @param code code
     * @return
     */
    @Override
    public String getAccessToken(String code) {
        String access_token = (String) getAccessTokenInfo(code, true);
        return access_token;
    }


    public Object getAccessTokenInfo(String code, boolean onlyGetAccessToken) {
        String url = String.format(ACCESS_TOKEN_URL, getClientDetails().getAccessTokenUri(), getClientDetails().getClientId(), getClientDetails().getClientSecret(), code);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = restTemplate.getForObject(uri, String.class);
        if (resp != null && resp.contains("access_token")) {
            JSONObject map = JSONObject.parseObject(resp);
            if (onlyGetAccessToken) {
                String access_token = map.getString("access_token");
                return access_token;
            }
            return map;
        }
        log.error("微信获得access_token失败，code无效，resp:{}", resp);
        return null;
    }


    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    /**
     * 获取唤起微信登录授权地址
     *
     * @return
     */
    @Override
    public String getAuthorizationUrl() {
        String url = String.format(AUTHORIZATION_URL, getClientDetails().getUserAuthorizationUri(),
                getClientDetails().getClientId(), getClientDetails().getRedirectUri(), getClientDetails().getScope(),
                System.currentTimeMillis());
        return url;
    }

    @Override
    public JSONObject getUserInfo(String accessToken, String openId) {
        String url = String.format(USER_INFO_URL, getClientDetails().getUserInfoUri(), accessToken, openId);
        String resp = restTemplate.getForObject(url, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        return data;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }

    @Override
    public String getLoginSuccessUrl() {
        return getClientDetails().getLoginSuccessUri();
    }

    @Override
    public JSONObject authCode2Session(String code) {
        String url = String.format(AUTH_CODE2SESSION_URL, getClientDetails().getAuthCode2sessionUri(), getClientDetails().getClientId(), getClientDetails().getClientSecret(), code);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = restTemplate.getForObject(uri, String.class);
        log.error("微信小程序登录凭证校验结果，code:{}，resp:{}", code, resp);
        JSONObject map = null;
        if (resp != null && resp.contains("openid")) {
            map = JSONObject.parseObject(resp);
        }
        return map;
    }
}
