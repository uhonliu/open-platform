package com.bsd.org.server.model.vo;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/8/28 14:58
 */
@Data
public class SimplePositionVO {
    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 岗位代码
     */
    private String positionCode;

    /**
     * 岗位名称
     */
    private String positionName;
}
