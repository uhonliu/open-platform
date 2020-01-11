package com.bsd.user.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 用户-第三方账号
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@TableName("bsd_user_account")
public class UserAccount extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "account_id", type = IdType.ASSIGN_ID)
    private Long accountId;

    /**
     * 账户类型:1-微信移动应用 、2-微信网站应用、3-微信公众号、4-微信小程序、5-QQ、6-微博
     */
    private Integer platform;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 第三方应用的唯一标识
     */
    private String openid;

    private String unionid;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别 0：未知、1：男、2：女
     */
    private Integer gender;

    /**
     * 语言
     */
    private String language;

    /**
     * 城市
     */
    private String city;

    /**
     * 省
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 手机号码国家编码
     */
    private String countryCode;

    /**
     * 手机号码
     */
    private String mobile;


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Override
    public String toString() {
        return "UserAccount{" +
                "accountId=" + accountId +
                ", platform=" + platform +
                ", userId=" + userId +
                ", openid=" + openid +
                ", unionid=" + unionid +
                ", avatar=" + avatar +
                ", nickname=" + nickname +
                ", gender=" + gender +
                ", language=" + language +
                ", city=" + city +
                ", province=" + province +
                ", country=" + country +
                ", countryCode=" + countryCode +
                ", mobile=" + mobile +
                "}";
    }
}
