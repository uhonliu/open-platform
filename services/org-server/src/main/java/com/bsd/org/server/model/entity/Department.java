package com.bsd.org.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门信息表
 *
 * @author lrx
 * @date 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
    private Long parentId;

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
    private Boolean status;

    /**
     * 所属企业ID
     */
    private Long companyId;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 最后修改人
     */
    private Long updateBy;
}
