package com.bsd.org.server.model.vo;

import com.bsd.org.server.model.entity.Position;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职位信息VO
 *
 * @Author: linrongxin
 * @Date: 2019/9/18 15:27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PositionVO extends Position {
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
}
