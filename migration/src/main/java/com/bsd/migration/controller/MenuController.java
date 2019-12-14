package com.bsd.migration.controller;

import com.bsd.migration.model.resp.Config;
import com.bsd.migration.model.resp.ResultBody;
import com.bsd.migration.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: linrongxin
 * @Date: 2019/9/30 10:12
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @RequestMapping("/sync")
    public Object sync(Config sourceConfig, Config targetConfig) {
        if (sourceConfig == null) {
            return ResultBody.failed().msg("源配置错误");
        }
        if (targetConfig == null) {
            return ResultBody.failed().msg("目标配置错误");
        }
        menuService.sync(sourceConfig, targetConfig);
        return ResultBody.ok().msg("菜单资源同步中");
    }
}
