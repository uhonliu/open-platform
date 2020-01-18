package com.opencloud.gateway.spring.server.filter;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson.JSONObject;
import com.opencloud.common.utils.CryptoUtils;
import com.opencloud.common.utils.StringHelper;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.gateway.spring.server.configuration.ApiProperties;
import com.opencloud.gateway.spring.server.filter.context.GatewayContext;
import com.opencloud.gateway.spring.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 响应前缀过滤器,增加访问日志及响应加密
 *
 * @author liujianhong
 */
@Slf4j
public class PreResponseFilter implements WebFilter {
    private AccessLogService accessLogService;

    private static final AntPathMatcher pathMatch = new AntPathMatcher();
    private Set<String> accessLogIgnores = new ConcurrentHashSet<>();

    public PreResponseFilter(AccessLogService accessLogService, ApiProperties apiProperties) {
        this.accessLogService = accessLogService;

        // 默认忽略写访问日志
        accessLogIgnores.add("/");
        accessLogIgnores.add("/error");
        accessLogIgnores.add("/favicon.ico");
        if (apiProperties != null && apiProperties.getApiDebug()) {
            accessLogIgnores.add("/**/v2/api-docs/**");
            accessLogIgnores.add("/**/swagger-resources/**");
            accessLogIgnores.add("/webjars/**");
            accessLogIgnores.add("/doc.html");
            accessLogIgnores.add("/swagger-ui.html");
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        if (gatewayContext != null && notAccessLog(gatewayContext.getRequestPath())) {
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(join);

                        HttpHeaders headers = response.getHeaders();
                        if (gatewayContext != null && MediaType.APPLICATION_JSON.isCompatibleWith(headers.getContentType())) {
                            content = repackageBody(content, gatewayContext, headers);
                        }

                        response.getHeaders().setContentLength(content.length);
                        return bufferFactory.wrap(content);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };

        // 保存日志
        accessLogService.sendLog(exchange, null);
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * ResponseBody重装 & 加密
     *
     * @author liujianhong
     */
    private byte[] repackageBody(byte[] content, GatewayContext gatewayContext, HttpHeaders headers) {
        try {
            String contentEncoding = headers.getFirst("Content-Encoding");
            boolean isGzip = null != contentEncoding && contentEncoding.contains("gzip");
            if (isGzip) {
                content = StringHelper.uncompress(content);
            }

            String bodyStr = new String(content, StandardCharsets.UTF_8);
            JSONObject bodyObject = JSONObject.parseObject(bodyStr);
            String pathKey = "path";
            if (StringUtils.isEmpty(bodyObject.getString(pathKey)) && StringUtils.isNotEmpty(gatewayContext.getRequestPath())) {
                bodyObject.replace(pathKey, gatewayContext.getRequestPath());
            }

            // 响应加密
            if (gatewayContext.getEncryptType() != null && gatewayContext.getEncryptSecret() != null) {
                String dataKey = "data";
                String dataValue = bodyObject.getString(dataKey);
                String encryptData = CryptoUtils.encrypt(dataValue, gatewayContext.getEncryptSecret(), CryptoUtils.CryptoType.valueOf(gatewayContext.getEncryptType()));
                bodyObject.replace(dataKey, encryptData);
                log.info("请求{} 加密响应参数{},加密前:{},加密后:{}", bodyObject.getString(pathKey), dataKey, dataValue, encryptData);
            }

            bodyStr = bodyObject.toJSONString();
            if (isGzip) {
                content = StringHelper.compress(bodyStr, StandardCharsets.UTF_8);
            } else {
                content = bodyStr.getBytes();
            }
        } catch (Exception e) {
            log.error("请求{} 加密响应参数异常:{}", gatewayContext.getRequestPath(), e.getMessage());
        }

        return content;
    }

    /**
     * 判断是否需要写入访问日志
     *
     * @author liujianhong
     */
    protected boolean notAccessLog(String requestPath) {
        for (String path : accessLogIgnores) {
            if (pathMatch.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }
}
