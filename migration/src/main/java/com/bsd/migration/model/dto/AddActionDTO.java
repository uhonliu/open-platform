package com.bsd.migration.model.dto;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/10/8 16:56
 */
@Data
public class AddActionDTO {
    private String actionCode;
    private String actionName;
    private Long menuId;
    private Integer status;
    private String actionDesc;
    private Integer priority;
}
