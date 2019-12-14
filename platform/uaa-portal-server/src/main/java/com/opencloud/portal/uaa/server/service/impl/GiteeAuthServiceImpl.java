package com.opencloud.portal.uaa.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.opencloud.common.security.http.OpenRestTemplate;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientDetails;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientProperties;
import com.opencloud.common.security.oauth2.client.OpenOAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信oauth2认证实现类
 *
 * @author liuyadu
 */
@Service("giteeService")
@Slf4j
public class GiteeAuthServiceImpl implements OpenOAuth2Service {
    @Autowired
    private OpenRestTemplate restTemplate;
    @Autowired
    private OpenOAuth2ClientProperties openOAuth2ClientProperties;
    /**
     * 微信 登陆页面的URL
     */
    private final static String AUTHORIZATION_URL = "%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";
    /**
     * 获取token的URL
     */
    private final static String ACCESS_TOKEN_URL = "%s?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";

    /**
     * 获取用户信息的 URL，oauth_consumer_key 为 apiKey
     */
    private static final String USER_INFO_URL = "%s?access_token=%s";


    @Override
    public String getAuthorizationUrl() {
        String url = String.format(AUTHORIZATION_URL, getClientDetails().getUserAuthorizationUri(), getClientDetails().getClientId(), getClientDetails().getRedirectUri(), getClientDetails().getScope(), System.currentTimeMillis());
        return url;
    }

    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESS_TOKEN_URL, getClientDetails().getAccessTokenUri(), getClientDetails().getClientId(), getClientDetails().getClientSecret(), code, getClientDetails().getRedirectUri());
        Map data = Maps.newHashMap();
        String resp = restTemplate.postForObject(url, data, String.class);
        if (resp != null && resp.contains("access_token")) {
            JSONObject jsonObject = JSONObject.parseObject(resp);
            return jsonObject.getString("access_token");
        }
        log.error("Gitee获得access_token失败，code无效，resp:{}", resp);
        return null;
    }

    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public JSONObject getUserInfo(String accessToken, String openId) {
        String url = String.format(USER_INFO_URL, getClientDetails().getUserInfoUri(), accessToken);
        String resp = restTemplate.getForObject(url, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        return data;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }

    /**
     * 获取登录成功地址
     *
     * @return
     */
    @Override
    public String getLoginSuccessUrl() {
        return getClientDetails().getLoginSuccessUri();
    }

    /**
     * 获取客户端配置信息
     *
     * @return
     */
    @Override
    public OpenOAuth2ClientDetails getClientDetails() {
        return openOAuth2ClientProperties.getOauth2().get("gitee");
    }
}
