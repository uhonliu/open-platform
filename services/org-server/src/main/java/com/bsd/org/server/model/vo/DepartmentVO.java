package com.bsd.org.server.model.vo;

import com.bsd.org.server.model.entity.Department;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门VO对象
 *
 * @Author: linrongxin
 * @Date: 2019/9/18 16:34
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartmentVO extends Department {
    /**
     * 上级部门名称
     */
    private String parentName;

    /**
     * 公司名称
     */
    private String companyName;
}
