package com.bsd.payment.server.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.payment.server.model.entity.PayOrder;
import com.opencloud.common.model.PageParams;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
public interface IPayOrderService {
    Map createPayOrder(String jsonParam);

    Map selectPayOrder(String jsonParam);

    Map selectPayOrderByMchIdAndPayOrderId(String jsonParam);

    Map selectPayOrderByMchIdAndMchOrderNo(String jsonParam);

    Map updateStatus4Ing(String jsonParam);

    Map updateStatus4Success(String jsonParam);

    Map updateStatus4Complete(String jsonParam);

    Map updateNotify(String jsonParam);

    int createPayOrder(JSONObject payOrder);

    JSONObject queryPayOrder(String mchId, String payOrderId, String mchOrderNo, String executeNotify);

    String doWxPayReq(String tradeType, JSONObject payOrder, String resKey);

    String doAliPayReq(String channelCode, JSONObject payOrder, String resKey);

    //分页查询
    IPage<PayOrder> findListPage(PageParams pageParams);

    //根据transOrderId查询详情
    PayOrder findPayOrder(String payOrderId);

    /**
     * 将支付未完成的支付订单置为过期
     *
     * @param seconds 过期时间，秒
     */
    int updateStatus4Expired(int seconds);

    /**
     * 同步支付结果
     *
     * @param seconds 距离当前时间间隔，秒
     */
    int synPayResult(int seconds);

    Boolean queryAliPayResult(PayOrder payOrder);

    Boolean queryWxPayResult(PayOrder payOrder);
}
