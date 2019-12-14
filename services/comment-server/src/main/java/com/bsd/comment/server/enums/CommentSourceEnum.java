package com.bsd.comment.server.enums;

/**
 * 评论来源枚举类
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 12:29
 */
public enum CommentSourceEnum {
    /**
     * APP客户端
     */
    APP(1, "APP"),
    /**
     * PC端
     */
    PC(2, "PC"),
    /**
     * WAP端
     */
    WAP(3, "WAP"),
    /**
     * 未知来源
     */
    UNKNOW(4, "UNKNOW");

    /**
     * 评论来源编码
     */
    private Integer commentSourceCode;
    /**
     * 评论来源名称
     */
    private String commentSourceName;

    CommentSourceEnum(Integer commentSourceCode, String commentSourceName) {
        this.commentSourceCode = commentSourceCode;
        this.commentSourceName = commentSourceName;
    }

    public Integer getCommentSourceCode() {
        return commentSourceCode;
    }

    public String getCommentSourceName() {
        return commentSourceName;
    }

    public static boolean contain(Integer commentSourceCode) {
        for (CommentSourceEnum commentSourceEnum : CommentSourceEnum.values()) {
            if (commentSourceEnum.commentSourceCode.equals(commentSourceCode)) {
                return true;
            }
        }
        return false;
    }
}
