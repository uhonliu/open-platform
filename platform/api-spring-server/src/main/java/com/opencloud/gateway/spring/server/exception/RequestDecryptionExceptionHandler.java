package com.opencloud.gateway.spring.server.exception;

import com.alibaba.fastjson.JSONObject;
import com.opencloud.common.exception.OpenCryptoException;
import com.opencloud.common.exception.OpenGlobalExceptionHandler;
import com.opencloud.common.model.ResultBody;
import com.opencloud.gateway.spring.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 请求解密异常处理,记录日志
 *
 * @author liujianhong
 */
@Slf4j
public class RequestDecryptionExceptionHandler implements CryptoExceptionHandler {
    private AccessLogService accessLogService;

    public RequestDecryptionExceptionHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, OpenCryptoException e) {
        ResultBody resultBody = OpenGlobalExceptionHandler.resolveException(e, exchange.getRequest().getURI().getPath());
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
            response.setStatusCode(HttpStatus.valueOf(resultBody.getHttpStatus()));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer buffer = dataBufferFactory.wrap(JSONObject.toJSONString(resultBody).getBytes(Charset.defaultCharset()));
            // 保存日志
            accessLogService.sendLog(exchange, e);
            return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
                DataBufferUtils.release(buffer);
            });
        });
    }
}
