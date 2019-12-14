package com.bsd.user.server.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 16:34
 */
@Data
public class CaptchaInitDTO implements Serializable {
    private String userId;
    private String clientType;
    private String ip;
    private Integer gtServerStatus;
}
