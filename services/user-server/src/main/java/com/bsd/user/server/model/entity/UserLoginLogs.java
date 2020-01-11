package com.bsd.user.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

import java.util.Date;

/**
 * 用户-登录日志
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@TableName("bsd_user_login_logs")
public class UserLoginLogs extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long logId;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 账号ID
     */
    private Long accountId;

    /**
     * 登录Ip
     */
    private String loginIp;

    private Date loginTime;

    /**
     * 登录设备
     */
    private String loginAgent;

    /**
     * 登录次数
     */
    private Integer loginNums;

    /**
     * 标识：手机号、邮箱、 用户名、或第三方应用的唯一标识
     */
    private String loginAccount;

    /**
     * 登录类型:0-密码、1-验证码、2-第三方账号
     */
    private Integer loginType;


    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }


    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginAgent() {
        return loginAgent;
    }

    public void setLoginAgent(String loginAgent) {
        this.loginAgent = loginAgent;
    }

    public Integer getLoginNums() {
        return loginNums;
    }

    public void setLoginNums(Integer loginNums) {
        this.loginNums = loginNums;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }


    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "UserLoginLogs{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", loginIp=" + loginIp +
                ", loginTime=" + loginTime +
                ", loginAgent=" + loginAgent +
                ", loginNums=" + loginNums +
                ", loginAccount=" + loginAccount +
                ", loginType=" + loginType +
                "}";
    }
}
