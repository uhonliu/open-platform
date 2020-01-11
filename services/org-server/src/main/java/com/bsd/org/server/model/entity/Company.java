package com.bsd.org.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业信息表
 *
 * @author lrx
 * @date 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("org_company")
public class Company extends AbstractEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 企业ID
     */
    @TableId(value = "company_id", type = IdType.ASSIGN_ID)
    private Long companyId;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date establishedTime;

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
    private Long createBy;

    /**
     * 最后修改人
     */
    private Long updateBy;
}
