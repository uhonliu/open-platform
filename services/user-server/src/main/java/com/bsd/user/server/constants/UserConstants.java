package com.bsd.user.server.constants;

/**
 * 用户中心常量定义类
 *
 * @author lisongmao
 * 2019年6月29日
 */
public class UserConstants {
    /**
     * 服务名称
     */
    public static final String BASE_SERVICE = "user-server";

    /**
     * 手机验证码存入redis中key的前缀
     */
    public static final String MOBILE_CODE_PRE = "user-mobile-code";
    /**
     * 手机验证码的失效时间(秒)
     */
    public static final Long MOBILE_CODE_TIME = 5 * 60L;
    /**
     * 手机验证码类型，1-注册验证码 2-登录验证码 3-忘记密码 4-微信绑定验证码 5-更换手机旧手机验证 6-更换手机新号码验证
     */
    public static final Integer[] MOBILE_CODE_TYPE_ALL = {1, 2, 3, 4, 5, 6};
    public static final int MOBILE_CODE_TYPE1 = 1;
    public static final int MOBILE_CODE_TYPE2 = 2;
    public static final int MOBILE_CODE_TYPE3 = 3;
    public static final int MOBILE_CODE_TYPE4 = 4;
    public static final int MOBILE_CODE_TYPE5 = 5;
    public static final int MOBILE_CODE_TYPE6 = 6;
    /**
     * 用户状态:0-禁用 1-启用 2-锁定
     */
    public static final Integer[] USER_STATUS = {0, 1, 2};
    public static final Integer USER_STATUS0 = 0;
    public static final Integer USER_STATUS1 = 1;
    public static final Integer USER_STATUS2 = 2;

    /**
     * 用户来源 0-跨境知道 1-卖家成长 3-人工录入
     */
    public static final Integer[] USER_SOURCE = {0, 1, 3};
    public static final Integer USER_SOURCE0 = 0;
    public static final Integer USER_SOURCE1 = 1;
    public static final Integer USER_SOURCE3 = 3;

    /**
     * 用户类型:0-普通 1-潜在客户 2-客户
     */
    public static final Integer[] USER_TYPE = {0, 1, 2};
    public static final Integer USER_TYPE_CUSTOMER = 2;
    /**
     * 登录类型:0-密码、1-验证码、2-第三方账号
     */
    public static final Integer[] LOGIN_TYPE_All = {0, 1, 2};
    public static final Integer LOGIN_TYPE1 = 1;
    public static final Integer LOGIN_TYPE2 = 2;
    public static final Integer LOGIN_TYPE3 = 3;
    /**
     * 登陆密码同步标识:0-未同步 1-已同步
     */
    public static final Integer[] UPDATEFLAG = {0, 1};
    public static final Integer UPDATEFLAG0 = 0;
    public static final Integer UPDATEFLAG1 = 1;

    /**
     * 登录token前缀
     */
    public static final String LOGIN_TOKEN_PRE = "usercent_login_token";
    /**
     * 登录token时间(秒)
     */
    public static final long LOGIN_TOKEN_TIME = 480 * 60;
    /**
     * 登录token公钥盐值
     */
    public static final String PUBLIC_KEY_SALT_VUALE = "ADFADFA221FADFAAASDFADF";


    /**
     * 账户类型:1-微信移动应用 、2-微信网站应用、3-微信公众号、4-微信小程序、5-QQ、6-微博
     */
    public static final Integer[] PLATFORM_ALL = {1, 2, 3, 4};
    public static final Integer PLATFORM_WECHAT_MOBILE = 1;
    public static final Integer PLATFORM_WECHAT_PC = 2;
    public static final Integer PLATFORM_WECHAT_GZH = 3;
    public static final Integer PLATFORM_MINIPROGRAM = 4;
    public static final Integer PLATFORM_QQ = 5;
    public static final Integer PLATFORM_WEIBO = 6;

    /**
     * 第三方回调code 持久化redis中key前缀
     */
    public static final String THIRD_LOGIN_CODE_PRE = "user-pre-";
    /**
     * qq 回调code 持久化redis过期时间
     */
    public static final Long THIRD_LOGIN_CODE_TIME = 5 * 60L;

    /**
     * 短信签名
     */
    public static final Integer[] SIGNSOURCE = {0, 1};
    public static final Integer SIGNSOURCE0 = 0;
    public static final Integer SIGNSOURCE1 = 1;
    public static final String SIGNNAME0 = "跨境知道";
    public static final String SIGNNAME1 = "卖家成长";
    /**
     * 登录短信模板code
     */
    public static final String LOGIN_TEMPLATECODE = "SMS_142475098";
    /**
     * 注册短信模板code
     */
    public static final String REGISTER_TEMPLATECODE = "SMS_142475096";

    /**
     * 忘记密码短信模板code
     */
    public static final String FORGET_PASSWORD_TEMPLATECODE = "SMS_142475095";
    /**
     * 绑定第三方账号短信模板code
     */
    public static final String BINDINGS_TEMPLATECODE = "SMS_172205023";

    /**
     * 登录拦截器中客户端传入的token名称
     **/
    public static final String TOKEN_NAME = "token";
    /**
     * 登录拦截器中客户端传入的sessionId名称
     **/
    public static final String SESSIONID = "sessionId";
    /**
     * 登录拦截器中认证登录用户后存当前登录用户手机号
     **/
    public static final String LOGIN_MOBILE = "loginMobile";
    /**
     * 登录拦截器中认证登录用户后存当前登录用户token信息
     **/
    public static final String LOGIN_INFO = "loginInfo";

    /**
     * 收货地址是否为默认地址：0-否 1-是
     */
    public static final Integer[] CONSIGNEE_ADDRESS_IS_DEFAULT = {0, 1};
    public static final Integer CONSIGNEE_ADDRESS_IS_DEFAULT0 = 0;
    public static final Integer CONSIGNEE_ADDRESS_IS_DEFAULT1 = 1;
    /**
     * 微信通过code获取access_token 返回结果常量配置
     */
    public static final String WECHAT_ACCESSTOKEN_NAME = "access_token";
    public static final String WECHAT_ACCESSTOKEN_EXPIRES_IN_NAME = "expires_in";
    public static final String WECHAT_ACCESSTOKEN_REFRESH_TOKEN_NAME = "refresh_token";
    public static final String WECHAT_ACCESSTOKEN_OPENID_NAME = "openid";
    public static final String WECHAT_ACCESSTOKEN_SCOPE_NAME = "scope";
    public static final String WECHAT_ACCESSTOKEN_UNIONID_NAME = "unionid";

    /**
     * 旧用户验证信息存放redis中的前缀
     */
    public static final String TOLD_MOBILE_PRE = "old-mobile-pre-";
    /**
     * 旧用户验证信息存放redis中过期时间
     */
    public static final Long OLD_MOBILE_TIME = 5 * 60L;

    /**
     * 手工输入用户默认密码
     */
    public static final String MANUAL_INPUT_USER_DEFAULT_PASSWORD = "bsd123456";

    /**
     * 性别
     */
    public static final int GENDER_SECRECY = 0;//保密
    public static final int GENDER_MAN = 1;//男性
    public static final int GENDER_FEMALE = 2;//女性
}
