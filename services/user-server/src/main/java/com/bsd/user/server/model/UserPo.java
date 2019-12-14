package com.bsd.user.server.model;

import com.bsd.user.server.model.entity.User;

import java.util.Date;

/**
 * User 扩展类
 *
 * @author lisongmao
 * 2019年6月27日
 */
public class UserPo extends User {
    private static final long serialVersionUID = -5378935646560519097L;

    public UserPo() {
    }

    public UserPo(Long userId, String userCode, String username, String password, String nickname, String avatar, Integer sex, String email, String mobile, Integer userType, Long companyId, String registerIp, Date registerTime, Integer source, Integer status, String userDesc, Integer updateFlag, Date createTime, Date updateTime) {
        setUserId(userId);
        setUserCode(userCode);
        setUsername(username);
        setPassword(password);
        setNickname(nickname);
        setAvatar(avatar);
        setSex(sex);
        setEmail(email);
        setMobile(mobile);
        setUserType(userType);
        setCompanyId(companyId);
        setRegisterIp(registerIp);
        setRegisterTime(registerTime);
        setSource(source);
        setStatus(status);
        setUserDesc(userDesc);
        setUpdateFlag(updateFlag);
        setCreateTime(createTime);
        setUpdateTime(updateTime);
    }

    /**
     * 用户登录注册输入手机验证码
     */
    private String inputCode;

    /**
     * 用户登录ip
     */
    private String loginIp;
    /**
     * 登录token
     */
    private String token;
    /**
     * 登录会话id
     */
    private String sessionId;
    /**
     * 原密码
     */
    private String oldPassword;
    /**
     * 登录手机号
     */
    private String loginMobile;
    /**
     * 搜索内容(用户昵称/手机号/用户ID)
     */
    private String searchContent;

    public String getInputCode() {
        return inputCode;
    }

    public void setInputCode(String inputCode) {
        this.inputCode = inputCode;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getLoginMobile() {
        return loginMobile;
    }

    public void setLoginMobile(String loginMobile) {
        this.loginMobile = loginMobile;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }
}
