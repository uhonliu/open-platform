package com.bsd.migration.service;

import com.bsd.migration.model.entity.BaseMenu;
import com.bsd.migration.model.resp.Config;

import java.util.Map;

/**
 * @Author: linrongxin
 * @Date: 2019/10/8 15:29
 */
public interface ActionService {
    void sync(Config sourceConfig, Config targetConfig, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap);
}
