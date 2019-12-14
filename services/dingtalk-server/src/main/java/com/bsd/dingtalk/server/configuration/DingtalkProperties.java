package com.bsd.dingtalk.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujianhong
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "opencloud.dingtalk")
public class DingtalkProperties {
    private String corpid;
    private String agentid;
    private String appkey;
    private String appsecret;
    private String encodingAesKey;
    private String token;
    private String callbackUrlHost;
}
