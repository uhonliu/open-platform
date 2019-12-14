package com.bsd.comment.server.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: linrongxin
 * @Date: 2019/9/11 15:52
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentReplyRespDTO extends CommentReplyDTO {
    /**
     * 回复ID
     */
    private Long replyId;

    /**
     * 创建时间
     */
    public Date createTime;
}
