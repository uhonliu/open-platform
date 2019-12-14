package com.opencloud.base.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.entity.BaseAction;
import com.opencloud.base.client.model.entity.BaseApp;
import com.opencloud.base.client.model.entity.BaseMenu;
import com.opencloud.base.server.service.BaseActionService;
import com.opencloud.base.server.service.BaseAppService;
import com.opencloud.base.server.service.BaseMenuService;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.http.OpenRestTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Api(tags = "系统菜单资源管理")
@RestController
public class BaseMenuController {
    @Autowired
    private BaseMenuService baseResourceMenuService;

    @Autowired
    private BaseActionService baseResourceOperationService;

    @Autowired
    private BaseAppService baseAppService;

    @Autowired
    private OpenRestTemplate openRestTemplate;

    /**
     * 所有服务列表
     *
     * @return
     */
    @ApiOperation(value = "所有服务列表", notes = "所有服务列表")
    @GetMapping("/menu/services")
    public ResultBody<List<JSONObject>> getServiceList() {
        List<BaseApp> apps = baseAppService.list();
        List<JSONObject> jsonList = new ArrayList<>();
        for (BaseApp app : apps) {
            JSONObject json = new JSONObject();
            json.put("serviceId", app.getAppNameEn());
            String serviceNameDisplay = app.getAppName() + "(" + app.getAppNameEn() + ")";
            if (app.getStatus() != 1) {
                serviceNameDisplay += "[未启用]";
            }
            json.put("serviceName", serviceNameDisplay);
            jsonList.add(json);
        }
        return ResultBody.ok().data(jsonList);
    }

    /**
     * 获取分页菜单资源列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页菜单资源列表", notes = "获取分页菜单资源列表")
    @GetMapping("/menu")
    public ResultBody<IPage<BaseMenu>> getMenuListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(baseResourceMenuService.findListPage(new PageParams(map)));
    }

    /**
     * 菜单所有资源列表
     *
     * @return
     */
    @ApiOperation(value = "菜单所有资源列表", notes = "菜单所有资源列表")
    @GetMapping("/menu/all")
    public ResultBody<List<BaseMenu>> getMenuAllList() {
        return ResultBody.ok().data(baseResourceMenuService.findAllList());
    }


