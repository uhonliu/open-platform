package com.opencloud.gateway.zuul.server.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.opencloud.common.constants.QueueConstants;
import com.opencloud.common.security.OpenHelper;
import com.opencloud.common.security.OpenUserDetails;
import com.opencloud.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 访问日志处理类
 *
 * @author: liuyadu
 * @date: 2019/5/8 11:27
 * @description:
 */
@Slf4j
@Component
public class AccessLogService {
    private ExecutorService executorService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${spring.application.name}")
    private String defaultServiceId;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AccessLogService() {
        int numOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        executorService = new ThreadPoolExecutor(numOfThreads, numOfThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build());
    }

    @JsonIgnore
    private Set<String> ignores = new HashSet<>(Arrays.asList(new String[]{
            "/**/oauth/check_token",
            "/**/gateway/access/logs",
            "/webjars/**"
    }));

    /**
     * 不记录日志
     *
     * @param requestPath
     * @return
     */
    public boolean ignore(String requestPath) {
        Iterator<String> iterator = ignores.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (antPathMatcher.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }

    public void sendLog(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        int httpStatus = response.getStatus();
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        Map headers = WebUtils.getHttpHeaders(request);
        Map data = WebUtils.getParameterMap(request);
        Object serviceId = request.getAttribute(FilterConstants.SERVICE_ID_KEY);
        String ip = WebUtils.getRemoteAddress(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        Object requestTime = request.getAttribute("requestTime");
        String error = null;
        if (ex != null) {
            error = ex.getMessage();
        }
        if (ignore(requestPath)) {
            return;
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("requestTime", requestTime);
        map.put("serviceId", serviceId == null ? defaultServiceId : serviceId);
        map.put("httpStatus", httpStatus);
        map.put("headers", JSONObject.toJSON(headers));
        map.put("path", requestPath);
        map.put("params", JSONObject.toJSON(data));
        map.put("ip", ip);
        map.put("method", method);
        map.put("userAgent", userAgent);
        map.put("responseTime", new Date());
        map.put("error", error);
        OpenUserDetails user = OpenHelper.getUser();
        if (user != null) {
            map.put("authentication", JSONObject.toJSONString(user));
        }
        executorService.submit(() -> {
            try {
                amqpTemplate.convertAndSend(QueueConstants.QUEUE_ACCESS_LOGS, map);
            } catch (Exception e) {
                log.error("access logs save error:{}", e.getMessage());
            }
        });
    }
}
