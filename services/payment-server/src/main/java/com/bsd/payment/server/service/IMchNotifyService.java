package com.bsd.payment.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.payment.server.model.entity.MchNotify;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.IBaseService;

/**
 * @author: wangyankai
 * @date: 2019/8/22
 * @description:
 */
public interface IMchNotifyService extends IBaseService<MchNotify> {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<MchNotify> findListPage(PageParams pageParams);

    /**
     * 根据orderId查询详情
     *
     * @param orderId
     * @return
     */
    MchNotify findMchNotify(String orderId);

    /**
     * 修改商户通知信息状态
     *
     * @param orderId
     * @param result
     * @param notifyCount
     * @return
     */
    int updateMchNotifyStatus(String orderId, byte status, String result, byte notifyCount);
}
