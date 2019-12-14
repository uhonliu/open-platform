package com.bsd.payment.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.mapper.MchInfoMapper;
import com.bsd.payment.server.mapper.MchNotifyMapper;
import com.bsd.payment.server.mapper.PayChannelMapper;
import com.bsd.payment.server.mapper.PayOrderMapper;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.model.entity.MchNotify;
import com.bsd.payment.server.model.entity.PayChannel;
import com.bsd.payment.server.model.entity.PayOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/9/9
 * @description:
 */
@Service
public class BaseService {
    @Resource
    private PayOrderMapper payOrderMapper;

    @Resource
    private MchInfoMapper mchInfoMapper;

    @Resource
    private PayChannelMapper payChannelMapper;

    @Resource
    private MchNotifyMapper mchNotifyMapper;


    public MchInfo baseSelectMchInfo(String mchId) {
        return mchInfoMapper.selectByPrimaryKey(mchId);
    }

    public MchNotify baseSelectMchNotify(String orderId) {
        return mchNotifyMapper.selectByPrimaryKey(orderId);
    }

    public PayChannel baseSelectPayChannel(String mchId, String channelCode) {
        List<PayChannel> payChannelList = payChannelMapper.selectByMchIdAndChannelCode(mchId, channelCode);
        if (CollectionUtils.isEmpty(payChannelList)) {
            return null;
        }
        return payChannelList.get(0);
    }

    public int baseCreatePayOrder(PayOrder payOrder) {
        payOrder.setCreateTime(new Date());
        return payOrderMapper.insertSelective(payOrder);
    }

    public PayOrder baseSelectPayOrder(String payOrderId) {
        return payOrderMapper.selectByPrimaryKey(payOrderId);
    }

    public PayOrder baseSelectPayOrderByMchIdAndPayOrderId(String mchId, String payOrderId) {
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getMchId, mchId)
                .eq(PayOrder::getPayOrderId, payOrderId);
        List<PayOrder> payOrderList = payOrderMapper.selectList(wrapper);
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    public PayOrder baseSelectPayOrderByMchIdAndMchOrderNo(String mchId, String mchOrderNo) {
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getMchId, mchId)
                .eq(PayOrder::getMchOrderNo, mchOrderNo)
                .orderByDesc(PayOrder::getExpireTime, PayOrder::getCreateTime);
        List<PayOrder> payOrderList = payOrderMapper.selectList(wrapper);
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    public int baseUpdateStatus4Ing(String payOrderId, String channelOrderNo) {
        PayOrder payOrder = new PayOrder();
        payOrder.setStatus(PayConstant.PAY_STATUS_PAYING);
        if (channelOrderNo != null) {
            payOrder.setChannelOrderNo(channelOrderNo);
        }
        payOrder.setUpdateTime(new Date());
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getPayOrderId, payOrderId)
                .eq(PayOrder::getStatus, PayConstant.PAY_STATUS_INIT);
        return payOrderMapper.update(payOrder, wrapper);
    }

    public int baseUpdateStatus4Success(String payOrderId, String channelOrderNo) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
        if (channelOrderNo != null) {
            payOrder.setChannelOrderNo(channelOrderNo);
        }
        payOrder.setPaySuccTime(new Date());
        payOrder.setUpdateTime(new Date());
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getPayOrderId, payOrderId)
                .eq(PayOrder::getStatus, PayConstant.PAY_STATUS_PAYING);
        return payOrderMapper.update(payOrder, wrapper);
    }

    public int baseUpdateStatus4Failed(String payOrderId) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_FAILED);
        payOrder.setUpdateTime(new Date());
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getPayOrderId, payOrderId)
                .eq(PayOrder::getStatus, PayConstant.PAY_STATUS_PAYING);
        return payOrderMapper.update(payOrder, wrapper);
    }

    public int baseUpdateStatus4Complete(String payOrderId) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_COMPLETE);
        payOrder.setUpdateTime(new Date());
        QueryWrapper<PayOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(PayOrder::getPayOrderId, payOrderId)
                .eq(PayOrder::getStatus, PayConstant.PAY_STATUS_SUCCESS);
        return payOrderMapper.update(payOrder, wrapper);
    }

    public int baseUpdateNotify(String payOrderId, byte count) {
        PayOrder newPayOrder = new PayOrder();
        newPayOrder.setNotifyCount(count);
        newPayOrder.setLastNotifyTime(new Date());
        newPayOrder.setPayOrderId(payOrderId);
        newPayOrder.setUpdateTime(new Date());
        return payOrderMapper.updateByPrimaryKeySelective(newPayOrder);
    }

    public int baseInsertMchNotify(String orderId, String mchId, String mchOrderNo, String orderType, String notifyUrl) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setOrderId(orderId);
        mchNotify.setMchId(mchId);
        mchNotify.setMchOrderNo(mchOrderNo);
        mchNotify.setOrderType(orderType);
        mchNotify.setNotifyUrl(notifyUrl);
        mchNotify.setCreateTime(new Date());
        return mchNotifyMapper.insertSelectiveOnDuplicateKeyUpdate(mchNotify);
    }

    public int baseUpdateMchNotifySuccess(String orderId, String result, byte notifyCount) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setStatus(PayConstant.MCH_NOTIFY_STATUS_SUCCESS);
        mchNotify.setResult(result);
        mchNotify.setNotifyCount(notifyCount);
        mchNotify.setLastNotifyTime(new Date());
        List values = new LinkedList<>();
        values.add(PayConstant.MCH_NOTIFY_STATUS_NOTIFYING);
        values.add(PayConstant.MCH_NOTIFY_STATUS_FAIL);
        QueryWrapper<MchNotify> wrapper = new QueryWrapper();
        wrapper.lambda().eq(MchNotify::getOrderId, orderId)
                .in(MchNotify::getStatus, values);
        return mchNotifyMapper.update(mchNotify, wrapper);
    }

    public int baseUpdateMchNotifyFail(String orderId, String result, byte notifyCount) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setStatus(PayConstant.MCH_NOTIFY_STATUS_FAIL);
        mchNotify.setResult(result);
        mchNotify.setNotifyCount(notifyCount);
        mchNotify.setLastNotifyTime(new Date());
        List values = new LinkedList<>();
        values.add(PayConstant.MCH_NOTIFY_STATUS_FAIL);
        values.add(PayConstant.MCH_NOTIFY_STATUS_NOTIFYING);
        QueryWrapper<MchNotify> wrapper = new QueryWrapper();
        wrapper.lambda().eq(MchNotify::getOrderId, orderId)
                .in(MchNotify::getStatus, values);
        return mchNotifyMapper.update(mchNotify, wrapper);
    }
}
