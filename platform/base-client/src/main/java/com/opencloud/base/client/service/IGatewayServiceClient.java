package com.opencloud.base.client.service;

import com.opencloud.base.client.model.IpLimitApi;
import com.opencloud.base.client.model.RateLimitApi;
import com.opencloud.base.client.model.entity.GatewayRoute;
import com.opencloud.common.model.ResultBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author liuyadu
 */
public interface IGatewayServiceClient {
    /**
     * 获取接口黑名单列表
     *
     * @return
     */
    @GetMapping("/gateway/api/blackList")
    ResultBody<List<IpLimitApi>> getApiBlackList();

    /**
     * 获取接口白名单列表
     *
     * @return
     */
    @GetMapping("/gateway/api/whiteList")
    ResultBody<List<IpLimitApi>> getApiWhiteList();

    /**
     * 获取限流列表
     *
     * @return
     */
    @GetMapping("/gateway/api/rateLimit")
    ResultBody<List<RateLimitApi>> getApiRateLimitList();

    /**
     * 获取路由列表
     *
     * @return
     */
    @GetMapping("/gateway/api/route")
    ResultBody<List<GatewayRoute>> getApiRouteList();
}
