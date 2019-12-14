package com.opencloud.base.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.model.entity.GatewayRoute;
import com.opencloud.base.server.service.GatewayRouteService;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.http.OpenRestTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 网关智能路由
 *
 * @author: liuyadu
 * @date: 2019/3/12 15:12
 * @description:
 */
@Api(tags = "网关智能路由")
@RestController
public class GatewayRouteController {
    @Autowired
    private GatewayRouteService gatewayRouteService;
    @Autowired
    private OpenRestTemplate openRestTemplate;

    /**
     * 获取分页路由列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页路由列表", notes = "获取分页路由列表")
    @GetMapping("/gateway/route")
    public ResultBody<IPage<GatewayRoute>> getRouteListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(gatewayRouteService.findListPage(new PageParams(map)));
    }


    /**
     * 获取路由
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "获取路由", notes = "获取路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeId", required = true, value = "路由ID", paramType = "path"),
    })
    @GetMapping("/gateway/route/{routeId}/info")
    public ResultBody<GatewayRoute> getRoute(@PathVariable("routeId") Long routeId) {
        return ResultBody.ok().data(gatewayRouteService.getRoute(routeId));
    }

    /**
     * 添加路由
     *
     * @param path        路径表达式
     * @param routeName   描述
     * @param serviceId   服务名方转发
     * @param url         地址转发
     * @param stripPrefix 忽略前缀
     * @param retryable   支持重试
     * @param status      是否启用
     * @return
     */
    @ApiOperation(value = "添加路由", notes = "添加路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", required = true, value = "路径表达式", paramType = "form"),
            @ApiImplicitParam(name = "routeName", required = true, value = "路由标识", paramType = "form"),
            @ApiImplicitParam(name = "routeType", required = false, value = "路由方式", paramType = "form"),
            @ApiImplicitParam(name = "routeDesc", required = true, value = "路由名称", paramType = "form"),
            @ApiImplicitParam(name = "serviceId", required = false, value = "服务名方转发", paramType = "form"),
            @ApiImplicitParam(name = "url", required = false, value = "地址转发", paramType = "form"),
            @ApiImplicitParam(name = "stripPrefix", required = false, allowableValues = "0,1", defaultValue = "1", value = "忽略前缀", paramType = "form"),
            @ApiImplicitParam(name = "retryable", required = false, allowableValues = "0,1", defaultValue = "0", value = "支持重试", paramType = "form"),
            @ApiImplicitParam(name = "status", required = false, allowableValues = "0,1", defaultValue = "1", value = "是否启用", paramType = "form")
    })
    @PostMapping("/gateway/route/add")
    public ResultBody<Long> addRoute(
            @RequestParam(value = "routeName", required = true, defaultValue = "") String routeName,
            @RequestParam(value = "routeType", required = false, defaultValue = BaseConstants.ROUTE_TYPE_SERVICE) String routeType,
            @RequestParam(value = "routeDesc", required = true, defaultValue = "") String routeDesc,
            @RequestParam(value = "path") String path,
            @RequestParam(value = "serviceId", required = false) String serviceId,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "stripPrefix", required = false, defaultValue = "1") Integer stripPrefix,
            @RequestParam(value = "retryable", required = false, defaultValue = "0") Integer retryable,
            @RequestParam(value = "status", defaultValue = "1") Integer status
    ) {
        GatewayRoute route = new GatewayRoute();
        route.setPath(path);
        route.setRetryable(retryable);
        route.setStripPrefix(stripPrefix);
        route.setStatus(status);
        route.setRouteName(routeName);
        route.setRouteType(routeType);
        route.setRouteDesc(routeDesc);
        switch (routeType) {
            case BaseConstants.ROUTE_TYPE_URL:
                route.setServiceId(null);
                route.setUrl(url.trim());
                break;
            default:
                route.setServiceId(serviceId.trim());
                route.setUrl(null);
        }
        gatewayRouteService.addRoute(route);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 编辑路由
     *
     * @param routeId     路由ID
     * @param path        路径表达式
     * @param serviceId   服务名方转发
     * @param url         地址转发
     * @param stripPrefix 忽略前缀
     * @param retryable   支持重试
     * @param status      是否启用
     * @param routeName   描述
     * @return
     */
    @ApiOperation(value = "编辑路由", notes = "编辑路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeId", required = true, value = "路由Id", paramType = "form"),
            @ApiImplicitParam(name = "routeName", required = true, value = "路由标识", paramType = "form"),
            @ApiImplicitParam(name = "routeType", required = false, value = "路由方式", paramType = "form"),
            @ApiImplicitParam(name = "routeDesc", required = true, value = "路由名称", paramType = "form"),
            @ApiImplicitParam(name = "path", required = true, value = "路径表达式", paramType = "form"),
            @ApiImplicitParam(name = "serviceId", required = false, value = "服务名方转发", paramType = "form"),
            @ApiImplicitParam(name = "url", required = false, value = "地址转发", paramType = "form"),
            @ApiImplicitParam(name = "stripPrefix", required = false, allowableValues = "0,1", defaultValue = "1", value = "忽略前缀", paramType = "form"),
            @ApiImplicitParam(name = "retryable", required = false, allowableValues = "0,1", defaultValue = "0", value = "支持重试", paramType = "form"),
            @ApiImplicitParam(name = "status", required = false, allowableValues = "0,1", defaultValue = "1", value = "是否启用", paramType = "form")
    })
    @PostMapping("/gateway/route/update")
    public ResultBody updateRoute(
            @RequestParam("routeId") Long routeId,
            @RequestParam(value = "routeName", defaultValue = "") String routeName,
            @RequestParam(value = "routeType", required = false, defaultValue = BaseConstants.ROUTE_TYPE_SERVICE) String routeType,
            @RequestParam(value = "routeDesc", defaultValue = "") String routeDesc,
            @RequestParam(value = "path") String path,
            @RequestParam(value = "serviceId", required = false) String serviceId,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "stripPrefix", required = false, defaultValue = "1") Integer stripPrefix,
            @RequestParam(value = "retryable", required = false, defaultValue = "0") Integer retryable,
            @RequestParam(value = "status", defaultValue = "1") Integer status
    ) {
        GatewayRoute route = new GatewayRoute();
        route.setRouteId(routeId);
        route.setPath(path);
        route.setRetryable(retryable);
        route.setStripPrefix(stripPrefix);
        route.setStatus(status);
        route.setRouteName(routeName);
        route.setRouteType(routeType);
        route.setRouteDesc(routeDesc);
        switch (routeType) {
            case "url":
                route.setServiceId(null);
                route.setUrl(url.trim());
                break;
            default:
                route.setServiceId(serviceId.trim());
                route.setUrl(null);
        }
        gatewayRouteService.updateRoute(route);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 移除路由
     *
     * @param routeId
     * @return
     */
    @ApiOperation(value = "移除路由", notes = "移除路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeId", required = true, value = "routeId", paramType = "form"),
    })
    @PostMapping("/gateway/route/remove")
    public ResultBody removeRoute(
            @RequestParam("routeId") Long routeId
    ) {
        gatewayRouteService.removeRoute(routeId);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
