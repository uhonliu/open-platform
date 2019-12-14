package com.bsd.user.server.model.vo;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/8/22 16:28
 */
@Data
public class UserVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 0-跨境知道 1-卖家成长 3-人工录入
     */
    private Integer source;

    /**
     * 状态:0-禁用 1-启用 2-锁定
     */
    private Integer status;

    /**
     * 性别 (0 保密  1男 2女)
     */
    private Integer sex;

    /**
     * 用户类型:0-普通 1-潜在客户 2-客户
     */
    private Integer userType;

}
