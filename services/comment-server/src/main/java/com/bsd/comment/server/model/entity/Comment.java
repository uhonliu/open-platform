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
 * 评论表
 *
 * @author lrx
 * @date 2019-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@TableName("bsd_comment")
@ApiModel(value = "评论对象", description = "评论表")
public class Comment extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评论ID")
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    @ApiModelProperty(value = "主题ID(商品,课程,活动ID)")
    private Long topicId;

    @ApiModelProperty(value = "主题名称(商品,课程,活动名称)")
    private String topicName;

    @ApiModelProperty(value = "主题类型")
    private String topicType;

    @ApiModelProperty(value = "主题子类型")
    private String topicSubType;

    @ApiModelProperty(value = "评论用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "来源 1.客户端APP 2.PC 3.WAP 4.unknow")
    private Integer source;

    @ApiModelProperty(value = "状态 1.未审核 2.未回复 3.已回复 4.已屏蔽")
    private Integer status;

    @ApiModelProperty(value = "是否置顶 0.否 1.是")
    private Boolean isTop;

    @ApiModelProperty(value = "点踩数")
    private Integer unLikeNum;

    @ApiModelProperty(value = "点赞数")
    private Integer likeNum;

    @ApiModelProperty(value = "回复数")
    private Integer replyNum;

    @ApiModelProperty(value = "创建者")
    private Long createBy;

    @ApiModelProperty(value = "更新者")
    private Long updateBy;
}
