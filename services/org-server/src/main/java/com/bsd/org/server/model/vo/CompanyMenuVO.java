package com.bsd.org.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: linrongxin
 * @Date: 2019/9/25 15:15
 */
@Data
public class CompanyMenuVO {
    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    /**
     * 菜单title
     */
    @ApiModelProperty(value = "菜单title")
    private String title;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    /**
     * 公司英文名称
     */
    @ApiModelProperty(value = "公司英文名称")
    private String companyNameEn;
    /**
     * 公司下的部门
     */
    @ApiModelProperty(value = "公司下的部门")
    private List<DepartmentMenuVO> children;

    public String getTitle() {
        return this.companyName;
    }
}
