package com.bsd.payment.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.mapper.RefundOrderMapper;
import com.bsd.payment.server.model.entity.RefundOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/10/30
 * @description:
 */
@Service
public class Service4Refund {
    @Resource
    private RefundOrderMapper refundOrderMapper;

    public int createRefundOrder(RefundOrder refundOrder) {
        refundOrder.setCreateTime(new Date());
        return refundOrderMapper.insertSelective(refundOrder);
    }

    public RefundOrder selectRefundOrder(String refundOrderId) {
        return refundOrderMapper.selectByPrimaryKey(refundOrderId);
    }

    public RefundOrder selectByMchIdAndRefundOrderId(String mchId, String refundOrderId) {
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RefundOrder::getMchId, mchId)
                .eq(RefundOrder::getRefundOrderId, refundOrderId);
        List<RefundOrder> refundOrderList = refundOrderMapper.selectList(wrapper);
        return CollectionUtils.isEmpty(refundOrderList) ? null : refundOrderList.get(0);
    }

    public RefundOrder selectByMchIdAndMchRefundNo(String mchId, String mchRefundNo) {
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RefundOrder::getMchId, mchId)
                .eq(RefundOrder::getMchRefundNo, mchRefundNo);
        List<RefundOrder> refundOrderList = refundOrderMapper.selectList(wrapper);
        return CollectionUtils.isEmpty(refundOrderList) ? null : refundOrderList.get(0);
    }

    public int updateStatus4Ing(String refundOrderId, String channelOrderNo) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setStatus(PayConstant.REFUND_STATUS_REFUNDING);
        if (channelOrderNo != null) {
            refundOrder.setChannelOrderNo(channelOrderNo);
        }
        refundOrder.setRefundSuccTime(new Date());
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RefundOrder::getRefundOrderId, refundOrderId)
                .eq(RefundOrder::getStatus, PayConstant.REFUND_STATUS_INIT);
        return refundOrderMapper.update(refundOrder, wrapper);
    }

    public int updateStatus4Success(String refundOrderId) {
        return updateStatus4Success(refundOrderId, null);
    }

    public int updateStatus4Success(String refundOrderId, String channelOrderNo) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundOrderId(refundOrderId);
        refundOrder.setStatus(PayConstant.REFUND_STATUS_SUCCESS);
        refundOrder.setResult(PayConstant.REFUND_RESULT_SUCCESS);
        refundOrder.setRefundSuccTime(new Date());
        if (StringUtils.isNotBlank(channelOrderNo)) {
            refundOrder.setChannelOrderNo(channelOrderNo);
        }
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RefundOrder::getRefundOrderId, refundOrderId)
                .eq(RefundOrder::getStatus, PayConstant.REFUND_STATUS_REFUNDING);
        return refundOrderMapper.update(refundOrder, wrapper);
    }

    public int updateStatus4Complete(String refundOrderId) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundOrderId(refundOrderId);
        refundOrder.setStatus(PayConstant.REFUND_STATUS_COMPLETE);

        List values = CollectionUtils.arrayToList(new Byte[]{
                PayConstant.REFUND_STATUS_SUCCESS, PayConstant.REFUND_STATUS_FAIL
        });
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RefundOrder::getRefundOrderId, refundOrderId)
                .in(RefundOrder::getStatus, values);
        return refundOrderMapper.update(refundOrder, wrapper);
    }

    public int updateStatus4Fail(String refundOrderId, String channelErrCode, String channelErrMsg) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setStatus(PayConstant.REFUND_STATUS_FAIL);
        refundOrder.setResult(PayConstant.REFUND_RESULT_FAIL);
        if (channelErrCode != null) {
            refundOrder.setChannelErrCode(channelErrCode);
        }
        if (channelErrMsg != null) {
            refundOrder.setChannelErrMsg(channelErrMsg);
        }
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RefundOrder::getRefundOrderId, refundOrderId)
                .eq(RefundOrder::getStatus, PayConstant.REFUND_STATUS_REFUNDING);
        return refundOrderMapper.update(refundOrder, wrapper);
    }
}
