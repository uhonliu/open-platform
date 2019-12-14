package com.bsd.migration.utils;

import com.bsd.migration.constants.CommonConstants;
import com.bsd.migration.model.resp.Config;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: linrongxin
 * @Date: 2019/9/30 11:53
 */
@Slf4j
public class OAuth2RequestUtils {
    /**
     * 创建OAuth2RestTemplate
     *
     * @param config
     * @return
     */
    private static OAuth2RestTemplate buildOAuth2ClientRequest(Config config) {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setClientId(config.getClientId());
        resource.setClientSecret(config.getClientSecret());
        resource.setAccessTokenUri(getUrl(config, CommonConstants.ACCESS_TOKEN_URL_SUFFIX));
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);
        return restTemplate;
    }

    /**
     * 创建OAuth2公共头部信息
     *
     * @param restTemplate
     * @return
     */
    private static HttpHeaders buildOAuth2CommonHeaders(OAuth2RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptableMediaTypes);
        headers.add("Authorization", String.format("%s %s", "Bearer", restTemplate.getAccessToken().getValue()));
        return headers;
    }


    private static <T> T req(Config config, String api, HttpMethod httpMethod, String data, ParameterizedTypeReference<T> t) {
        OAuth2RestTemplate restTemplate = buildOAuth2ClientRequest(config);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            if (!StringUtils.isEmpty(data)) {
                Map dataMap = objectMapper.readValue(data, Map.class);
                dataMap.forEach((k, v) -> {
                    params.add(String.valueOf(k), String.valueOf(v));
                });
            }
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params, buildOAuth2CommonHeaders(restTemplate));
            return restTemplate.exchange(URI.create(getUrl(config, api)), httpMethod, entity, t).getBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T getForEntity(Config config, String api, String data, Class<T> t) {
        OAuth2RestTemplate restTemplate = buildOAuth2ClientRequest(config);
        HttpEntity<String> entity = new HttpEntity<String>(data, buildOAuth2CommonHeaders(restTemplate));
        T result = restTemplate.getForEntity(getUrl(config, api), t, entity).getBody();
        return result;
    }

    private static <T> T postForEntity(Config config, String api, String data, Class<T> t) {
        OAuth2RestTemplate restTemplate = buildOAuth2ClientRequest(config);
        HttpEntity<String> entity = new HttpEntity<String>(data, buildOAuth2CommonHeaders(restTemplate));
        T result = restTemplate.postForEntity(getUrl(config, api), entity, t).getBody();
        return result;
    }


    public static <T> T getReq(Config config, String api, String data, Class<T> clazz) {
        return getForEntity(config, api, data, clazz);
    }

    public static <T> T getReq(Config config, String api, String data, ParameterizedTypeReference<T> t) {
        return req(config, api, HttpMethod.GET, data, t);
    }

    public static <T> T postReq(Config config, String api, String data, ParameterizedTypeReference<T> t) {
        return req(config, api, HttpMethod.POST, data, t);
    }

    public static <T> T getReq(Config config, String api, Class<T> clazz) {
        return getReq(config, api, null, clazz);
    }

    public static <T> T getReq(Config config, String api, ParameterizedTypeReference<T> t) {
        return getReq(config, api, null, t);
    }


    private static String getUrl(Config config, String api) {
        return config.getGatewayUrl() + api;
    }
}
