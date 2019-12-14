package com.opencloud.monitor.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author liuyadu
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //授予对所有静态资产和登录页面的公共访问权限。
                .antMatchers("/assets/**").permitAll()
                //必须对每个其他请求进行身份验证
                .anyRequest().authenticated()
                .and()
                //配置登录和注销
                .formLogin().loginPage("/login").permitAll().and()
                .logout().logoutUrl("/logout").permitAll().and()
                .cors().disable()
                //启用HTTP-Basic支持。这是Spring Boot Admin Client注册所必需的
                .httpBasic();
        // 允许iframe嵌套
        http.headers().frameOptions().disable();
    }
}