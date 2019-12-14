package com.bsd.comment.server.constants;

/**
 * 评论常量
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 12:22
 */
public class CommentConst {
    /**
     * 分页默认页码
     */
    public static final int DEFAULT_PAGE_INDEX = 1;
    /**
     * 分页默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 批量修改最大条数
     */
    public static final int BACTH_UPDATE_MAX_SIZE = 1000;
    /**
     * 最大置顶条数
     */
    public static final int TOP_COMMENT_MAX_SIZE = 5;
    /**
     * 普通回复
     */
    public static final int USER_REPLY = 1;
    /**
     * 后台回复
     */
    public static final int ADMIN_REPLY = 2;

    /**
     * 点赞
     */
    public static final int ACTION_LIKE = 1;
    /**
     * 点踩
     */
    public static final int ACTION_UN_LIKE = 2;
}
