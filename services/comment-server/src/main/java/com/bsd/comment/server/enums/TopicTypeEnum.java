package com.bsd.comment.server.enums;

/**
 * @Author: linrongxin
 * @Date: 2019/9/9 17:38
 */
public enum TopicTypeEnum {
    /**
     * 商品类型
     */
    GOODS("goods", "商品"),
    /**
     * 课程类型
     */
    COURSE("course", "课程"),
    /**
     * 活动类型
     */
    ACTIVITY("activity", "活动"),
    /**
     * 帖子类型
     */
    ARTICLE("article", "帖子");

    /**
     * 主题名称
     */
    private String name;
    /**
     * 主题编码
     */
    private String code;

    TopicTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
