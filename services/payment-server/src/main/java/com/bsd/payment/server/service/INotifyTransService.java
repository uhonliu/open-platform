package com.bsd.payment.server.service;

import com.bsd.payment.server.model.entity.TransOrder;

/**
 * 商户转账通知接口
 *
 * @Author: linrongxin
 * @Date: 2019/9/2 15:21
 */
public interface INotifyTransService {
    void doNotify(TransOrder transOrder, boolean isFirst);
}
