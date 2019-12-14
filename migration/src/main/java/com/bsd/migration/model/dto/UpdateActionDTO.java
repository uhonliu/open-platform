package com.bsd.migration.model.dto;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/10/8 17:00
 */
@Data
public class UpdateActionDTO {
    private Long actionId;
    private String actionCode;
    private String actionName;
    private Long menuId;
    private Integer status;
    private String actionDesc;
    private Integer priority;
}
