package com.opencloud.gateway.spring.server.filter;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opencloud.base.client.model.entity.BaseApp;
import com.opencloud.common.constants.CommonConstants;
import com.opencloud.common.exception.OpenCryptoException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.CryptoUtils;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.gateway.spring.server.configuration.ApiProperties;
import com.opencloud.gateway.spring.server.exception.RequestDecryptionExceptionHandler;
import com.opencloud.gateway.spring.server.filter.context.GatewayContext;
import com.opencloud.gateway.spring.server.filter.support.CachedBodyOutputMessage;
import com.opencloud.gateway.spring.server.service.feign.BaseAppServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SpringCloud Gateway 记录缓存请求Body和Form表单,请求参数解密
 * GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
 * https://github.com/chenggangpro/spring-cloud-gateway-plugin
 *
 * @author liuyadu
 */
@Slf4j
@AllArgsConstructor
public class GatewayContextFilter implements WebFilter, Ordered {
    /**
     * default HttpMessageReader
     */
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    private RequestDecryptionExceptionHandler requestDecryptionExceptionHandler;
    private BaseAppServiceClient baseAppServiceClient;
    private ApiProperties apiProperties;

    private static final AntPathMatcher pathMatch = new AntPathMatcher();
    private Set<String> encryptIgnores = new ConcurrentHashSet<>();

    public GatewayContextFilter(BaseAppServiceClient baseAppServiceClient, ApiProperties apiProperties, RequestDecryptionExceptionHandler requestDecryptionExceptionHandler) {
        this.apiProperties = apiProperties;
        this.baseAppServiceClient = baseAppServiceClient;
        this.requestDecryptionExceptionHandler = requestDecryptionExceptionHandler;

        // 默认忽略解密
        encryptIgnores.add("/");
        encryptIgnores.add("/error");
        encryptIgnores.add("/favicon.ico");
        if (apiProperties != null) {
            if (apiProperties.getEncryptIgnores() != null) {
                encryptIgnores.addAll(apiProperties.getEncryptIgnores());
            }
            if (apiProperties.getApiDebug()) {
                encryptIgnores.add("/**/v2/api-docs/**");
                encryptIgnores.add("/**/swagger-resources/**");
                encryptIgnores.add("/webjars/**");
                encryptIgnores.add("/doc.html");
                encryptIgnores.add("/swagger-ui.html");
            }
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().pathWithinApplication().value();
        HttpHeaders headers = request.getHeaders();

        GatewayContext gatewayContext = new GatewayContext();
        gatewayContext.setRequestPath(requestPath);
        gatewayContext.setRequestHeaders(headers);

        MultiValueMap<String, String> params = request.getQueryParams();
        String method = request.getMethodValue();
        if (apiProperties.getCheckEncrypt() && !notEncrypt(requestPath) && baseAppServiceClient != null) {
            // 验证请求参数
            Assert.notNull(headers.getFirst(CommonConstants.APP_ID_KEY), String.format("解密失败:%s不能为空", CommonConstants.APP_ID_KEY));

            // 获取客户端信息
            String appId = headers.getFirst(CommonConstants.APP_ID_KEY);
            ResultBody<BaseApp> result = baseAppServiceClient.getApp(appId);
            BaseApp app = result.getData();
            if (app == null || app.getAppId() == null) {
                return requestDecryptionExceptionHandler.handle(exchange, new OpenCryptoException("appId无效"));
            }

            if (app.getIsEncrypt() == 1) {
                String encryptType = app.getEncryptType();
                if (!CryptoUtils.CryptoType.contains(encryptType)) {
                    return requestDecryptionExceptionHandler.handle(exchange, new OpenCryptoException("加密类型错误"));
                }
                gatewayContext.setEncryptType(encryptType);

                // 加密Key
                String encryptSecret = "RSA".equalsIgnoreCase(encryptType) ? app.getPublicKey() : app.getSecretKey();
                gatewayContext.setEncryptSecret(encryptSecret);

                // 参数解密
                if (HttpMethod.GET.matches(method) && params.containsKey(apiProperties.getEncryptKey()) && StringUtils.isNotEmpty(params.getFirst(apiProperties.getEncryptKey()))) {
                    return decryptQueryParams(exchange, chain, gatewayContext);
                }
            }
        }

        gatewayContext.getAllRequestData().addAll(params);

        MediaType contentType = headers.getContentType();
        if (contentType != null && headers.getContentLength() > 0) {
            if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                return readBody(exchange, chain, gatewayContext);
            }
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return readFormData(exchange, chain, gatewayContext);
            }
        }

