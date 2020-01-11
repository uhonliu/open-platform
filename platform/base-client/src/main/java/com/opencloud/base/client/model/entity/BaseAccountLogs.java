package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户-登录日志
 *
 * @author liuyadu
 */
@TableName("base_account_logs")
public class BaseAccountLogs implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Date loginTime;

    /**
     * 登录Ip
     */
    private String loginIp;

    /**
     * 登录设备
     */
    private String loginAgent;

    /**
     * 登录次数
     */
    private Integer loginNums;

    private Long userId;

    private String account;

    private String accountType;

    private String accountId;

    private String domain;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return login_time
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * @param loginTime
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 获取登录Ip
     *
     * @return login_ip - 登录Ip
     */
    public String getLoginIp() {
        return loginIp;
    }

    /**
     * 设置登录Ip
     *
     * @param loginIp 登录Ip
     */
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    /**
     * 获取登录设备
     *
     * @return login_agent - 登录设备
     */
    public String getLoginAgent() {
        return loginAgent;
    }

    /**
     * 设置登录设备
     *
     * @param loginAgent 登录设备
     */
    public void setLoginAgent(String loginAgent) {
        this.loginAgent = loginAgent;
    }

    /**
     * 获取登录次数
     *
     * @return login_nums - 登录次数
     */
    public Integer getLoginNums() {
        return loginNums;
    }

    /**
     * 设置登录次数
     *
     * @param loginNums 登录次数
     */
    public void setLoginNums(Integer loginNums) {
        this.loginNums = loginNums;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
