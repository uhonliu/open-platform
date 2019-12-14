package com.opencloud.sdk;

/**
 * @author: liuyadu
 * @date: 2019/7/10 14:38
 * @description:
 */
public class BaseClient {
    /**
     * 客户端ID
     */
    private String appId;
    /**
     * 访问Key
     */
    private String apiKey;
    /**
     * 访问密钥
     */
    private String secretKey;

    public BaseClient(String appId, String apiKey, String secretKey) {
        this.appId = appId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
