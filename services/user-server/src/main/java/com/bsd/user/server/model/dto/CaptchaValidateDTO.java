package com.bsd.user.server.model.dto;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 16:36
 */
@Data
public class CaptchaValidateDTO {
    private String chllenge;
    private String validate;
    private String seccode;
    private String userId;
    private String ip;
    private String clientType;
    private Integer gtServerStatus;
}

