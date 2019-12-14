package com.bsd.payment.server.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.payment.server.model.entity.MchInfo;
import com.opencloud.common.model.PageParams;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
public interface IMchInfoService {
    Map selectMchInfo(String jsonParam);

    JSONObject getByMchId(String mchId);

    int addMchInfo(MchInfo mchInfo);

    int updateMchInfo(MchInfo mchInfo);

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<MchInfo> findListPage(PageParams pageParams);

    /**
     * 根据mchId查询详情
     *
     * @param mchId
     * @return
     */
    MchInfo findMchInfo(String mchId);
}
