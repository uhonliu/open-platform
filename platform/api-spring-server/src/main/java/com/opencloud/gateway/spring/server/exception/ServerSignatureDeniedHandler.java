package com.opencloud.gateway.spring.server.exception;

import com.opencloud.common.exception.OpenSignatureException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ServerSignatureDeniedHandler {
    Mono<Void> handle(ServerWebExchange var1, OpenSignatureException var2);
}