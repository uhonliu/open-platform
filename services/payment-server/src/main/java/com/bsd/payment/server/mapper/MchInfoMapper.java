package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.MchInfo;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MchInfoMapper extends SuperMapper<MchInfo> {
    int deleteByPrimaryKey(String mchId);

    int insertSelective(MchInfo record);

    MchInfo selectByPrimaryKey(String mchId);

    int updateByPrimaryKeySelective(MchInfo record);

    int updateByPrimaryKey(MchInfo record);

    List<MchInfo> selectMchId();
}