package com.bsd.migration.service;

import com.bsd.migration.model.resp.Config;

/**
 * @Author: linrongxin
 * @Date: 2019/9/30 10:15
 */
public interface MenuService {
    void sync(Config sourceConfig, Config targetConfig);
}
