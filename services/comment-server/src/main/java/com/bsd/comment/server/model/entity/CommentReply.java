package com.bsd.comment.server.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 评论回复表
 *
 * @author lrx
 * @date 2019-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("bsd_comment_reply")
@ApiModel(value = "CommentReply对象", description = "评论回复表")
public class CommentReply extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评论回复ID")
    @TableId(value = "reply_id", type = IdType.ASSIGN_ID)
    private Long replyId;

    @ApiModelProperty(value = "评论ID")
    private Long commentId;

    @ApiModelProperty(value = "上级回复ID")
    private Long parentId;

    @ApiModelProperty(value = "回复者ID")
    private Long fromUserId;

    @ApiModelProperty(value = "回复者的名字")
    private String fromUserName;

    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "回复目标ID")
    private Long toUserId;

    @ApiModelProperty(value = "是否屏蔽 0.不屏蔽 1.屏蔽")
    private Boolean isShield;

    @ApiModelProperty(value = "是否后台回复 1.普通回复  2.平台回复")
    private Integer isAuthor;

    @ApiModelProperty(value = "创建者")
    private Long createBy;

    @ApiModelProperty(value = "更新者")
    private Long updateBy;
}
