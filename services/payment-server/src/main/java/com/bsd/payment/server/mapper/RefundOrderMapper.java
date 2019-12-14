package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.RefundOrder;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefundOrderMapper extends SuperMapper<RefundOrder> {
    int deleteByPrimaryKey(String refundOrderId);

    int insertSelective(RefundOrder record);

    RefundOrder selectByPrimaryKey(String refundOrderId);

    int updateByPrimaryKeySelective(RefundOrder record);

    int updateByPrimaryKey(RefundOrder record);
}