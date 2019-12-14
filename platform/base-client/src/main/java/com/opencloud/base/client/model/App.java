package com.opencloud.base.client.model;

import com.opencloud.base.client.model.entity.BaseApp;

import java.io.Serializable;

/**
 * @author: liujianhong
 * @date: 2019/10/11 9:35
 * @description:
 */
public class App extends BaseApp implements Serializable {
    private static final long serialVersionUID = 6717800085953996702L;

    /**
     * 昵称
     */
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
