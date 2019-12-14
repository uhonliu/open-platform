package com.bsd.migration.model.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: liujianhong
 * @Date: 2019/10/10 10:17
 */
@Data
public class Config implements Serializable {
    /**
     * 网关地址
     */
    private String gatewayUrl;

    /**
     * AppKey
     */
    private String clientId;

    /**
     * AppSecret
     */
    private String clientSecret;
}
