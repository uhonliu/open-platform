package com.bsd.comment.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.comment.server.enums.CommentStatusEnum;
import com.bsd.comment.server.model.dto.CommentDTO;
import com.bsd.comment.server.model.entity.Comment;
import com.bsd.comment.server.model.query.CommentQuery;
import com.bsd.comment.server.service.CommentService;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 评论表 前端控制器
 *
 * @author lrx
 * @date 2019-09-09
 */
@Api(value = "评论服务接口", tags = "评论服务接口")
@RestController
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 分页获取评论(后台)
     *
     * @return
     */
    @ApiOperation(value = "分页获取评论(后台)", notes = "获取分页数据")
    @PostMapping(value = "/admin/list")
    public ResultBody adminComments(CommentQuery commentQuery) {
        IPage<Comment> page = commentService.commentPage(commentQuery, true);
        return ResultBody.ok().data(page);
    }

    /**
     * 分页获取评论(客户端)
     *
     * @return
     */
    @ApiOperation(value = "分页获取评论(客户端)", notes = "获取分页数据")
    @PostMapping(value = "/client/list")
    public ResultBody clientComments(CommentQuery commentQuery) {
        IPage<Comment> page = commentService.commentPage(commentQuery, false);
        return ResultBody.ok().data(page);
    }


    /**
     * 添加评论
     *
     * @return
     */
    @ApiOperation(value = "添加评论", notes = "添加评论数据")
    @PostMapping("/add")
    public ResultBody add(@Validated CommentDTO commentDTO) {
        //拷贝属性
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        //保存评论
        boolean isSuc = commentService.saveComment(comment);
        if (!isSuc) {
            return ResultBody.failed().msg("添加评论失败");
        }
        return ResultBody.ok();
    }

    /**
     * 批量审核评论
     *
     * @return
     */
    @ApiOperation(value = "批量审核评论", notes = "批量审核评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentIds", required = true, value = "多个用,号隔开", paramType = "form")
    })
    @PostMapping("/batch/audited")
    public ResultBody batchAudited(@RequestParam(value = "commentIds") String commentIds) {
        boolean isSuc = commentService.changeStatus(CommentStatusEnum.NO_RESPONSE, Arrays.asList(commentIds.split(",")));
        if (!isSuc) {
            return ResultBody.failed().msg("批量审核评论失败");
        }
        return ResultBody.ok();
    }

    /**
     * 批量回复评论
     *
     * @param commentIds
     * @param replyContent
     * @return
     */
    @ApiOperation(value = "批量回复评论", notes = "批量回复评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentIds", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "replyContent", required = true, value = "回复内容", paramType = "form")
    })
    @PostMapping("/batch/reply")
    public ResultBody batchReply(@RequestParam(value = "commentIds") String commentIds,
                                 @RequestParam(value = "replyContent") String replyContent) {
        boolean isSuc = commentService.batchReply(Arrays.asList(commentIds.split(",")), replyContent);
        if (!isSuc) {
            return ResultBody.failed().msg("批量回复评论失败");
        }
        return ResultBody.ok();
    }


    /**
     * 批量屏蔽评论
     *
     * @param commentIds
     * @return
     */
    @ApiOperation(value = "批量屏蔽评论", notes = "批量屏蔽评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentIds", required = true, value = "多个用,号隔开", paramType = "form")
    })
    @PostMapping("/batch/shield")
    public ResultBody batchShield(@RequestParam(value = "commentIds") String commentIds) {
        boolean isSuc = commentService.changeStatus(CommentStatusEnum.SHIELD, Arrays.asList(commentIds.split(",")));
        if (!isSuc) {
            return ResultBody.failed().msg("批量屏蔽评论失败");
        }
        return ResultBody.ok();
    }


    /**
     * 置顶评论
     *
     * @param commentId
     * @return
     */
    @ApiOperation(value = "置顶评论", notes = "置顶评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", required = true, value = "评论ID", paramType = "form")
    })
    @PostMapping("/setTop")
    public ResultBody setTop(@RequestParam(value = "commentId") String commentId) {
        boolean isSuc = commentService.setCommentToTop(commentId);
        if (!isSuc) {
            return ResultBody.failed().msg("置顶评论失败");
        }
        return ResultBody.ok();
    }


    /**
     * 评论行为操作
     *
     * @param commentId
     * @param action
     * @return
     */
    @ApiOperation(value = "评论行为操作", notes = "评论行为操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", required = true, value = "评论ID", paramType = "form"),
            @ApiImplicitParam(name = "action", required = true, value = "1.点赞 2.点踩", paramType = "form")
    })
    @PostMapping("/action")
    public ResultBody action(@RequestParam(value = "commentId") Long commentId,
                             @RequestParam(value = "action") Integer action) {
        boolean isSuc = commentService.doCommentAction(commentId, action);
        if (!isSuc) {
            return ResultBody.failed().msg("评论操作失败");
        }
        return ResultBody.ok();
    }
}
