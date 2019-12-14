package com.bsd.payment.server.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.payment.server.mapper.GoodsOrderMapper;
import com.bsd.payment.server.model.entity.GoodsOrder;
import com.bsd.payment.server.util.Constant;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by dingzhiwei on 17/6/2.
 */
@Component
public class GoodsOrderService {
    @Resource
    private GoodsOrderMapper goodsOrderMapper;

   /*
    `Status` tinyint(6) NOT NULL DEFAULT '0' COMMENT '订单状态,订单生成(0),支付成功(1),处理完成(2),处理失败(-1)',
    */

    public int addGoodsOrder(GoodsOrder goodsOrder) {
        return goodsOrderMapper.insertSelective(goodsOrder);
    }

    public GoodsOrder getGoodsOrder(String goodsOrderId) {
        return goodsOrderMapper.selectByPrimaryKey(goodsOrderId);
    }

    public int updateStatus4Success(String goodsOrderId) {
        return changeGoodsOrderStatus(goodsOrderId, Constant.GOODS_ORDER_STATUS_INIT, Constant.GOODS_ORDER_STATUS_SUCCESS);
    }

    public int updateStatus4Complete(String goodsOrderId) {
        return changeGoodsOrderStatus(goodsOrderId, Constant.GOODS_ORDER_STATUS_SUCCESS, Constant.GOODS_ORDER_STATUS_COMPLETE);
    }

    public int updateStatus4Fail(String goodsOrderId) {
        return changeGoodsOrderStatus(goodsOrderId, Constant.GOODS_ORDER_STATUS_SUCCESS, Constant.GOODS_ORDER_STATUS_FAIL);
    }

    /**
     * 修改商品订单状态
     *
     * @param goodsOrderId 商品订单ID
     * @param nowStatus    当前商品订单状态
     * @param afterStatus  修改后的商品订单状态
     * @return
     */
    public int changeGoodsOrderStatus(String goodsOrderId, Byte nowStatus, Byte afterStatus) {
        //修改状态设置
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setStatus(afterStatus);
        //更新查询条件
        LambdaUpdateWrapper update = Wrappers.<GoodsOrder>lambdaUpdate().eq(GoodsOrder::getGoodsOrderId, goodsOrderId).eq(GoodsOrder::getStatus, nowStatus);
        //更新状态
        int effectCount = goodsOrderMapper.update(goodsOrder, update);
        //返回修改成功条数
        return effectCount;
    }

    public int update(GoodsOrder goodsOrder) {
        return goodsOrderMapper.updateByPrimaryKeySelective(goodsOrder);
    }
}
