package com.opencloud.base.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.AuthorityAction;
import com.opencloud.base.client.model.entity.BaseAction;
import com.opencloud.base.server.service.BaseActionService;
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
 * @author liuyadu
 */
@Api(tags = "系统功能按钮管理")
@RestController
public class BaseActionController {
    @Autowired
    private BaseActionService baseActionService;
    @Autowired
    private OpenRestTemplate openRestTemplate;

    /**
     * 获取分页功能按钮列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页功能按钮列表", notes = "获取分页功能按钮列表")
    @GetMapping("/action")
    public ResultBody<IPage<AuthorityAction>> findActionListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(baseActionService.findListPage(new PageParams(map)));
    }


    /**
     * 获取功能按钮详情
     *
     * @param actionId
     * @return
     */
    @ApiOperation(value = "获取功能按钮详情", notes = "获取功能按钮详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮Id", paramType = "path"),
    })
    @GetMapping("/action/{actionId}/info")
    public ResultBody<AuthorityAction> getAction(@PathVariable("actionId") Long actionId) {
        return ResultBody.ok().data(baseActionService.getAction(actionId));
    }

    /**
     * 添加功能按钮
     *
     * @param actionCode 功能按钮编码
     * @param actionName 功能按钮名称
     * @param menuId     上级菜单
     * @param status     是否启用
     * @param priority   优先级越小越靠前
     * @param actionDesc 描述
     * @return
     */
    @ApiOperation(value = "添加功能按钮", notes = "添加功能按钮")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actionCode", required = true, value = "功能按钮编码", paramType = "form"),
            @ApiImplicitParam(name = "actionName", required = true, value = "功能按钮名称", paramType = "form"),
            @ApiImplicitParam(name = "menuId", required = true, value = "上级菜单", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
            @ApiImplicitParam(name = "actionDesc", required = false, value = "描述", paramType = "form"),
    })
    @PostMapping("/action/add")
    public ResultBody<Long> addAction(
            @RequestParam(value = "actionCode") String actionCode,
            @RequestParam(value = "actionName") String actionName,
            @RequestParam(value = "menuId") Long menuId,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "actionDesc", required = false, defaultValue = "") String actionDesc
    ) {
        BaseAction action = new BaseAction();
        action.setActionCode(actionCode);
        action.setActionName(actionName);
        action.setMenuId(menuId);
        action.setStatus(status);
        action.setPriority(priority);
        action.setActionDesc(actionDesc);
        Long actionId = null;
        BaseAction result = baseActionService.addAction(action);
        if (result != null) {
            actionId = result.getActionId();
            openRestTemplate.refreshGateway();
        }
        return ResultBody.ok().data(actionId);
    }

    /**
     * 编辑功能按钮
     *
     * @param actionId   功能按钮ID
     * @param actionCode 功能按钮编码
     * @param actionName 功能按钮名称
     * @param menuId     上级菜单
     * @param status     是否启用
     * @param priority   优先级越小越靠前
     * @param actionDesc 描述
     * @return
     */
    @ApiOperation(value = "编辑功能按钮", notes = "添加功能按钮")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮ID", paramType = "form"),
            @ApiImplicitParam(name = "actionCode", required = true, value = "功能按钮编码", paramType = "form"),
            @ApiImplicitParam(name = "actionName", required = true, value = "功能按钮名称", paramType = "form"),
            @ApiImplicitParam(name = "menuId", required = true, value = "上级菜单", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
            @ApiImplicitParam(name = "actionDesc", required = false, value = "描述", paramType = "form"),
    })
    @PostMapping("/action/update")
    public ResultBody updateAction(
            @RequestParam("actionId") Long actionId,
            @RequestParam(value = "actionCode") String actionCode,
            @RequestParam(value = "actionName") String actionName,
            @RequestParam(value = "menuId") Long menuId,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "actionDesc", required = false, defaultValue = "") String actionDesc
    ) {
        BaseAction action = new BaseAction();
        action.setActionId(actionId);
        action.setActionCode(actionCode);
        action.setActionName(actionName);
        action.setMenuId(menuId);
        action.setStatus(status);
        action.setPriority(priority);
        action.setActionDesc(actionDesc);
        baseActionService.updateAction(action);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 移除功能按钮
     *
     * @param actionId
     * @return
     */
    @ApiOperation(value = "移除功能按钮", notes = "移除功能按钮")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮ID", paramType = "form")
    })
    @PostMapping("/action/remove")
    public ResultBody removeAction(
            @RequestParam("actionId") Long actionId
    ) {
        baseActionService.removeAction(actionId);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
