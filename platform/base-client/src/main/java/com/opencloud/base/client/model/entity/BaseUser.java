package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.annotation.TableAlias;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 系统用户-基础信息
 *
 * @author liuyadu
 */
@TableAlias("user")
@TableName("base_user")
public class BaseUser extends AbstractEntity {
    private static final long serialVersionUID = -735161640894047414L;

    /**
     * 系统用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    /**
     * 登陆名
     */
    private String userName;

    /**
     * 用户类型:super-超级管理员 normal-普通管理员
     */
    private String userType;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 昵称
     */
    private String nickName;

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
     * 描述
     */
    private String userDesc;

    /**
     * 密码
     */
    @JsonIgnore
    @TableField(exist = false)
    private String password;

    /**
     * 状态:0-禁用 1-正常 2-锁定
     */
    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
