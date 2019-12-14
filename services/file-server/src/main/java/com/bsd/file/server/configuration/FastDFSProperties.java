package com.bsd.file.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * FastDFS配置信息
 *
 * @author liujianhong
 * @date 2019-07-02 16:12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "fastdfs")
public class FastDFSProperties {
    private String connectTimeoutInSeconds;
    private String networkTimeoutInSeconds;
    private String charset;
    private String httpAntiStealToken;
    private String httpSecretKey;
    private String httpTrackerHttpPort;
    private String trackerServers;
    private String trackerUrl;
}
