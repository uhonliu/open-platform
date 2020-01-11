package com.bsd.org.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位体系表
 *
 * @author lrx
 * @date 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("org_position")
public class Position extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 岗位ID
     */
    @TableId(value = "position_id", type = IdType.ASSIGN_ID)
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
    private Long departmentId;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 最后修改人
     */
    private Long updateBy;
}
