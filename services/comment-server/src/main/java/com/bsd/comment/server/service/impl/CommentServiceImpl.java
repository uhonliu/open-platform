package com.bsd.comment.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.comment.server.constants.CommentConst;
import com.bsd.comment.server.enums.CommentSourceEnum;
import com.bsd.comment.server.enums.CommentStatusEnum;
import com.bsd.comment.server.mapper.CommentMapper;
import com.bsd.comment.server.model.entity.Comment;
import com.bsd.comment.server.model.entity.CommentReply;
import com.bsd.comment.server.model.query.CommentQuery;
import com.bsd.comment.server.service.CommentReplyService;
import com.bsd.comment.server.service.CommentService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.security.OpenHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 评论表 服务实现类
 *
 * @author lrx
 * @date 2019-09-09
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl extends BaseServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Autowired
    private CommentReplyService commentReplyService;

    /**
     * 保存评论
     *
     * @param comment
     * @return
     */
    @Override
    public boolean saveComment(Comment comment) {
        //判断评论来源
        if (!CommentSourceEnum.contain(comment.getSource())) {
            comment.setSource(CommentSourceEnum.UNKNOW.getCommentSourceCode());
        }
        //初始化评论数据
        initComment(comment);
        //保存评论数据
        return commentMapper.insert(comment) > 0;
    }

    /**
     * 初始化评论数据
     *
     * @param comment
     */
    private void initComment(Comment comment) {
        //创建者ID
        comment.setCreateBy(OpenHelper.getUser().getUserId());
        //创建时间
        comment.setCreateTime(new Date());
        //初始状态(默认为审核通过,未回复状态)
        comment.setStatus(CommentStatusEnum.NO_RESPONSE.getCommentStatusCode());
        //默认不置顶
        comment.setIsTop(false);
        //初始点赞个数
        comment.setLikeNum(0);
        //初始点踩个数
        comment.setUnLikeNum(0);
        //初始回复数
        comment.setReplyNum(0);
    }

    /**
     * 分页查询评论数据
     *
     * @param commentQuery
     * @return
     */
    @Override
    public IPage<Comment> commentPage(CommentQuery commentQuery, boolean isAdmin) {
        //分页配置
        if (commentQuery.getPageIndex() == null) {
            commentQuery.setPageIndex(CommentConst.DEFAULT_PAGE_INDEX);
        }
        if (commentQuery.getPageSize() == null) {
            commentQuery.setPageSize(CommentConst.DEFAULT_PAGE_SIZE);
        }
        Page<Comment> pageConfig = new Page<Comment>(commentQuery.getPageIndex(), commentQuery.getPageSize());
        //查询条件
        LambdaQueryWrapper<Comment> query = Wrappers.<Comment>lambdaQuery()
                .eq(StringUtils.isNotEmpty(commentQuery.getCommentType()), Comment::getTopicType, commentQuery.getCommentType());
        if (StringUtils.isNotEmpty(commentQuery.getSearchContent())) {
            query.and(x -> x.eq(Comment::getTopicName, commentQuery.getSearchContent()).or()
                    .eq(Comment::getUserName, commentQuery.getSearchContent()));
        }
        //用户只能看到未回复或已经回复的评论
        if (!isAdmin) {
            query.in(Comment::getStatus, CommentStatusEnum.NO_RESPONSE.getCommentStatusCode(), CommentStatusEnum.RESPONSED.getCommentStatusCode());
        }
        query.orderByDesc(Comment::getIsTop).orderByDesc(Comment::getCreateTime);
        //分页查询结果
        return commentMapper.selectPage(pageConfig, query);
    }

    /**
     * 批量回复
     *
     * @param commentIds
     * @param replyContent
     * @return
     */
    @Override
    public boolean batchReply(List<String> commentIds, String replyContent) {
        List<Comment> comments = getComments(commentIds);
        List<CommentReply> commentReplies = createBatchCommentReply(comments, replyContent);
        //批量添加回复
        boolean isSaveSuc = commentReplyService.saveBatch(commentReplies);
        if (!isSaveSuc) {
            throw new OpenAlertException("评论回复添加回复数据失败");
        }
        //修改评论
        comments.forEach(x -> {
            //回复次数加1
            x.setReplyNum(x.getReplyNum() + 1);
            x.setUpdateBy(OpenHelper.getUser().getUserId());
            x.setUpdateTime(new Date());
            if (CommentStatusEnum.NO_RESPONSE.getCommentStatusCode().equals(x.getStatus())) {
                x.setStatus(CommentStatusEnum.RESPONSED.getCommentStatusCode());
            }
        });
        //更新评论
        boolean isUpdateSuc = updateBatchById(comments);
        if (!isUpdateSuc) {
            throw new OpenAlertException("更新评论失败");
        }
        return true;
    }

    /**
     * 评论置顶
     *
     * @param commentId
     * @return
     */
    @Override
    public boolean setCommentToTop(String commentId) {
        //获取评论信息
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new OpenAlertException("未找到置顶评论信息");
        }
        if (comment.getIsTop()) {
            throw new OpenAlertException("评论已经置顶,请勿重复操作");
        }
        comment.setIsTop(true);
        comment.setUpdateTime(new Date());
        comment.setUpdateBy(OpenHelper.getUser().getUserId());
        //置顶评论列表
        List<Comment> topComments = getCommentTopList(comment.getTopicType());
        if (topComments == null || topComments.size() < CommentConst.TOP_COMMENT_MAX_SIZE) {
            return commentMapper.updateById(comment) > 0;
        } else {
            //获取需要移除置顶的评论
            List<Comment> removeComments = getRemoveTopList(topComments);
            return commentMapper.updateById(comment) > 0 && updateBatchById(removeComments);
        }
    }

    /**
     * 评论行为操作
     *
     * @param commentId
     * @param action
     * @return
     */
    @Override
    public boolean doCommentAction(Long commentId, Integer action) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new OpenAlertException("未找到评论信息");
        }
        //查询条件
        LambdaUpdateWrapper<Comment> lambdaUpdateWrapper = Wrappers.<Comment>lambdaUpdate();
        //判断操作类型
        if (CommentConst.ACTION_LIKE == action) {
            //点赞
            lambdaUpdateWrapper.eq(Comment::getLikeNum, comment.getLikeNum());
            comment.setLikeNum(comment.getLikeNum() + 1);
        } else if (CommentConst.ACTION_UN_LIKE == action) {
            //点踩
            lambdaUpdateWrapper.eq(Comment::getUnLikeNum, comment.getUnLikeNum());
            comment.setUnLikeNum(comment.getUnLikeNum() + 1);
        } else {
            throw new OpenAlertException("用户操作评论行为错误");
        }
        lambdaUpdateWrapper.eq(Comment::getCommentId, comment.getCommentId());
        //更新评论点赞,点踩数
        comment.setUpdateBy(OpenHelper.getUser().getUserId());
        comment.setUpdateTime(new Date());
        int effectCount = commentMapper.update(comment, lambdaUpdateWrapper);
        return effectCount > 0;
    }

    /**
     * 获取要移除的置顶信息
     *
     * @param topComments
     * @return
     */
    private List<Comment> getRemoveTopList(List<Comment> topComments) {
        //暂时以更新时间排序处理,后续再确定处理逻辑
        List<Comment> removeComments = new ArrayList<Comment>();
        //置顶评论排序
        topComments.sort((o1, o2) -> {
            long t1 = o1.getUpdateTime().getTime();
            long t2 = o2.getUpdateTime().getTime();
            return (int) (t1 - t2);
        });
        //获取需要移除的置顶评论列表
        int removeSize = topComments.size() - (CommentConst.TOP_COMMENT_MAX_SIZE - 1);
        for (int i = 0; i < removeSize; i++) {
            removeComments.add(topComments.get(i));
        }
        //遍历修改置顶状态
        removeComments.forEach(x -> {
            x.setIsTop(false);
            x.setUpdateTime(new Date());
            x.setUpdateBy(OpenHelper.getUser().getUserId());
        });
        return removeComments;
    }

    /**
     * 获取置顶评论
     *
     * @param topicType
     * @return
     */
    private List<Comment> getCommentTopList(String topicType) {
        List<Comment> topComments = commentMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getIsTop, true).eq(Comment::getTopicType, topicType));
        return topComments;
    }

    /**
     * 创建批量回复列表
     *
     * @param comments
     * @return
     */
    private List<CommentReply> createBatchCommentReply(List<Comment> comments, String replyContent) {
        List<CommentReply> commentReplies = new ArrayList<CommentReply>();
        comments.forEach(x -> {
            CommentReply commentReply = new CommentReply();
            commentReply.setCommentId(x.getCommentId());
            commentReply.setParentId(0L);//回复评论,ParentId设置为0
            commentReply.setFromUserId(OpenHelper.getUser().getUserId());
            commentReply.setFromUserName(OpenHelper.getUser().getUsername());
            commentReply.setContent(replyContent);
            commentReply.setToUserId(x.getUserId());
            commentReply.setIsAuthor(CommentConst.ADMIN_REPLY);
            commentReply.setIsShield(false);
            commentReply.setCreateBy(OpenHelper.getUser().getUserId());
            commentReply.setCreateTime(new Date());
            commentReplies.add(commentReply);
        });
        return commentReplies;
    }

    /**
     * 批量修改状态
     *
     * @param toStatus   修改后状态
     * @param commentIds 评论ID列表
     * @return
     */
    @Override
    public boolean changeStatus(CommentStatusEnum toStatus, List<String> commentIds) {
        List<Comment> comments = getComments(commentIds);
        if (CommentStatusEnum.NO_RESPONSE.equals(toStatus)) {
            //批量审核
            return batchAudited(comments);
        } else if (CommentStatusEnum.SHIELD.equals(toStatus)) {
            //批量屏蔽
            return batchShield(comments);
        } else {
            throw new OpenAlertException("不支持修改的状态");
        }
    }


    /**
     * 根据评论ID列表获取评论列表信息
     *
     * @param commentIds
     * @return
     */
    private List<Comment> getComments(List<String> commentIds) {
        //简单校验
        if (commentIds == null || commentIds.size() == 0) {
            throw new OpenAlertException("评论ID列表不能为空,请检查评论ID是否正确");
        }
        //查询评论
        List<Comment> comments = commentMapper.selectBatchIds(commentIds);
        if (comments == null || comments.size() == 0) {
            throw new OpenAlertException("查询不到评论列表信息,请检查评论ID是否正确");
        }
        if (commentIds.size() != comments.size()) {
            throw new OpenAlertException("未查询到所有评论列表信息,请检查评论ID是否正确");
        }
        if (comments.size() > CommentConst.BACTH_UPDATE_MAX_SIZE) {
            throw new OpenAlertException("批量修改最多支持一次修改1000条数据");
        }
        return comments;
    }

    /**
     * 批量屏蔽
     *
     * @param comments
     * @return
     */
    private boolean batchShield(List<Comment> comments) {
        //遍历屏蔽
        comments.forEach(x -> x.setStatus(CommentStatusEnum.SHIELD.getCommentStatusCode()));
        //更新状态
        return updateBatchById(comments);
    }

    /**
     * 批量审核
     *
     * @param comments
     * @return
     */
    private boolean batchAudited(List<Comment> comments) {
        //遍历设置状态,并且判断状态是否异常
        for (Comment comment : comments) {
            if (CommentStatusEnum.NOT_AUDITED.getCommentStatusCode().equals(comment.getStatus())) {
                comment.setStatus(CommentStatusEnum.NO_RESPONSE.getCommentStatusCode());
            } else {
                throw new OpenAlertException("批量审核评论存在非未审核状态的评论");
            }
        }
        return updateBatchById(comments);
    }
}
