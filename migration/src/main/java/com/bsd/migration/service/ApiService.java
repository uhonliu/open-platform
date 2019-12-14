package com.bsd.migration.service;

import com.bsd.migration.model.entity.GatewayRoute;
import com.bsd.migration.model.resp.Config;

import java.util.Map;

/**
 * @Author: yuanyujun
 * @Date: 2019/10/29
 */
public interface ApiService {
    void sync(Config sourceConfig, Config targetConfig, Map<String, GatewayRoute> sourceMenusMap, Map<String, GatewayRoute> targetMenusMap);
}
