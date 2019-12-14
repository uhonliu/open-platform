package com.bsd.payment.server.model.dto;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/9/6 10:06
 */
@Data
public class QueryTransResultDTO {
    /**
     * 转账订单号
     */
    private String transOrderId;

    /**
     * 转账状态
     */
    private byte transResult;
}
