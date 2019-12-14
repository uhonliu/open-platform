package com.bsd.user.server.constants;

/**
 * 开发平台常量
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 14:41
 */
public class DeveloperConstants {
    /**
     * ticket缓存redis前缀
     */
    public static final String APP_JSAPI_TICKET_REDIS_PREFIX = "dev:wx:jsapi:ticket:appid:";
    /**
     * js-sdk获取token url
     */
    public static final String JSAPI_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * js-sdk获取ticket url
     */
    public static final String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    /**
     * 获取js-sdk ticket 锁 key
     */
    public static final String TICKET_LOCK_KEY = "TICKET_LOCK_KEY";

    /**
     * 获取js-sdk ticket 锁 自动释放时间(时间秒)
     */
    public static final int TICKET_LOCK_RELEASE_TIME = 3;

    /**
     * http 前缀
     */
    public static final String HTTP_PREFIX = "http://";
    /**
     * https 前缀
     */
    public static final String HTTPS_PREFIX = "https://";

    /**
     * 获取小程序全局唯一后台接口调用凭据（access_token）
     */
    public static final String AUTH_GETACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * 微信小程序accesstoken 存入redis 前缀
     */
    public static final String AUTH_GETACCESSTOKEN_REDIS_PREFIX = "wechat_gzh_xcx_accesstoken_";
}
