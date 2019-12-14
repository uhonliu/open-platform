package com.bsd.user.server.model;

import com.bsd.user.server.model.entity.ConsigneeAddress;

public class ConsigneeAddressPo extends ConsigneeAddress {
    /**
     * 当前登录用户手机
     **/
    public String loginMobile;

    public String getLoginMobile() {
        return loginMobile;
    }

    public void setLoginMobile(String loginMobile) {
        this.loginMobile = loginMobile;
    }
}
