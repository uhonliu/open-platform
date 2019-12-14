package com.bsd.comment.server.enums;

/**
 * 评论状态
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 14:15
 */
public enum CommentStatusEnum {
    /**
     * 未审核
     */
    NOT_AUDITED(1, "未审核"),
    /**
     * 未回复
     */
    NO_RESPONSE(2, "未回复"),
    /**
     * 已回复
     */
    RESPONSED(3, "已回复"),
    /**
     * 已屏蔽
     */
    SHIELD(4, "已屏蔽");

    /**
     * 评论状态名称
     */
    private String commentStatusName;
    /**
     * 评论状态编码
     */
    private Integer commentStatusCode;

    CommentStatusEnum(Integer commentStatusCode, String commentStatusName) {
        this.commentStatusCode = commentStatusCode;
        this.commentStatusName = commentStatusName;
    }

    public String getCommentStatusName() {
        return commentStatusName;
    }

    public Integer getCommentStatusCode() {
        return commentStatusCode;
    }
}
