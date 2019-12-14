package com.bsd.migration.service;

import com.bsd.migration.model.resp.Config;

/**
 * @Author: yuanyujun
 * @Date: 2019/10/29
 */
public interface RouteService {
    void sync(Config sourceConfig, Config targetConfig);
}
