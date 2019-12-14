package com.bsd.user.server.service.impl;

import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.service.WechatAuthService;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientDetails;
import org.springframework.stereotype.Service;

/**
 * 微信公众号登录授权服务实现类
 *
 * @Author: linrongxin
 * @Date: 2019/8/23 17:54
 */
@Service
public class WechatGzhXcxAuthServiceImpl extends AbstractWechatAuthServiceImpl implements WechatAuthService {
    /**
     * 配置标识
     */
    public static final String LOGIN_WECHAT_GZH_XCX = "wechat_gzh_xcx";

    /**
     * 返回公众号平台微信授权登录编号
     *
     * @return
     */
    @Override
    public int getPlatform() {
        return UserConstants.PLATFORM_MINIPROGRAM;
    }

    @Override
    public String getConfigTag() {
        return LOGIN_WECHAT_GZH_XCX;
    }

    /**
     * 获取公众号微信登录授权配置信息
     *
     * @return
     */
    @Override
    public OpenOAuth2ClientDetails getClientDetails() {
        return openOAuth2ClientProperties.getOauth2().get(LOGIN_WECHAT_GZH_XCX);
    }
}
