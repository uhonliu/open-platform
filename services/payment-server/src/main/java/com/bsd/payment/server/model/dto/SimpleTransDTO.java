package com.bsd.payment.server.model.dto;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/8/30 15:09
 */
@Data
public class SimpleTransDTO {
    /**
     * 转账订单号
     */
    private String transOrderId;

    /**
     * 支付通道名称
     */
    private String channelName;
}
