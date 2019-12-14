package com.bsd.payment.server.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: linrongxin
 * @Date: 2019/8/30 17:22
 */
@Data
public class TransResultDTO {
    /**
     * 是否需要再次查询
     */
    private boolean query = false;

    /**
     * 转账状态
     */
    private byte transStatus;

    /**
     * 支付渠道转账订单号
     */
    private String channelOrderNo;

    /**
     * 支付渠道转账错误码
     */
    private String channelErrCode;

    /**
     * 支付渠道转账错误描述
     */
    private String channelErrMsg;

    /**
     * 支付渠道转账成功时间
     */
    private Date transSuccTime;
}
