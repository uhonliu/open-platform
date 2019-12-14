package com.bsd.comment.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.comment.server.constants.CommentConst;
import com.bsd.comment.server.enums.CommentStatusEnum;
import com.bsd.comment.server.mapper.CommentMapper;
import com.bsd.comment.server.mapper.CommentReplyMapper;
import com.bsd.comment.server.model.entity.Comment;
import com.bsd.comment.server.model.entity.CommentReply;
import com.bsd.comment.server.service.CommentReplyService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.security.OpenHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 评论回复表 服务实现类
 *
 * @author lrx
 * @date 2019-09-09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentReplyServiceImpl extends BaseServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyService {
    @Resource
    private CommentReplyMapper commentReplyMapper;

    @Resource
    private CommentMapper commentMapper;

    /**
     * 获取评论下的所有回复
     *
     * @param commentId
     * @return
     */
    @Override
    public List<CommentReply> listByCommentId(Long commentId, boolean isAdmin) {
        //获取评论信息
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new OpenAlertException("找不到评论信息");
        }
        if (!isAdmin && CommentStatusEnum.SHIELD.getCommentStatusCode().equals(comment.getStatus())) {
            throw new OpenAlertException("评论为屏蔽状态");
        }
        LambdaQueryWrapper<CommentReply> lambdaQueryWrapper = Wrappers.<CommentReply>lambdaQuery().eq(CommentReply::getCommentId, commentId).orderByDesc(CommentReply::getCreateTime);
        if (!isAdmin) {
            //客户端获取回复,只能获取未屏蔽的回复
            lambdaQueryWrapper.eq(CommentReply::getIsShield, false);
        }
        return commentReplyMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 添加回复数据
     *
     * @param commentReply
     * @return
     */
    @Override
    public boolean saveCommentReply(CommentReply commentReply) {
        //获取评论信息
        Comment comment = commentMapper.selectById(commentReply.getCommentId());
        if (comment == null) {
            throw new OpenAlertException("找不到回复评论信息");
        }
        if (CommentStatusEnum.SHIELD.getCommentStatusCode().equals(comment.getStatus())) {
            throw new OpenAlertException("评论为屏蔽状态，请勿回复");
        }
        if (commentReply.getParentId() == null) {
            //回复评论,验证用户
            if (!comment.getUserId().equals(commentReply.getToUserId())) {
                throw new OpenAlertException("回复评论时,回复目标ID有误");
            }
        } else {
            //回复回复
            CommentReply dbCommentReply = commentReplyMapper.selectById(commentReply.getParentId());
            if (dbCommentReply == null) {
                throw new OpenAlertException("目标回复信息不存在");
            }
            if (!dbCommentReply.getFromUserId().equals(commentReply.getToUserId())) {
                throw new OpenAlertException("回复目标ID有误");
            }
        }
        //回复类型
        Integer isAuthor = commentReply.getIsAuthor();
        if (CommentConst.USER_REPLY != isAuthor && CommentConst.ADMIN_REPLY != isAuthor) {
            throw new OpenAlertException("回复类型错误,请按照规定填写");
        }
        if (commentReply.getParentId() == null) {
            //回复评论,ParentId设置为0
            commentReply.setParentId(0L);
        }
        commentReply.setIsShield(false);
        commentReply.setCreateBy(OpenHelper.getUser().getUserId());
        commentReply.setCreateTime(new Date());

        //保存回复
        int saveCount = commentReplyMapper.insert(commentReply);
        //更新评论次数与状态[后续使用异步定时更新]
        int oldReplyCount = comment.getReplyNum();
        comment.setReplyNum(oldReplyCount + 1);
        comment.setUpdateBy(OpenHelper.getUser().getUserId());
        comment.setUpdateTime(new Date());
        if (CommentStatusEnum.NO_RESPONSE.getCommentStatusCode().equals(comment.getStatus())) {
            comment.setStatus(CommentStatusEnum.RESPONSED.getCommentStatusCode());
        }
        int updateCount = commentMapper.update(comment, Wrappers.<Comment>lambdaUpdate().eq(Comment::getCommentId, comment.getCommentId()).eq(Comment::getReplyNum, oldReplyCount));

        return saveCount > 0 && updateCount > 0;
    }


    /**
     * 批量屏蔽用户回复
     *
     * @param replyIds
     * @return
     */
    @Override
    public boolean shieldCommentReply(List<String> replyIds) {
        //获取回复列表
        List<CommentReply> commentReplies = getCommentReplys(replyIds);
        //修改成屏蔽状态
        commentReplies.forEach(x -> {
            x.setIsShield(true);
            x.setUpdateBy(OpenHelper.getUser().getUserId());
            x.setUpdateTime(new Date());
        });
        //更新
        return updateBatchById(commentReplies);
    }

    /**
     * 根据回复ID获取回复信息
     *
     * @param replyIds
     * @return
     */
    private List<CommentReply> getCommentReplys(List<String> replyIds) {
        //简单校验
        if (replyIds == null || replyIds.size() == 0) {
            throw new OpenAlertException("回复ID列表不能为空,请检查回复ID是否正确");
        }
        //查询评论
        List<CommentReply> commentReplies = commentReplyMapper.selectBatchIds(replyIds);
        if (commentReplies == null || commentReplies.size() == 0) {
            throw new OpenAlertException("查询不到回复列表信息,请检查回复ID是否正确");
        }
        if (commentReplies.size() != commentReplies.size()) {
            throw new OpenAlertException("未查询到所有回复列表信息,请检查回复ID是否正确");
        }
        if (commentReplies.size() > CommentConst.BACTH_UPDATE_MAX_SIZE) {
            throw new OpenAlertException("批量修改最多支持一次修改1000条数据");
        }
        return commentReplies;
    }
}
