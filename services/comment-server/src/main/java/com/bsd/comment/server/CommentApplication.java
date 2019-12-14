package com.bsd.comment.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 评论中心
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 14:54
 */
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.bsd.comment.server.mapper")
public class CommentApplication {
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

    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
    }
}
