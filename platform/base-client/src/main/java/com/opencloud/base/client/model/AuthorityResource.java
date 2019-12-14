package com.opencloud.base.client.model;

import java.io.Serializable;

/**
 * 资源权限
 *
 * @author liuyadu
 */
public class AuthorityResource implements Serializable {
    private static final long serialVersionUID = -320031660125425711L;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 是否身份认证
     */
    private Integer isAuth;

    /**
     * 是否公开访问
     */
    private Integer isOpen;

    /**
     * 服务名称
     */
    private String serviceId;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * 资源状态
     */
    private Integer status;

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }
}
