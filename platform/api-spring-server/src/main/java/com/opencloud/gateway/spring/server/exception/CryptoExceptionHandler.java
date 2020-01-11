package com.opencloud.gateway.spring.server.exception;

import com.opencloud.common.exception.OpenCryptoException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author liujianhong
 */
public interface CryptoExceptionHandler {
    Mono<Void> handle(ServerWebExchange var1, OpenCryptoException var2);
}