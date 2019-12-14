package com.bsd.user.server.service.impl;

import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.service.WechatAuthService;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信PC oauth2认证实现类
 *
 * @author liuyadu
 */
@Slf4j
@Service("wechatAuthServiceImpl")
public class WechatAuthServiceImpl extends AbstractWechatAuthServiceImpl implements WechatAuthService {
    /**
     * 配置标识
     */
    public static final String LOGIN_WECHAT_PC = "wechat";


    /**
     * 返回PC平台微信授权登录编号
     *
     * @return
     */
    @Override
    public int getPlatform() {
        return UserConstants.PLATFORM_WECHAT_PC;
    }


    @Override
    public String getConfigTag() {
        return LOGIN_WECHAT_PC;
    }

    /**
     * 获取微信PC授权登录配置信息
     *
     * @return
     */
    @Override
    public OpenOAuth2ClientDetails getClientDetails() {
        return openOAuth2ClientProperties.getOauth2().get(LOGIN_WECHAT_PC);
    }
}