    /**
     * 获取菜单下所有操作
     *
     * @param menuId
     * @return
     */
    @ApiOperation(value = "获取菜单下所有操作", notes = "获取菜单下所有操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", value = "menuId", paramType = "form"),
    })
    @GetMapping("/menu/action")
    public ResultBody<List<BaseAction>> getMenuAction(Long menuId) {
        return ResultBody.ok().data(baseResourceOperationService.findListByMenuId(menuId));
    }

    /**
     * 获取菜单资源详情
     *
     * @param menuId
     * @return 应用信息
     */
    @ApiOperation(value = "获取菜单资源详情", notes = "获取菜单资源详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", required = true, value = "menuId"),
    })
    @GetMapping("/menu/{menuId}/info")
    public ResultBody<BaseMenu> getMenu(@PathVariable("menuId") Long menuId) {
        return ResultBody.ok().data(baseResourceMenuService.getMenu(menuId));
    }

    /**
     * 添加菜单资源
     *
     * @param menuCode 菜单编码
     * @param menuName 菜单名称
     * @param icon     图标
     * @param scheme   请求前缀
     * @param path     请求路径
     * @param target   打开方式
     * @param status   是否启用
     * @param parentId 父节点ID
     * @param priority 优先级越小越靠前
     * @param menuDesc 描述
     * @return
     */
    @ApiOperation(value = "添加菜单资源", notes = "添加菜单资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuCode", required = true, value = "菜单编码", paramType = "form"),
            @ApiImplicitParam(name = "menuName", required = true, value = "菜单名称", paramType = "form"),
            @ApiImplicitParam(name = "icon", required = false, value = "图标", paramType = "form"),
            @ApiImplicitParam(name = "scheme", required = false, value = "请求协议", allowableValues = "/,http://,https://", paramType = "form"),
            @ApiImplicitParam(name = "path", required = false, value = "请求路径", paramType = "form"),
            @ApiImplicitParam(name = "target", required = false, value = "请求路径", allowableValues = "_self,_blank", paramType = "form"),
            @ApiImplicitParam(name = "parentId", required = false, defaultValue = "0", value = "父节点ID", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
            @ApiImplicitParam(name = "menuDesc", required = false, value = "描述", paramType = "form"),
            @ApiImplicitParam(name = "serviceId", required = false, value = "前端应用", paramType = "form")
    })
    @PostMapping("/menu/add")
    public ResultBody<Long> addMenu(
            @RequestParam(value = "menuCode") String menuCode,
            @RequestParam(value = "menuName") String menuName,
            @RequestParam(value = "icon", required = false) String icon,
            @RequestParam(value = "scheme", required = false, defaultValue = "/") String scheme,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            @RequestParam(value = "target", required = false, defaultValue = "_self") String target,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "menuDesc", required = false, defaultValue = "") String menuDesc,
            @RequestParam(value = "serviceId", required = false, defaultValue = "") String serviceId
    ) {
        BaseMenu menu = new BaseMenu();
        menu.setMenuCode(menuCode);
        menu.setMenuName(menuName);
        menu.setIcon(icon);
        menu.setPath(path);
        menu.setScheme(scheme);
        menu.setTarget(target);
        menu.setStatus(status);
        menu.setParentId(parentId);
        menu.setPriority(priority);
        menu.setMenuDesc(menuDesc);
        menu.setServiceId(serviceId);
        Long menuId = null;
        BaseMenu result = baseResourceMenuService.addMenu(menu);
        if (result != null) {
            menuId = result.getMenuId();
        }
        return ResultBody.ok().data(menuId);
    }

    /**
     * 编辑菜单资源
     *
     * @param menuCode 菜单编码
     * @param menuName 菜单名称
     * @param icon     图标
     * @param scheme   请求前缀
     * @param path     请求路径
     * @param target   打开方式
     * @param status   是否启用
     * @param parentId 父节点ID
     * @param priority 优先级越小越靠前
     * @param menuDesc 描述
     * @return
     */
    @ApiOperation(value = "编辑菜单资源", notes = "编辑菜单资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", required = true, value = "菜单ID", paramType = "form"),
            @ApiImplicitParam(name = "menuCode", required = true, value = "菜单编码", paramType = "form"),
            @ApiImplicitParam(name = "menuName", required = true, value = "菜单名称", paramType = "form"),
            @ApiImplicitParam(name = "icon", required = false, value = "图标", paramType = "form"),
            @ApiImplicitParam(name = "scheme", required = false, value = "请求协议", allowableValues = "/,http://,https://", paramType = "form"),
            @ApiImplicitParam(name = "path", required = false, value = "请求路径", paramType = "form"),
            @ApiImplicitParam(name = "target", required = false, value = "请求路径", allowableValues = "_self,_blank", paramType = "form"),
            @ApiImplicitParam(name = "parentId", required = false, defaultValue = "0", value = "父节点ID", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
            @ApiImplicitParam(name = "menuDesc", required = false, value = "描述", paramType = "form"),
            @ApiImplicitParam(name = "serviceId", required = false, value = "前端应用", paramType = "form")
    })
    @PostMapping("/menu/update")
    public ResultBody updateMenu(
            @RequestParam("menuId") Long menuId,
            @RequestParam(value = "menuCode") String menuCode,
            @RequestParam(value = "menuName") String menuName,
            @RequestParam(value = "icon", required = false) String icon,
            @RequestParam(value = "scheme", required = false, defaultValue = "/") String scheme,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            @RequestParam(value = "target", required = false, defaultValue = "_self") String target,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "menuDesc", required = false, defaultValue = "") String menuDesc,
            @RequestParam(value = "serviceId", required = false, defaultValue = "") String serviceId
    ) {
        BaseMenu menu = new BaseMenu();
        menu.setMenuId(menuId);
        menu.setMenuCode(menuCode);
        menu.setMenuName(menuName);
        menu.setIcon(icon);
        menu.setPath(path);
        menu.setScheme(scheme);
        menu.setTarget(target);
        menu.setStatus(status);
        menu.setParentId(parentId);
        menu.setPriority(priority);
        menu.setMenuDesc(menuDesc);
        menu.setServiceId(serviceId);
        baseResourceMenuService.updateMenu(menu);
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 移除菜单资源
     *
     * @param menuId
     * @return
     */
    @ApiOperation(value = "移除菜单资源", notes = "移除菜单资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", required = true, value = "menuId", paramType = "form"),
    })
    @PostMapping("/menu/remove")
    public ResultBody<Boolean> removeMenu(
            @RequestParam("menuId") Long menuId
    ) {
        baseResourceMenuService.removeMenu(menuId);
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
