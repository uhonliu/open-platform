package com.bsd.org.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

/**
 * 组织架构
 *
 * @Author: linrongxin
 * @Date: 2019/8/14 11:06
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = "com.bsd.org.server.mapper")
public class OrgApplication {
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
        SpringApplication.run(OrgApplication.class, args);
    }
}
