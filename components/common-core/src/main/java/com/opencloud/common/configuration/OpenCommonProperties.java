package com.opencloud.common.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义网关配置
 *
 * @author: liuyadu
 * @date: 2018/11/23 14:40
 * @description:
 */
@ConfigurationProperties(prefix = "opencloud.common")
public class OpenCommonProperties {
    /**
     * 网关客户端Id
     */
    private String clientId;
    /**
     * 网关客户端密钥
     */
    private String clientSecret;
    /**
     * 网关服务地址
     */
    private String apiServerAddr;

    /**
     * 平台认证服务地址
     */
    private String authServerAddr;

    /**
     * 后台部署地址
     */
    private String adminServerAddr;

    /**
     * 认证范围
     */
    private String scope;
    /**
     * 获取token
     */
    private String accessTokenUri;
    /**
     * 认证地址
     */
    private String userAuthorizationUri;
    /**
     * 获取token地址
     */
    private String tokenInfoUri;
    /**
     * 获取用户信息地址
     */
    private String userInfoUri;

    /**
     * jwt签名key
     */
    private String jwtSigningKey;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getApiServerAddr() {
        return apiServerAddr;
    }

    public void setApiServerAddr(String apiServerAddr) {
        this.apiServerAddr = apiServerAddr;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public void setUserAuthorizationUri(String userAuthorizationUri) {
        this.userAuthorizationUri = userAuthorizationUri;
    }

    public String getTokenInfoUri() {
        return tokenInfoUri;
    }

    public void setTokenInfoUri(String tokenInfoUri) {
        this.tokenInfoUri = tokenInfoUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public String getAdminServerAddr() {
        return adminServerAddr;
    }

    public void setAdminServerAddr(String adminServerAddr) {
        this.adminServerAddr = adminServerAddr;
    }

    public String getAuthServerAddr() {
        return authServerAddr;
    }

    public void setAuthServerAddr(String authServerAddr) {
        this.authServerAddr = authServerAddr;
    }

    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

    public void setJwtSigningKey(String jwtSigningKey) {
        this.jwtSigningKey = jwtSigningKey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OpenCommonProperties{");
        sb.append("clientId='").append(clientId).append('\'');
        sb.append(", clientSecret='").append(clientSecret).append('\'');
        sb.append(", apiServerAddr='").append(apiServerAddr).append('\'');
        sb.append(", authServerAddr='").append(authServerAddr).append('\'');
        sb.append(", adminServerAddr='").append(adminServerAddr).append('\'');
        sb.append(", scope='").append(scope).append('\'');
        sb.append(", accessTokenUri='").append(accessTokenUri).append('\'');
        sb.append(", userAuthorizationUri='").append(userAuthorizationUri).append('\'');
        sb.append(", tokenInfoUri='").append(tokenInfoUri).append('\'');
        sb.append(", userInfoUri='").append(userInfoUri).append('\'');
        sb.append(", jwtSigningKey='").append(jwtSigningKey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
