package com.bsd.comment.server.service;

import com.bsd.comment.server.model.entity.CommentReply;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 评论回复表 服务类
 *
 * @author lrx
 * @date 2019-09-09
 */
public interface CommentReplyService extends IBaseService<CommentReply> {
    /**
     * 获取评论下的所有回复
     *
     * @param commentId
     * @return
     */
    List<CommentReply> listByCommentId(Long commentId, boolean isAdmin);

    /**
     * 添加回复数据
     *
     * @param commentReply
     * @return
     */
    boolean saveCommentReply(CommentReply commentReply);

    /**
     * 批量屏蔽回复
     *
     * @param asList
     * @return
     */
    boolean shieldCommentReply(List<String> asList);
}
