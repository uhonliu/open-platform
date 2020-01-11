package com.opencloud.common.constants;

/**
 * @author liuyadu
 */
public class CommonConstants {
    /**
     * 默认超级管理员账号
     */
    public final static String ROOT = "admin";

    /**
     * 默认最小页码
     */
    public static final int MIN_PAGE = 0;
    /**
     * 最大显示条数
     */
    public static final int MAX_LIMIT = 999;
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 1;
    /**
     * 默认显示条数
     */
    public static final int DEFAULT_LIMIT = 10;
    /**
     * 页码 KEY
     */
    public static final String PAGE_KEY = "page";
    /**
     * 显示条数 KEY
     */
    public static final String PAGE_LIMIT_KEY = "limit";
    /**
     * 排序字段 KEY
     */
    public static final String PAGE_SORT_KEY = "sort";
    /**
     * 排序方向 KEY
     */
    public static final String PAGE_ORDER_KEY = "order";

    /**
     * 客户端ID KEY
     */
    public static final String APP_ID_KEY = "AppId";

    /**
     * 客户端秘钥 KEY
     */
    public static final String SECRET_KEY = "SecretKey";

    /**
     * 随机字符串 KEY
     */
    public static final String NONCE_KEY = "Nonce";

    /**
     * 时间戳 KEY
     */
    public static final String TIMESTAMP_KEY = "Timestamp";

    /**
     * 签名类型 KEY
     */
    public static final String SIGN_TYPE_KEY = "SignType";
    /**
     * 签名结果 KEY
     */
    public static final String SIGN_KEY = "Sign";
}
