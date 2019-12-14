package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.TransOrder;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransOrderMapper extends SuperMapper<TransOrder> {
    int deleteByPrimaryKey(String transOrderId);

    int insertSelective(TransOrder record);

    TransOrder selectByPrimaryKey(String transOrderId);

    int updateByPrimaryKeySelective(TransOrder record);

    int updateByPrimaryKey(TransOrder record);
}