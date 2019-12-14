package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.IapReceipt;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IapReceiptMapper extends SuperMapper<IapReceipt> {
    int deleteByPrimaryKey(String payOrderId);

    int insertSelective(IapReceipt record);

    IapReceipt selectByPrimaryKey(String payOrderId);

    int updateByPrimaryKeySelective(IapReceipt record);

    int updateByPrimaryKey(IapReceipt record);
}