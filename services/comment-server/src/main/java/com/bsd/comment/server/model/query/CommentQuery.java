package com.bsd.comment.server.model.query;

import com.bsd.comment.server.validation.constraints.TopicType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评论数据查询对象
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 14:30
 */
@Data
public class CommentQuery {
    /**
     * 评论类型
     */
    @TopicType
    @ApiModelProperty(required = false, name = "commentType", value = "评论类型", example = "goods")
    private String commentType;

    /**
     * 查询内容
     */
    @ApiModelProperty(required = false, name = "searchContent", value = "查询内容", example = "商品")
    private String searchContent;

    /**
     * 页数
     */
    @ApiModelProperty(required = false, name = "pageIndex", value = "页数", example = "1")
    private Integer pageIndex;

    /**
     * 每页大小
     */
    @ApiModelProperty(required = false, name = "pageSize", value = "每页大小", example = "20")
    private Integer pageSize;
}
