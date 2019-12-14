package com.bsd.payment.server.configuration.channel.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujianhong
 * @Description: 微信配置类
 * @date 2019-07-05
 */
@Configuration
@ConfigurationProperties(prefix = "opencloud.wxpay")
public class WxPayProperties {
    private String certRootPath;

    private String notifyUrl;

    public String getCertRootPath() {
        return certRootPath;
    }

    public void setCertRootPath(String certRootPath) {
        this.certRootPath = certRootPath;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
