package com.bsd.user.server.service;

import com.alibaba.fastjson.JSONObject;
import com.opencloud.common.security.oauth2.client.OpenOAuth2Service;

/**
 * 微信授权服务接口
 *
 * @Author: linrongxin
 * @Date: 2019/8/23 17:27
 */
public interface WechatAuthService extends OpenOAuth2Service {
    JSONObject getAccessTokenResult(String code);

    int getPlatform();

    String getConfigTag();

    /**
     * 微信小程序登录
     *
     * @param code
     * @return
     */
    JSONObject authCode2Session(String code);
}
