package com.bsd.file.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 七牛云存储配置信息
 *
 * @author liujianhong
 * @date 2019-07-02 16:12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qiniu")
public class QiNiuProperties {
    private String domain;
    private String prefix;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String callbackUrl;
}
