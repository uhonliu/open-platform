package com.bsd.payment.server.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.payment.server.model.entity.RefundOrder;
import com.opencloud.common.model.PageParams;

import java.util.Map;

/**
 * @author: wangyankai
 * @date: 2019/8/21
 * @description: 退款业务
 */
public interface IRefundOrderService {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<RefundOrder> findListPage(PageParams pageParams);

    /**
     * 根据transOrderId查询详情
     *
     * @param refundOrderId
     * @return
     */
    RefundOrder findRefundOrder(String refundOrderId);

    /**
     * 创建退款订单
     *
     * @param refundOrder
     * @return
     */
    int create(JSONObject refundOrder);

    /**
     * 创建退款订单
     *
     * @param jsonParam
     * @return
     */
    Map create(String jsonParam);

    /**
     * 查询退款订单
     *
     * @param jsonParam
     * @return
     */
    Map select(String jsonParam);

    /**
     * 通过mchid，mchRefundOrderId查询订单信息
     *
     * @param jsonParam
     * @return
     */
    Map selectByMchIdAndRefundOrderId(String jsonParam);

    /**
     * 通过mchid，mchRefundNo查询订单信息
     *
     * @param jsonParam
     * @return
     */
    Map selectByMchIdAndMchRefundNo(String jsonParam);

    /**
     * 修改订单状态为退款中
     *
     * @param jsonParam
     * @return
     */
    Map updateStatus4Ing(String jsonParam);

    /**
     * 修改订单状态为成功
     *
     * @param jsonParam
     * @return
     */
    Map updateStatus4Success(String jsonParam);

    /**
     * 修改订单状态为已完成
     *
     * @param jsonParam
     * @return
     */
    Map updateStatus4Complete(String jsonParam);

    /**
     * 发送异步退款消息
     *
     * @param jsonParam
     */
    Map sendRefundNotify(String jsonParam);

    /**
     * 构建退款参数
     *
     * @param refundOrderId
     * @param channelName
     */
    void sendRefundNotify(String refundOrderId, String channelName);

    /**
     * 创建退款订单
     *
     * @param params
     * @return
     */
    String createRefundOrder(JSONObject params);

    /**
     * 验证创建订单请求参数,参数通过返回JSONObject对象,否则返回错误文本信息
     *
     * @param map
     * @return
     */
    String validateQueryParams(Map map, JSONObject payContext);
}
