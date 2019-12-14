package com.bsd.user.server.configuration;

import com.bsd.user.server.filter.LoginTokenHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户中心登录认证拦截器配置
 *
 * @author lisongmao
 * @date 2019-7-18
 **/
@Configuration
public class LoginTokenInterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginTokenHandlerInterceptor loginTokenHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*List<String> excludePtah = new ArrayList<String>();
        excludePtah.add("/user/User/**");
        excludePtah.add("/user/address/save");

        registry.addInterceptor(loginTokenHandlerInterceptor).excludePathPatterns(excludePtah).addPathPatterns("/**");*/
        List<String> pathPatterns = new ArrayList<String>();
        pathPatterns.add("/user/address/save");
        pathPatterns.add("/user/address/list");
        pathPatterns.add("/user/address/delete");
        pathPatterns.add("/user/address/setDefault");
        pathPatterns.add("/user/verify/OldMobile");
        pathPatterns.add("/user/update/mobile");

        pathPatterns.add("/user/get/info");
        pathPatterns.add("/user/verify/token");
        pathPatterns.add("/user/update/password");
        pathPatterns.add("/user/update/info");
        pathPatterns.add("/user/third/unbind");


        registry.addInterceptor(loginTokenHandlerInterceptor).addPathPatterns(pathPatterns);
    }
}