        exchange.getAttributes().put(GatewayContext.CACHE_GATEWAY_CONTEXT, gatewayContext);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private Mono<Void> decryptQueryParams(ServerWebExchange exchange, WebFilterChain chain, GatewayContext gatewayContext) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params = request.getQueryParams();
        String encryptData = params.getFirst(apiProperties.getEncryptKey());
        Map<String, String> decryptToMap = CryptoUtils.decryptToMap(encryptData, gatewayContext.getEncryptSecret(), CryptoUtils.CryptoType.valueOf(gatewayContext.getEncryptType()));
        if (decryptToMap.isEmpty()) {
            return requestDecryptionExceptionHandler.handle(exchange, new OpenCryptoException("请求参数解密失败!"));
        }

        MultiValueMap<String, String> newParams = new LinkedMultiValueMap<>();
        newParams.setAll(decryptToMap);
        newParams.addAll(params);
        newParams.remove(apiProperties.getEncryptKey());

        gatewayContext.getAllRequestData().addAll(newParams);
        exchange.getAttributes().put(GatewayContext.CACHE_GATEWAY_CONTEXT, gatewayContext);
        log.info("请求{} 解密请求参数{},解密前:{},解密后:{}", gatewayContext.getRequestPath(), apiProperties.getEncryptKey(), encryptData, JSON.toJSONString(decryptToMap));

