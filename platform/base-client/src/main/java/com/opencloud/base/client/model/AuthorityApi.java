package com.opencloud.base.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencloud.base.client.model.entity.BaseApi;

import java.io.Serializable;
import java.util.Objects;

/**
 * API权限
 *
 * @author liuyadu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityApi extends BaseApi implements Serializable {
    private static final long serialVersionUID = 3474271304324863160L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 前缀
     */
    private String prefix;

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AuthorityApi)) {
            return false;
        }
        AuthorityApi a = (AuthorityApi) obj;
        return this.authorityId.equals(a.getAuthorityId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId);
    }
}
