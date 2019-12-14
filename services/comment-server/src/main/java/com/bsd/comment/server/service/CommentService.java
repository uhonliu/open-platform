package com.bsd.comment.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.comment.server.enums.CommentStatusEnum;
import com.bsd.comment.server.model.entity.Comment;
import com.bsd.comment.server.model.query.CommentQuery;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 评论表 服务类
 *
 * @author lrx
 * @date 2019-09-09
 */
public interface CommentService extends IBaseService<Comment> {
    /**
     * 保存评论
     *
     * @param comment
     * @return
     */
    boolean saveComment(Comment comment);

    /**
     * 分页数据查询
     *
     * @param commentQuery
     * @return
     */
    IPage<Comment> commentPage(CommentQuery commentQuery, boolean isAdmin);

    /**
     * 批量修改状态
     *
     * @param toStatus   修改后状态
     * @param commentIds 评论ID列表
     * @return
     */
    boolean changeStatus(CommentStatusEnum toStatus, List<String> commentIds);

    /**
     * 批量回复
     *
     * @param asList
     * @param replyContent
     * @return
     */
    boolean batchReply(List<String> asList, String replyContent);

    /**
     * 置顶评论
     *
     * @param commentId
     * @return
     */
    boolean setCommentToTop(String commentId);

    /**
     * 评论行为操作
     *
     * @param commentId
     * @param action
     * @return
     */
    boolean doCommentAction(Long commentId, Integer action);
}
