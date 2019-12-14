package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.PayOrder;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayOrderMapper extends SuperMapper<PayOrder> {
    int deleteByPrimaryKey(String payOrderId);

    int insertSelective(PayOrder record);

    PayOrder selectByPrimaryKey(String payOrderId);

    int updateByPrimaryKeySelective(PayOrder record);

    int updateByPrimaryKey(PayOrder record);
}