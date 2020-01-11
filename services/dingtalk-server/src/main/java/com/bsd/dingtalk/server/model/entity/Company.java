package com.bsd.dingtalk.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 企业信息表
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@TableName("org_company")
public class Company extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 企业ID
     */
    @TableId(value = "company_id", type = IdType.ASSIGN_ID)
    private Integer companyId;

    /**
     * 企业全称
     */
    private String companyName;

    /**
     * 企业英文名
     */
    private String companyNameEn;

    /**
     * 企业性质ID
     */
    private Integer natureId;

    /**
     * 所属行业ID
     */
    private Integer industryId;

    /**
     * 所在区域ID
     */
    private Integer areaId;

    /**
     * 成立时间
     */
    private LocalDateTime establishedTime;

    /**
     * 注册资金
     */
    private BigDecimal registeredCapital;

    /**
     * 员工人数
     */
    private Integer staffNum;

    /**
     * 公司网址
     */
    private String website;

    /**
     * 公司介绍
     */
    private String profile;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 电话
     */
    private String phone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 通信地址
     */
    private String address;

    /**
     * 邮政编码
     */
    private String postCode;

    /**
     * 企业Logo
     */
    private String logo;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 最后修改人
     */
    private Integer updateBy;


    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNameEn() {
        return companyNameEn;
    }

    public void setCompanyNameEn(String companyNameEn) {
        this.companyNameEn = companyNameEn;
    }

    public Integer getNatureId() {
        return natureId;
    }

    public void setNatureId(Integer natureId) {
        this.natureId = natureId;
    }

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public LocalDateTime getEstablishedTime() {
        return establishedTime;
    }

    public void setEstablishedTime(LocalDateTime establishedTime) {
        this.establishedTime = establishedTime;
    }

    public BigDecimal getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(BigDecimal registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public Integer getStaffNum() {
        return staffNum;
    }

    public void setStaffNum(Integer staffNum) {
        this.staffNum = staffNum;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", companyName=" + companyName +
                ", companyNameEn=" + companyNameEn +
                ", natureId=" + natureId +
                ", industryId=" + industryId +
                ", areaId=" + areaId +
                ", establishedTime=" + establishedTime +
                ", registeredCapital=" + registeredCapital +
                ", staffNum=" + staffNum +
                ", website=" + website +
                ", profile=" + profile +
                ", contact=" + contact +
                ", phone=" + phone +
                ", fax=" + fax +
                ", email=" + email +
                ", address=" + address +
                ", postCode=" + postCode +
                ", logo=" + logo +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                "}";
    }
}
