package com.bsd.user.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 极验验证配置信息
 *
 * @author liujianhong
 * @date 2019-09-05 13:12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "geetest")
public class GeetestProperties {
    private String captchaId;
    private String privateKey;
}
