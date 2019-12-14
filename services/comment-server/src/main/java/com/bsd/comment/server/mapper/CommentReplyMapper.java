package com.bsd.comment.server.mapper;

import com.bsd.comment.server.model.entity.CommentReply;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论回复表 Mapper 接口
 *
 * @author lrx
 * @date 2019-09-09
 */
@Mapper
public interface CommentReplyMapper extends SuperMapper<CommentReply> {

}
