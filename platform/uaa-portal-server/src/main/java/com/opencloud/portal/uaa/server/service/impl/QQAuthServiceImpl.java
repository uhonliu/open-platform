package com.opencloud.portal.uaa.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.opencloud.common.security.http.OpenRestTemplate;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientDetails;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientProperties;
import com.opencloud.common.security.oauth2.client.OpenOAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * QQ互联oauth2认证实现类
 *
 * @author liuyadu
 */
@Service("qqAuthService")
@Slf4j
public class QQAuthServiceImpl implements OpenOAuth2Service {
    @Autowired
    private OpenRestTemplate restTemplate;
    @Autowired
    private OpenOAuth2ClientProperties openOAuth2ClientProperties;
    /**
     * QQ 登陆页面的URL
     */
    private final static String AUTHORIZATION_URL = "%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";
    /**
     * 获取token的URL
     */
    private final static String ACCESS_TOKEN_URL = "%s?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";
    /**
     * 获取用户 openid 的 URL
     */
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s";
    /**
     * 获取用户信息的 URL，oauth_consumer_key 为 apiKey
     */
    private static final String USER_INFO_URL = "%s?access_token=%s&oauth_consumer_key=%s&openid=%s";


    @Override
    public String getAuthorizationUrl() {
        String url = String.format(AUTHORIZATION_URL, getClientDetails().getUserAuthorizationUri(), getClientDetails().getClientId(), getClientDetails().getRedirectUri(), getClientDetails().getScope());
        return url;
    }

    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESS_TOKEN_URL, getClientDetails().getAccessTokenUri(), getClientDetails().getClientId(), getClientDetails().getClientSecret(), code, getClientDetails().getRedirectUri());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = restTemplate.getForObject(uri, String.class);
        if (resp != null && resp.contains("access_token")) {
            Map<String, String> map = getParam(resp);
            String access_token = map.get("access_token");
            return access_token;
        }
        log.error("QQ获得access_token失败，code无效，resp:{}", resp);
        return null;
    }

    /**
     * 由于QQ的几个接口返回类型不一样，此处是获取key-value类型的参数
     *
     * @param string
     * @return
     */
    private Map<String, String> getParam(String string) {
        Map<String, String> map = new HashMap();
        String[] kvArray = string.split("&");
        for (int i = 0; i < kvArray.length; i++) {
            String[] kv = kvArray[i].split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    /**
     * QQ接口返回类型是text/plain，此处将其转为json
     *
     * @param string
     * @return
     */
    private JSONObject convertToJson(String string) {
        string = string.substring(string.indexOf("(") + 1, string.length());
        string = string.substring(0, string.indexOf(")"));
        JSONObject jsonObject = JSONObject.parseObject(string);
        return jsonObject;
    }

    @Override
    public String getOpenId(String accessToken) {
        String url = String.format(OPEN_ID_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = restTemplate.getForObject(uri, String.class);
        if (resp != null && resp.contains("openid")) {
            JSONObject jsonObject = convertToJson(resp);
            String openid = jsonObject.getString("openid");
            return openid;
        }
        log.error("QQ获得openid失败，accessToken无效，resp:{}", resp);
        return null;
    }

    @Override
    public JSONObject getUserInfo(String accessToken, String openId) {
        String url = String.format(USER_INFO_URL, getClientDetails().getUserInfoUri(), accessToken, getClientDetails().getClientId(), openId);
        String resp = restTemplate.getForObject(url, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        return data;
    }

    /**
     * 获取客户端配置信息
     *
     * @return
     */
    @Override
    public OpenOAuth2ClientDetails getClientDetails() {
        return openOAuth2ClientProperties.getOauth2().get("qq");
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
}
