package com.opencloud.gateway.spring.server.filter.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author liuyadu
 */
@Getter
@Setter
@ToString
public class GatewayContext {
    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";
    /**
     * cache request path
     */
    private String requestPath;
    /**
     * cache json body
     */
    private String requestBody;
    /**
     * cache Response Body
     */
    private Object responseBody;
    /**
     * request headers
     */
    private HttpHeaders requestHeaders;
    /**
     * cache form data
     */
    private MultiValueMap<String, String> formData;
    /**
     * cache all request data include:form data and query param
     */
    private MultiValueMap<String, String> allRequestData = new LinkedMultiValueMap<>(0);
    /**
     * cache encrypt type
     */
    private String encryptType;
    /**
     * cache encrypt secret
     */
    private String encryptSecret;
}