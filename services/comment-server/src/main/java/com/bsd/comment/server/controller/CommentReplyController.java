package com.bsd.comment.server.controller;

import com.bsd.comment.server.model.dto.CommentReplyDTO;
import com.bsd.comment.server.model.dto.CommentReplyRespDTO;
import com.bsd.comment.server.model.dto.CommentReplyTreeDTO;
import com.bsd.comment.server.model.entity.CommentReply;
import com.bsd.comment.server.service.CommentReplyService;
import com.bsd.comment.server.utils.CommentReplyTreeUtils;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.BeanConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 评论回复表 前端控制器
 *
 * @author lrx
 * @date 2019-09-09
 */
@Api(value = "评论回复服务接口", tags = "评论回复服务接口")
@RestController
@RequestMapping("reply")
public class CommentReplyController {
    @Autowired
    private CommentReplyService commentReplyService;

    /**
     * 根据commentId获取评论下的所有回复(客户端)
     *
     * @return
     */
    @ApiOperation(value = "获取评论下的回复(客户端)", notes = "根据commentId获取评论下的所有回复")
    @GetMapping(value = "/client/replies")
    public ResultBody clientTypeGetReplies(@RequestParam(required = false) Long commentId) {
        //根据评论ID获取所有评论回复信息
        List<CommentReply> commentReplies = commentReplyService.listByCommentId(commentId, false);
        //PO转DTO
        List<CommentReplyRespDTO> result = BeanConvertUtils.copyList(commentReplies, CommentReplyRespDTO.class);
        //返回结果
        return ResultBody.ok().data(result);
    }

    /**
     * 获取评论下的回复(后台)
     *
     * @param commentId
     * @return
     */
    @ApiOperation(value = "获取评论下的回复(后台)", notes = "根据commentId获取评论下的所有回复")
    @GetMapping(value = "/admin/replies")
    public ResultBody adminTypeGetReplies(@RequestParam(required = false) Long commentId) {
        //根据评论ID获取所有评论回复信息
        List<CommentReply> commentReplies = commentReplyService.listByCommentId(commentId, true);
        //PO转DTO
        List<CommentReplyRespDTO> result = BeanConvertUtils.copyList(commentReplies, CommentReplyRespDTO.class);
        //返回结果
        return ResultBody.ok().data(result);
    }


    @ApiOperation(value = "获取评论下的回复JSON树(客户端)", notes = "根获取评论下的回复JSON树")
    @GetMapping(value = "/client/json/replies")
    public ResultBody clientTypeGetRepliesJson(@RequestParam(required = false) Long commentId) {
        //根据评论ID获取所有评论回复信息
        List<CommentReply> commentReplies = commentReplyService.listByCommentId(commentId, false);
        //PO转DTO
        List<CommentReplyTreeDTO> result = BeanConvertUtils.copyList(commentReplies, CommentReplyTreeDTO.class);
        //转换成JSON Tree形式
        List<CommentReplyTreeDTO> resultTree = CommentReplyTreeUtils.getNodeJson(result);
        //返回结果
        return ResultBody.ok().data(resultTree);
    }

    @ApiOperation(value = "获取评论下的回复JSON树(后台)", notes = "根获取评论下的回复JSON树")
    @GetMapping(value = "/admin/json/replies")
    public ResultBody adminTypeGetRepliesJson(@RequestParam(required = false) Long commentId) {
        //根据评论ID获取所有评论回复信息
        List<CommentReply> commentReplies = commentReplyService.listByCommentId(commentId, true);
        //PO转DTO
        List<CommentReplyTreeDTO> result = BeanConvertUtils.copyList(commentReplies, CommentReplyTreeDTO.class);
        //转换成JSON Tree形式
        List<CommentReplyTreeDTO> resultTree = CommentReplyTreeUtils.getNodeJson(result);
        //返回结果
        return ResultBody.ok().data(resultTree);
    }


    /**
     * 添加回复
     *
     * @return
     */
    @ApiOperation(value = "添加回复", notes = "添加回复")
    @PostMapping("/add")
    public ResultBody add(@Validated CommentReplyDTO commentReplyDTO) {
        CommentReply commentReply = BeanConvertUtils.copy(commentReplyDTO, CommentReply.class);
        //添加回复
        boolean isSuc = commentReplyService.saveCommentReply(commentReply);
        if (!isSuc) {
            return ResultBody.failed().msg("添加回复数据失败");
        }
        return ResultBody.ok();
    }


    /**
     * 批量屏蔽回复
     *
     * @return
     */
    @ApiOperation(value = "批量屏蔽回复", notes = "批量屏蔽回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "replyIds", required = true, value = "多个用,号隔开", paramType = "form")
    })
    @PostMapping("/batch/shield")
    public ResultBody batchShield(@RequestParam(value = "replyIds") String replyIds) {
        boolean isSuc = commentReplyService.shieldCommentReply(Arrays.asList(replyIds.split(",")));
        if (!isSuc) {
            return ResultBody.failed().msg("批量屏蔽回复失败");
        }
        return ResultBody.ok();
    }
}
