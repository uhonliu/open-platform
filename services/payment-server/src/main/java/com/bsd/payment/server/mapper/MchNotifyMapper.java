package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.MchNotify;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MchNotifyMapper extends SuperMapper<MchNotify> {
    int deleteByPrimaryKey(String orderId);

    int insertSelective(MchNotify record);

    MchNotify selectByPrimaryKey(String orderId);

    int updateByPrimaryKeySelective(MchNotify record);

    int updateByPrimaryKey(MchNotify record);

    int insertSelectiveOnDuplicateKeyUpdate(MchNotify record);
}