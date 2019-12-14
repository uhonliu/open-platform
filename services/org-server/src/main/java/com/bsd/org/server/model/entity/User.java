package com.bsd.org.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 人员信息表（钉钉）
 *
 * @author lrx
 * @date 2019-08-14
 */
@Data
@TableName("org_user")
public class User extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 人员ID
     */
    private Long userId;

    /**
     * 上级ID
     */
    private Long parentId;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 员工在当前企业内的唯一标识
     */
    private String ddUserid;

    /**
     * 员工在当前开发者企业账号范围内的唯一标识
     */
    private String unionid;

    /**
     * 员工名字
     */
    private String name;

    /**
     * 分机号（仅限企业内部开发调用）
     */
    private String tel;

    /**
     * 办公地点
     */
    private String workPlace;

    /**
     * 备注
     */
    private String remark;

    /**
     * 手机号码
     */
    private String mobile;


    /**
     * 员工的电子邮箱
     */
    private String email;

    /**
     * 员工的企业邮箱
     */
    private String orgEmail;

    /**
     * 是否已经激活:1已激活，0未激活
     */
    private Boolean active;

    /**
     * 成员所属部门id列表
     */
    private String department;

    /**
     * 职位信息
     */
    private String position;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 入职时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date hiredDate;

    /**
     * 员工工号
     */
    private String jobnumber;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 最后修改人
     */
    private Long updateBy;
}
