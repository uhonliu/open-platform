package com.bsd.user.server.service;

import com.alibaba.fastjson.JSONObject;
import com.bsd.user.server.model.dto.JsSdkSignDTO;

/**
 * 开放平台服务
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 15:05
 */
public interface DeveloperService {
    /**
     * 微信js-sdk授权地址签名
     *
     * @param url
     * @return
     */
    JsSdkSignDTO makeWxJsSdkSign(String url);

    /**
     * 获取微信小程序AccessToken
     *
     * @param platform
     * @return
     */
    JSONObject authGetAccessToken(Integer platform);
}
