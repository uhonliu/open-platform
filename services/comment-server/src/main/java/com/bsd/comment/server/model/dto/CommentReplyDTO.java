package com.bsd.comment.server.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 评论回复DTO
 *
 * @Author: linrongxin
 * @Date: 2019/9/10 17:08
 */
@Data
public class CommentReplyDTO {
    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    @ApiModelProperty(required = true, name = "commentId", value = "评论ID", example = "4")
    private Long commentId;

    /**
     * 上级回复ID
     */
    @ApiModelProperty(required = false, name = "parentId", value = "上级回复ID", example = "10")
    private Long parentId;

    /**
     * 回复者ID
     */
    @NotNull(message = "回复者ID不能为空")
    @ApiModelProperty(required = true, name = "fromUserId", value = "回复者ID", example = "521677655146233856")
    private Long fromUserId;

    /**
     * 回复者的名字
     */
    @NotEmpty(message = "回复者的名字不能为空")
    @Size(max = 20, message = "回复者昵称最大不能超过20个字符")
    @ApiModelProperty(required = true, name = "fromUserName", value = "回复者的名字", example = "admin")
    private String fromUserName;

    /**
     * 回复内容
     */
    @NotEmpty(message = "回复内容不能为空")
    @Size(max = 5000, message = "回复内容最大不能超过500个字符")
    @ApiModelProperty(required = true, name = "content", value = "回复内容", example = "感谢你的评论，我们会更加努力。")
    private String content;

    /**
     * 回复目标ID
     */
    @NotNull(message = "回复目标ID不能为空")
    @ApiModelProperty(required = true, name = "toUserId", value = "回复目标ID", example = "1164480423881207800")
    private Long toUserId;

    /**
     * 是否后台回复 1.普通回复  2.平台回复
     */
    @NotNull(message = "是否后台回复类型不能为空")
    @ApiModelProperty(required = true, name = "isAuthor", value = "是否后台回复 1.普通回复  2.平台回复", example = "1")
    private Integer isAuthor;
}
