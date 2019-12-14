package com.bsd.payment.server.service;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/9
 * @description:
 */
public interface IPayChannel4WxService {
    Map doWxPayReq(String jsonParam);

    Map doWxRefundReq(String jsonParam);

    Map getWxRefundReq(String jsonParam);
}
