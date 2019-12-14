package com.opencloud.gateway.zuul.server.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 网关属性配置类
 *
 * @author: liuyadu
 * @date: 2018/11/23 14:40
 * @description:
 */
@ConfigurationProperties(prefix = "opencloud.api")
public class ApiProperties {
    /**
     * 是否开启签名验证
     */
    private Boolean checkSign = true;
    /**
     * 是否开启动态访问控制
     */
    private Boolean accessControl = true;

    /**
     * 是否开启接口调试
     */
    private Boolean apiDebug = false;

    /**
     * 始终放行
     */
    private Set<String> permitAll;

    /**
     * 无需鉴权的请求
     */
    private Set<String> authorityIgnores;

    /**
     * 签名忽略请求
     */
    private Set<String> signIgnores;

    public Boolean getCheckSign() {
        return checkSign;
    }

    public void setCheckSign(Boolean checkSign) {
        this.checkSign = checkSign;
    }

    public Boolean getAccessControl() {
        return accessControl;
    }

    public void setAccessControl(Boolean accessControl) {
        this.accessControl = accessControl;
    }

    public Boolean getApiDebug() {
        return apiDebug;
    }

    public void setApiDebug(Boolean apiDebug) {
        this.apiDebug = apiDebug;
    }

    public Set<String> getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(Set<String> permitAll) {
        this.permitAll = permitAll;
    }

    public Set<String> getAuthorityIgnores() {
        return authorityIgnores;
    }

    public void setAuthorityIgnores(Set<String> authorityIgnores) {
        this.authorityIgnores = authorityIgnores;
    }

    public Set<String> getSignIgnores() {
        return signIgnores;
    }

    public void setSignIgnores(Set<String> signIgnores) {
        this.signIgnores = signIgnores;
    }
}
