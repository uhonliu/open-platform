package com.bsd.migration.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: linrongxin
 * @Date: 2019/10/8 15:43
 */
@Data
public class PageResult<T> {
    private Integer current;
    private Integer pages;
    private List<T> records;
    private Boolean searchCount;
    private Integer size;
    private Integer total;
}
