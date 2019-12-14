package com.bsd.payment.server.service;

import com.bsd.payment.server.model.dto.TransResultDTO;
import com.bsd.payment.server.model.entity.TransOrder;
import com.opencloud.common.model.ResultBody;

/**
 * 支付通道服务接口
 *
 * @Author: linrongxin
 * @Date: 2019/8/30 16:06
 */
public interface IPayService {
    /**
     * 支付
     * @param payOrder
     * @return
     */
    // ResultBody doPayReq(PayOrder payOrder);


    /**
     * 转账
     *
     * @param transOrder
     */
    ResultBody<TransResultDTO> doTransReq(TransOrder transOrder, String configStr);


    /**
     * 转账结果查询
     *
     * @param transOrder
     * @return
     */
    ResultBody<TransResultDTO> getTransReq(TransOrder transOrder, String configStr);

    /**
     * 退款
     * @param refundOrderId
     * @return
     */
    // ResultBody doRefundReq(String refundOrderId);

    /**
     * 退款结果查询
     * @param refundOrderId
     * @return
     */
    // ResultBody getRefundReq(String refundOrderId);
}
