package com.bsd.user.server.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: linrongxin
 * @Date: 2019/8/22 17:39
 */
@Data
public class UserDetailVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 0-跨境知道 1-卖家成长 3-人工录入
     */
    private Integer source;

    /**
     * 用户来源
     */
    private String sourceStr;

    /**
     * 性别 (0 保密  1男 2女)
     */
    private Integer sex;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
}
