package com.bsd.comment.server.utils;

import com.bsd.comment.server.model.dto.CommentReplyTreeDTO;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 回复树结构处理
 *
 * @Author: linrongxin
 * @Date: 2019/9/11 14:27
 */
public class CommentReplyTreeUtils {
    public static List<CommentReplyTreeDTO> getNodeJson(List<CommentReplyTreeDTO> commentReplies) {
        //list转换成map,便于查询
        Map<Long, CommentReplyTreeDTO> nodes = makeCommentReplyMap(commentReplies);
        //获取tree
        return getNodeJson(0L, nodes);
    }

    private static Map<Long, CommentReplyTreeDTO> makeCommentReplyMap(List<CommentReplyTreeDTO> commentReplies) {
        //LinkedHashMap,这里不再排序,由SQL语句排序
        Map<Long, CommentReplyTreeDTO> nodes = Maps.newLinkedHashMap();
        commentReplies.forEach(x -> {
            nodes.put(x.getReplyId(), x);
        });
        return nodes;
    }

    public static List<CommentReplyTreeDTO> getNodeJson(Long nodeId, Map<Long, CommentReplyTreeDTO> nodes) {
        //当前层级当前点下的所有子节点
        List<CommentReplyTreeDTO> childList = getChildNodes(nodeId, nodes);
        for (CommentReplyTreeDTO node : childList) {
            //递归调用该方法
            List<CommentReplyTreeDTO> childs = getNodeJson(node.getReplyId(), nodes);
            if (!childs.isEmpty()) {
                //设置下级回复
                node.setChildren(childs);
            }
        }
        return childList;
    }


    /**
     * 获取当前节点的所有子节点
     *
     * @param nodeId
     * @param nodes
     * @return
     */
    public static List<CommentReplyTreeDTO> getChildNodes(Long nodeId, Map<Long, CommentReplyTreeDTO> nodes) {
        List<CommentReplyTreeDTO> list = new ArrayList<>();
        for (Long key : nodes.keySet()) {
            if (nodes.get(key).getParentId().equals(nodeId)) {
                list.add(nodes.get(key));
            }
        }
        return list;
    }
}
