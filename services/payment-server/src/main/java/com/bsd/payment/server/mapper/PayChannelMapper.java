package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.PayChannel;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PayChannelMapper extends SuperMapper<PayChannel> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PayChannel record);

    PayChannel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayChannel record);

    int updateByPrimaryKey(PayChannel record);

    List<PayChannel> selectByMchIdAndChannelCode(@Param("mchId") String mchId, @Param("channelCode") String channelCode);
}