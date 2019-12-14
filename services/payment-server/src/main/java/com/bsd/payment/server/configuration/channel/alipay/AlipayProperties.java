package com.bsd.payment.server.configuration.channel.alipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @author liujianhong
 * @Description: 支付宝配置类
 * @date 2019-07-05
 */
@Configuration
@ConfigurationProperties(prefix = "opencloud.alipay")
public class AlipayProperties {
    /**
     * 商户appid
     */
    private String appid;

    /**
     * 公钥
     */
    public String publicKey;

    /**
     * 私钥 pkcs8格式
     */
    private String privateKey;

    /**
     * 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    private String notifyUrl;

    /**
     * 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
     */
    private String returnUrl;

    /**
     * 请求网关地址
     */
    private String url = "https://openapi.alipay.com/gateway.do";

    /**
     * 编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 返回格式
     */
    public static final String FORMAT = "json";

    /**
     * RSA2
     */
    public static final String SIGNTYPE = "RSA2";

    /**
     * 是否沙箱环境,1:沙箱,0:正式环境
     */
    private Short isSandbox = 0;

    /**
     * 初始化支付宝配置
     *
     * @param configParam
     * @return
     */
    public AlipayProperties init(String configParam) {
        Assert.notNull(configParam, "init alipay config error");
        JSONObject paramObj = JSON.parseObject(configParam);
        this.setAppid(paramObj.getString("appid"));
        this.setPrivateKey(paramObj.getString("private_key"));
        this.setPublicKey(paramObj.getString("public_key"));
        this.setIsSandbox(paramObj.getShortValue("is_sandbox"));
        if (this.getIsSandbox().intValue() == 1) {
            this.setUrl("https://openapi.alipaydev.com/gateway.do");
        }
        return this;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Short getIsSandbox() {
        return isSandbox;
    }

    public void setIsSandbox(Short isSandbox) {
        this.isSandbox = isSandbox;
    }
}

