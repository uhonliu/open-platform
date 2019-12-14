package com.opencloud.gateway.spring.server.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.opencloud.common.constants.QueueConstants;
import com.opencloud.common.security.OpenUserDetails;
import com.opencloud.gateway.spring.server.filter.context.GatewayContext;
import com.opencloud.gateway.spring.server.util.ReactiveWebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @author: liuyadu
 * @date: 2019/5/8 11:27
 * @description:
 */
@Slf4j
@Component
public class AccessLogService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${spring.application.name}")
    private String defaultServiceId;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @JsonIgnore
    private Set<String> ignores = new HashSet<>(Arrays.asList(new String[]{
            "/**/oauth/check_token/**",
            "/**/gateway/access/logs/**",
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

    public void sendLog(ServerWebExchange exchange, Exception ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        try {
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            int httpStatus = response.getStatusCode().value();
            String requestPath = request.getURI().getPath();
            String method = request.getMethodValue();
            Map<String, String> headers = request.getHeaders().toSingleValueMap();
            Map data = Maps.newHashMap();
            GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
            if (gatewayContext != null) {
                data = gatewayContext.getAllRequestData().toSingleValueMap();
            }
            String serviceId = null;
            if (route != null) {
                serviceId = route.getUri().toString().replace("lb://", "");
            }
            String ip = ReactiveWebUtils.getRemoteAddress(exchange);
            String userAgent = headers.get(HttpHeaders.USER_AGENT);
            Object requestTime = exchange.getAttribute("requestTime");
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
            Mono<Authentication> authenticationMono = exchange.getPrincipal();
            Mono<OpenUserDetails> authentication = authenticationMono
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal)
                    .cast(OpenUserDetails.class);
            authentication.subscribe(user -> {
                if (user != null) {
                    map.put("authentication", JSONObject.toJSONString(user));
                }
            });
            amqpTemplate.convertAndSend(QueueConstants.QUEUE_ACCESS_LOGS, map);
        } catch (Exception e) {
            log.error("access logs save error:{}", e.getMessage());
        }
    }
}
