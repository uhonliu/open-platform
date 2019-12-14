package com.opencloud.base.client.model;

import com.google.common.collect.Lists;
import com.opencloud.base.client.model.entity.BaseAccount;
import com.opencloud.common.security.OpenAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author: liuyadu
 * @date: 2018/11/12 11:35
 * @description:
 */
public class UserAccount extends BaseAccount implements Serializable {
    private static final long serialVersionUID = 6717800085953996702L;

    private Collection<Map> roles = Lists.newArrayList();
    /**
     * 用户权限
     */
    private Collection<OpenAuthority> authorities = Lists.newArrayList();
    /**
     * 第三方账号
     */
    private String thirdParty;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    public Collection<OpenAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<OpenAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(String thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Collection<Map> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Map> roles) {
        this.roles = roles;
    }
}
