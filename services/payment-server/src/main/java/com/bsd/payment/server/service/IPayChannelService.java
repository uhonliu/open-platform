package com.bsd.payment.server.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.payment.server.model.entity.PayChannel;
import com.opencloud.common.model.PageParams;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
public interface IPayChannelService {
    Map selectPayChannel(String jsonParam);

    JSONObject getByMchIdAndChannelCode(String mchId, String channelCode);

    int addPayChannel(PayChannel payChannel);

    int updatePayChannel(PayChannel payChannel);

    PayChannel selectByPayChannelId(int id);

    /**
     * 分页查询
     *
     * @param pageParams
     */
    IPage<PayChannel> findListPage(PageParams pageParams);

    /**
     * Id查询详情
     *
     * @param channelId
     * @return
     */
    PayChannel findPayChannel(String channelId);

    String getChannelParamConfig(String mchId, String channelCode);
}
