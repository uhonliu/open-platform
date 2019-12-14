package com.bsd.org.server.enums;

/**
 * 部门级别
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 11:31
 */
public enum DepartmentLevelEnum {

    LEVEL_TOP(1, "一级部门"),
    LEVEL_SECOND(2, "二级部门"),
    LEVEL_THREE(3, "三级部门"),
    LEVEL_FOUR(4, "四级部门");

    /**
     * 部门等级编码
     */
    private Integer levelCode;
    /**
     * 部门等级名称
     */
    private String levelName;


    DepartmentLevelEnum(Integer levelCode, String levelName) {
        this.levelCode = levelCode;
        this.levelName = levelName;
    }

    public Integer getLevelCode() {
        return levelCode;
    }

    public String getLevelName() {
        return levelName;
    }
}
