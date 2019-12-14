package com.bsd.org.server.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: linrongxin
 * @Date: 2019/8/27 18:39
 */
@Data
public class UserDetailVO {
    /**
     * 人员ID
     */
    private Long userId;

    /**
     * 上级ID
     */
    private Long parentId;

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
    private Long departmentId;

    /**
     * 部门列表字符串
     */
    private String department;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 入职时间
     */
    private Date hiredDate;

    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 岗位代码
     */
    private String positionCode;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 上级名字
     */
    private String parentName;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;
}
