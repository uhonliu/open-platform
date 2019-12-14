package com.bsd.migration.controller;

import com.bsd.migration.model.resp.Config;
import com.bsd.migration.model.resp.ResultBody;
import com.bsd.migration.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: yuanyujun
 * @Date: 2019/10/29
 */
@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @RequestMapping("/sync")
    public Object sync(Config sourceConfig, Config targetConfig) {
        if (sourceConfig == null) {
            return ResultBody.failed().msg("源配置错误");
        }
        if (targetConfig == null) {
            return ResultBody.failed().msg("目标配置错误");
        }
        routeService.sync(sourceConfig, targetConfig);
        return ResultBody.ok().msg("路由资源同步中");
    }
}
