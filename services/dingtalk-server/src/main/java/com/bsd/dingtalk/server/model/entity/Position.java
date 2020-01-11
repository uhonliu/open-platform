package com.bsd.dingtalk.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 岗位体系表
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@TableName("org_position")
public class Position extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 岗位ID
     */
    @TableId(value = "position_id", type = IdType.ASSIGN_ID)
    private Integer positionId;

    /**
     * 岗位代码
     */
    private String positionCode;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 工作内容
     */
    private String workContent;

    /**
     * 工作标准
     */
    private String workStandard;

    /**
     * 责任权重
     */
    private String responsibilityWeight;

    /**
     * 所需资格条件
     */
    private String requiredQualifications;

    /**
     * 状态:0-禁用 1-启用
     */
    private Boolean status;

    /**
     * 显示顺序
     */
    private Integer seq;

    /**
     * 所属部门ID
     */
    private Integer departmentId;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 最后修改人
     */
    private Integer updateBy;


    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public String getWorkStandard() {
        return workStandard;
    }

    public void setWorkStandard(String workStandard) {
        this.workStandard = workStandard;
    }

    public String getResponsibilityWeight() {
        return responsibilityWeight;
    }

    public void setResponsibilityWeight(String responsibilityWeight) {
        this.responsibilityWeight = responsibilityWeight;
    }

    public String getRequiredQualifications() {
        return requiredQualifications;
    }

    public void setRequiredQualifications(String requiredQualifications) {
        this.requiredQualifications = requiredQualifications;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
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
        return "Position{" +
                "positionId=" + positionId +
                ", positionCode=" + positionCode +
                ", positionName=" + positionName +
                ", workContent=" + workContent +
                ", workStandard=" + workStandard +
                ", responsibilityWeight=" + responsibilityWeight +
                ", requiredQualifications=" + requiredQualifications +
                ", status=" + status +
                ", seq=" + seq +
                ", departmentId=" + departmentId +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                "}";
    }
}
