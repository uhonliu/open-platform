package com.bsd.dingtalk.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 部门信息表
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@TableName("org_department")
public class Department extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableId(value = "department_id", type = IdType.ASSIGN_ID)
    private Long departmentId;

    /**
     * 上级部门ID
     */
    private Integer parentId;

    /**
     * 部门代码
     */
    private String departmentCode;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门级别
     */
    private Integer level;

    /**
     * 显示顺序
     */
    private Long seq;

    /**
     * 状态:0-禁用 1-启用
     */
    private Integer status;

    /**
     * 所属企业ID
     */
    private Long companyId;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 最后修改人
     */
    private Integer updateBy;


    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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
        return "Department{" +
                "departmentId=" + departmentId +
                ", parentId=" + parentId +
                ", departmentCode=" + departmentCode +
                ", departmentName=" + departmentName +
                ", level=" + level +
                ", seq=" + seq +
                ", status=" + status +
                ", companyId=" + companyId +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                "}";
    }
}
