package com.bsd.file.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 启动入口
 *
 * @author liujianhong
 * @date 2019-06-27
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FileApplication {
    /**
     * 开启 @LoadBalanced 与 Ribbon 的集成
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}
