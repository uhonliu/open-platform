package com.bsd.user.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

import java.util.Date;

/**
 * 用户-基础信息
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@TableName("bsd_user")
public class User extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 (0 保密  1男 2女)
     */
    private Integer sex;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户类型:0-普通 1-潜在客户 2-客户
     */
    private Integer userType;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 用户类型:0-PC 1-H5 2-IOS 3-Android 4-公众号 5-小程序
     */
    private Integer source;

    /**
     * 状态:0-禁用 1-启用 2-锁定
     */
    private Integer status;

    /**
     * 描述
     */
    private String userDesc;

    /**
     * 登陆密码同步标识:0-未同步 1-已同步
     */
    private Integer updateFlag;

    /**
     * 邀请码
     */
    private String invitedCode;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }


    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }


    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Integer updateFlag) {
        this.updateFlag = updateFlag;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getInvitedCode() {
        return invitedCode;
    }

    public void setInvitedCode(String invitedCode) {
        this.invitedCode = invitedCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userCode=" + userCode +
                ", username=" + username +
                ", password=" + password +
                ", nickname=" + nickname +
                ", avatar=" + avatar +
                ", email=" + email +
                ", mobile=" + mobile +
                ", userType=" + userType +
                ", companyId=" + companyId +
                ", registerIp=" + registerIp +
                ", registerTime=" + registerTime +
                ", source=" + source +
                ", status=" + status +
                ", userDesc=" + userDesc +
                "}";
    }
}
