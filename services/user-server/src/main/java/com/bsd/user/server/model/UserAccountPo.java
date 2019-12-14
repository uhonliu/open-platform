package com.bsd.user.server.model;

import com.bsd.user.server.model.entity.UserAccount;

import java.util.Date;

/**
 * 第三方账号信息扩展类
 *
 * @author lisongmao
 * 2019年7月1日
 */
public class UserAccountPo extends UserAccount {
    private static final long serialVersionUID = -2183387760761576190L;

    public UserAccountPo(Long accountId, Integer platform, Long userId, String openid, String unionid, String avatar,
                         String nickname, Integer gender, String language, String city, String province, String country,
                         String countryCode, String mobile, Date createTime, Date updateTime) {
        this.setAccountId(accountId);
        this.setPlatform(platform);
        this.setUserId(userId);
        this.setOpenid(openid);
        this.setUnionid(unionid);
        this.setAvatar(avatar);
        this.setNickname(nickname);
        this.setGender(gender);
        this.setLanguage(language);
        this.setCity(city);
        this.setProvince(province);
        this.setCountry(countryCode);
        this.setCountryCode(countryCode);
        this.setMobile(mobile);
        this.setCreateTime(createTime);
        this.setUpdateTime(updateTime);
    }

    public UserAccountPo() {

    }

    /**
     * 登录ip
     */
    private String loginIp;
    /**
     * 第三方账号登录返回的code
     */
    private String code;
    /**
     * 用户中心账号手机号
     */
    private String moblie;
    /**
     * 手机号验证码
     */
    private String mobileCode;
    /**
     * 用户中心账号密码
     */
    private String password;
    /**
     * 第三方账号平台
     */
    private Integer platform;

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Integer getPlatform() {
        return platform;
    }

    @Override
    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }
}
