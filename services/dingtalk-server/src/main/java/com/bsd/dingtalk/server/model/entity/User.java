package com.bsd.dingtalk.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

import java.util.Date;

/**
 * 人员信息表（钉钉）
 *
 * @author liujianhong
 * @date 2019-07-01
 */
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
    @TableId(value = "dd_userid", type = IdType.ASSIGN_ID)
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
    private Integer active;

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


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getDdUserid() {
        return ddUserid;
    }

    public void setDdUserid(String ddUserid) {
        this.ddUserid = ddUserid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(Date hiredDate) {
        this.hiredDate = hiredDate;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", parentId=" + parentId +
                ", positionId=" + positionId +
                ", ddUserid=" + ddUserid +
                ", unionid=" + unionid +
                ", name=" + name +
                ", tel=" + tel +
                ", workPlace=" + workPlace +
                ", remark=" + remark +
                ", mobile=" + mobile +
                ", email=" + email +
                ", orgEmail=" + orgEmail +
                ", active=" + active +
                ", department=" + department +
                ", position=" + position +
                ", avatar=" + avatar +
                ", hiredDate=" + hiredDate +
                ", jobnumber=" + jobnumber +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                "}";
    }
}