        URI uri = request.getURI();
        boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
        URI newUri = UriComponentsBuilder.fromUri(uri).replaceQueryParams(newParams).build(encoded).toUri();
        ServerHttpRequest newRequest = request.mutate().uri(newUri).build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * ReadFormData
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> readFormData(ServerWebExchange exchange, WebFilterChain chain, GatewayContext gatewayContext) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return exchange.getFormData()
                .doOnNext(multiValueMap -> {
                    multiValueMap = decryptFormData(multiValueMap, gatewayContext);
                    gatewayContext.setFormData(multiValueMap);
                    gatewayContext.getAllRequestData().addAll(multiValueMap);
                    log.debug("[GatewayContext]Read FormData Success");
                })
                .then(Mono.defer(() -> {
                    Charset charset = (headers.getContentType() != null && headers.getContentType().getCharset() != null)
                            ? headers.getContentType().getCharset() : StandardCharsets.UTF_8;
                    String charsetName = charset.name();
                    MultiValueMap<String, String> formData = gatewayContext.getFormData();

                    /*
                     * formData is empty just return
                     */
                    if (null == formData || formData.isEmpty()) {
                        return chain.filter(exchange);
                    }

                    StringBuilder formDataBodyBuilder = new StringBuilder();
                    String entryKey;
                    List<String> entryValue;
                    try {
                        /*
                         * repackage form data
                         */
                        for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
                            entryKey = entry.getKey();
                            entryValue = entry.getValue();
                            if (entryValue.size() > 1) {
                                for (String value : entryValue) {
                                    formDataBodyBuilder.append(entryKey).append("=").append(URLEncoder.encode(value, charsetName)).append("&");
                                }
                            } else {
                                formDataBodyBuilder.append(entryKey).append("=").append(URLEncoder.encode(entryValue.get(0), charsetName)).append("&");
                            }
                        }
                    } catch (UnsupportedEncodingException e) {

                    }

                    /*
                     * substring with the last char '&'
                     */
                    String formDataBodyString = "";
                    if (formDataBodyBuilder.length() > 0) {
                        formDataBodyString = formDataBodyBuilder.substring(0, formDataBodyBuilder.length() - 1);
                    }

                    /*
                     * get data bytes
                     */
                    byte[] bodyBytes = formDataBodyString.getBytes(charset);

                    int contentLength = bodyBytes.length;
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(exchange.getRequest().getHeaders());
                    httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);

                    /*
                     * in case of content-length not matched
                     */
                    if (contentLength > 0) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    gatewayContext.setRequestHeaders(httpHeaders);
                    exchange.getAttributes().put(GatewayContext.CACHE_GATEWAY_CONTEXT, gatewayContext);

                    /*
                     * use BodyInserter to InsertFormData Body
                     */
                    log.debug("[GatewayContext]Rewrite Form Data :{}", formDataBodyString);
                    CachedBodyOutputMessage cachedBodyOutputMessage = new CachedBodyOutputMessage(exchange, httpHeaders);
                    BodyInserter<String, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromObject(formDataBodyString);
                    return bodyInserter.insert(cachedBodyOutputMessage, new BodyInserterContext())
                            .then(Mono.defer(() -> {
                                ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                                    @Override
                                    public HttpHeaders getHeaders() {
                                        return httpHeaders;
                                    }

                                    @Override
                                    public Flux<DataBuffer> getBody() {
                                        return cachedBodyOutputMessage.getBody();
                                    }
                                };
                                return chain.filter(exchange.mutate().request(decorator).build());
                            }));
                }));
    }

    /**
     * FormData 解密
     *
     * @author liujianhong
     */
    private MultiValueMap<String, String> decryptFormData(MultiValueMap<String, String> fromData, GatewayContext gatewayContext) {
        if (gatewayContext.getEncryptType() != null && gatewayContext.getEncryptSecret() != null) {
            String encryptData = fromData.getFirst(apiProperties.getEncryptKey());
            if (StringUtils.isNotEmpty(encryptData)) {
                Map<String, String> decryptToMap = CryptoUtils.decryptToMap(encryptData, gatewayContext.getEncryptSecret(), CryptoUtils.CryptoType.valueOf(gatewayContext.getEncryptType()));
                if (decryptToMap.isEmpty()) {
                    throw new OpenCryptoException("请求参数解密失败!");
                }
                log.info("请求{} 解密请求参数{},解密前:{},解密后:{}", gatewayContext.getRequestPath(), apiProperties.getEncryptKey(), encryptData, JSON.toJSONString(decryptToMap));

                MultiValueMap<String, String> newFormData = new LinkedMultiValueMap<>(0);
                newFormData.setAll(decryptToMap);
                newFormData.addAll(fromData);
                newFormData.remove(apiProperties.getEncryptKey());
                return newFormData;
            }
        }

        return fromData;
    }

    /**
     * ReadJsonBody
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> readBody(ServerWebExchange exchange, WebFilterChain chain, GatewayContext gatewayContext) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Charset charset = (headers.getContentType() != null && headers.getContentType().getCharset() != null)
                ? headers.getContentType().getCharset() : StandardCharsets.UTF_8;
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    /*
                     * read the body Flux<DataBuffer>, and release the buffer
                     * //TODO when SpringCloudGateway Version Release To G.SR2,this can be update with the new version's feature
                     * see PR https://github.com/spring-cloud/spring-cloud-gateway/pull/1095
                     */
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    byte[] finalBytes = repackageBody(bytes, gatewayContext, charset);
                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(finalBytes);
                        DataBufferUtils.retain(buffer);
                        return Mono.just(buffer);
                    });

                    int contentLength = finalBytes.length;
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(headers);
                    httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);

                    /*
                     * in case of content-length not matched
                     */
                    if (contentLength > 0) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    gatewayContext.setRequestHeaders(httpHeaders);

                    /*
                     * repackage ServerHttpRequest
                     */
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                    return ServerRequest.create(mutatedExchange, MESSAGE_READERS)
                            .bodyToMono(String.class)
                            .doOnNext(objectValue -> {
                                gatewayContext.setRequestBody(objectValue);
                                Map<String, String> bodyMap = new HashMap<>(0);
                                JSONObject.parseObject(objectValue).forEach((key, value) -> {
                                    bodyMap.put(key, value.toString());
                                });
                                gatewayContext.getAllRequestData().setAll(bodyMap);
                                mutatedExchange.getAttributes().put(GatewayContext.CACHE_GATEWAY_CONTEXT, gatewayContext);
                                log.debug("[GatewayContext]Read JsonBody Success");
                            }).then(chain.filter(mutatedExchange));
                });
    }

    /**
     * RequestBody重装 & 解密
     *
     * @author liujianhong
     */
    private byte[] repackageBody(byte[] content, GatewayContext gatewayContext, Charset charset) {
        // 参数解密
        if (gatewayContext.getEncryptType() != null && gatewayContext.getEncryptSecret() != null) {
            JSONObject bodyObject = JSONObject.parseObject(new String(content, charset));
            String encryptData = bodyObject.getString(apiProperties.getEncryptKey());
            if (StringUtils.isNotEmpty(encryptData)) {
                Map<String, String> decryptToMap = CryptoUtils.decryptToMap(encryptData, gatewayContext.getEncryptSecret(), CryptoUtils.CryptoType.valueOf(gatewayContext.getEncryptType()));
                if (decryptToMap.isEmpty()) {
                    throw new OpenCryptoException("请求参数解密失败!");
                }
                log.info("请求{} 解密请求参数{},解密前:{},解密后:{}", gatewayContext.getRequestPath(), apiProperties.getEncryptKey(), encryptData, JSON.toJSONString(decryptToMap));

                bodyObject.remove(apiProperties.getEncryptKey());
                decryptToMap.forEach(bodyObject::put);
                return bodyObject.toJSONString().getBytes(charset);
            }
        }
        return content;
    }

    protected boolean notEncrypt(String requestPath) {
        if (apiProperties.getEncryptIgnores() == null) {
            return false;
        }
        for (String path : encryptIgnores) {
            if (pathMatch.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }
}
