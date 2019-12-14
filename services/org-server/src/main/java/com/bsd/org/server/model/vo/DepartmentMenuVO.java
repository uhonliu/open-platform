package com.bsd.org.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: linrongxin
 * @Date: 2019/9/25 15:16
 */
@Data
public class DepartmentMenuVO {
    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;
    /**
     * 部门编码
     */
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;
    /**
     * 菜单title
     */
    @ApiModelProperty(value = "菜单title")
    private String title;
    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    /**
     * 下级部门
     */
    @ApiModelProperty(value = "下级部门")
    private List<DepartmentMenuVO> children;

    public String getTitle() {
        return this.departmentName;
    }
}
