package com.bsd.payment.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.payment.server.model.TransOrderPo;
import com.bsd.payment.server.model.dto.QueryTransResultDTO;
import com.bsd.payment.server.model.dto.TransResultDTO;
import com.bsd.payment.server.model.entity.TransOrder;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.Date;

/**
 * @author: wangyankai
 * @date: 2019/8/21
 * @description:
 */
public interface ITransOrderService extends IBaseService<TransOrder> {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<TransOrder> findListPage(PageParams pageParams);

    /**
     * 根据orderId查询详情
     *
     * @param orderId
     * @return
     */
    TransOrder findTransOrder(String orderId);

    /**
     * 保存转账订单
     *
     * @param transOrder
     * @param sign
     * @return
     */
    boolean saveTransOrder(TransOrder transOrder, String sign, String ip);

    /**
     * 处理转账订单结果
     *
     * @param resultBody
     * @param transOrder
     */
    void handleTransResult(ResultBody<TransResultDTO> resultBody, TransOrderPo transOrder);

    /**
     * 更新转账订单为成功
     *
     * @param transOrderId
     * @param channelOrderNo
     * @param sucTime
     * @return
     */
    int updateStatus4Success(String transOrderId, String channelOrderNo, Date sucTime);

    /**
     * 更新转账订单为失败
     *
     * @param transOrderId
     * @param channelErrCode
     * @param channelErrMsg
     * @return
     */
    int updateStatus4Fail(String transOrderId, String channelErrCode, String channelErrMsg);

    /**
     * 更新转账订单为完成
     *
     * @param transOrderId
     * @return
     */
    int updateStatus4Complete(String transOrderId);

    /**
     * 更新订单状态为转账中
     *
     * @param transOrderId
     * @return
     */
    int updateStatus4Traning(String transOrderId);

    /**
     * 根据转账订单号获取转账信息
     *
     * @param transOrderId
     * @return
     */
    TransOrder getByTransOrderId(String transOrderId);

    /**
     * 根据商户转账订单号获取转账信息
     */
    TransOrder getByMchTransNo(String mchTransNo);

    /**
     * 查询转账订单结果
     *
     * @param transOrderId
     * @return
     */
    QueryTransResultDTO queryTransOrderResult(String transOrderId);
}
