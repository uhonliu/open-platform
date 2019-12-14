package com.bsd.comment.server.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: linrongxin
 * @Date: 2019/9/11 16:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentReplyTreeDTO extends CommentReplyRespDTO {
    /**
     * 子回复
     */
    private List<CommentReplyTreeDTO> children;
}
