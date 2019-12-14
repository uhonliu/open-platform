package com.bsd.user.server.model;

import com.bsd.user.server.model.entity.UserLoginLogs;

import java.util.Date;

/**
 * UserLoginLogs 扩展类
 *
 * @author lisongmao
 * 2019年6月28日
 */
public class UserLoginLogsPo extends UserLoginLogs {
    private static final long serialVersionUID = 5973011789016760599L;

    public UserLoginLogsPo() {

    }

    public UserLoginLogsPo(Long logId, Long userId, Long accountId, String loginIp, Date loginTime,
                           String loginAgent, Integer loginNums, String loginAccount, Integer loginType) {
        super();
        this.setLogId(logId);
        this.setUserId(userId);
        this.setAccountId(accountId);
        this.setLoginIp(loginIp);
        this.setLoginTime(loginTime);
        this.setLoginAgent(loginAgent);
        this.setLoginNums(loginNums);
        this.setLoginAccount(loginAccount);
        this.setLoginType(loginType);
    }
}
