package com.bsd.user.server.model.dto;

import lombok.Data;

/**
 * 用户账号DTO
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 16:28
 */
@Data
public class UserAccountDTO {
    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 第三方应用的唯一标识
     */
    private String openid;

    /**
     * 第三方unionid
     */
    private String unionid;
}
