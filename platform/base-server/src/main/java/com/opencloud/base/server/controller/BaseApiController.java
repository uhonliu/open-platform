package com.opencloud.base.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.entity.BaseApi;
import com.opencloud.base.server.service.BaseApiService;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.http.OpenRestTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Api(tags = "系统接口资源管理")
@RestController
public class BaseApiController {
    @Autowired
    private BaseApiService apiService;
    @Autowired
    private OpenRestTemplate openRestTemplate;

    /**
     * 获取分页接口列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页接口列表", notes = "获取分页接口列表")
    @GetMapping(value = "/api")
    public ResultBody<IPage<BaseApi>> getApiList(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(apiService.findListPage(new PageParams(map)));
    }


    /**
     * 获取所有接口列表
     *
     * @return
     */
    @ApiOperation(value = "获取所有接口列表", notes = "获取所有接口列表")
    @GetMapping("/api/all")
    public ResultBody<List<BaseApi>> getApiAllList(String serviceId) {
        return ResultBody.ok().data(apiService.findAllList(serviceId));
    }

    /**
     * 获取接口资源
     *
     * @param apiId
     * @return
     */
    @ApiOperation(value = "获取接口资源", notes = "获取接口资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apiId", required = true, value = "ApiId", paramType = "path"),
    })
    @GetMapping("/api/{apiId}/info")
    public ResultBody<BaseApi> getApi(@PathVariable("apiId") Long apiId) {
        return ResultBody.ok().data(apiService.getApi(apiId));
    }

    /**
     * 添加接口资源
     *
     * @param apiCode   接口编码
     * @param apiName   接口名称
     * @param serviceId 服务ID
     * @param path      请求路径
     * @param status    是否启用
     * @param priority  优先级越小越靠前
     * @param apiDesc   描述
     * @return
     */
    @ApiOperation(value = "添加接口资源", notes = "添加接口资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apiCode", required = true, value = "接口编码", paramType = "form"),
            @ApiImplicitParam(name = "apiName", required = true, value = "接口名称", paramType = "form"),
            @ApiImplicitParam(name = "apiCategory", required = true, value = "接口分类", paramType = "form"),
            @ApiImplicitParam(name = "serviceId", required = true, value = "服务ID", paramType = "form"),
            @ApiImplicitParam(name = "path", required = false, value = "请求路径", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
            @ApiImplicitParam(name = "apiDesc", required = false, value = "描述", paramType = "form"),
            @ApiImplicitParam(name = "isAuth", required = false, defaultValue = "0", allowableValues = "0,1", value = "是否身份认证", paramType = "form"),
            @ApiImplicitParam(name = "isOpen", required = false, defaultValue = "0", allowableValues = "0,1", value = "是否公开: 0-内部的 1-公开的", paramType = "form")
    })
    @PostMapping("/api/add")
    public ResultBody<Long> addApi(
            @RequestParam(value = "apiCode") String apiCode,
            @RequestParam(value = "apiName") String apiName,
            @RequestParam(value = "apiCategory") String apiCategory,
            @RequestParam(value = "serviceId") String serviceId,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "apiDesc", required = false, defaultValue = "") String apiDesc,
            @RequestParam(value = "isAuth", required = false, defaultValue = "1") Integer isAuth,
            @RequestParam(value = "isOpen", required = false, defaultValue = "0") Integer isOpen
    ) {
        BaseApi api = new BaseApi();
        api.setApiCode(apiCode);
        api.setApiName(apiName);
        api.setApiCategory(apiCategory);
        api.setServiceId(serviceId);
        api.setPath(path);
        api.setStatus(status);
        api.setPriority(priority);
        api.setApiDesc(apiDesc);
        api.setIsAuth(isAuth);
        api.setIsOpen(isOpen);
        Long apiId = null;
        apiService.addApi(api);
        openRestTemplate.refreshGateway();
        return ResultBody.ok().data(apiId);
    }

    /**
     * 编辑接口资源
     *
     * @param apiId     接口ID
     * @param apiCode   接口编码
     * @param apiName   接口名称
     * @param serviceId 服务ID
     * @param path      请求路径
     * @param status    是否启用
     * @param priority  优先级越小越靠前
     * @param apiDesc   描述
     * @return
     */
    @ApiOperation(value = "编辑接口资源", notes = "编辑接口资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apiId", required = true, value = "接口Id", paramType = "form"),
            @ApiImplicitParam(name = "apiCode", required = true, value = "接口编码", paramType = "form"),
            @ApiImplicitParam(name = "apiName", required = true, value = "接口名称", paramType = "form"),
            @ApiImplicitParam(name = "apiCategory", required = true, value = "接口分类", paramType = "form"),
            @ApiImplicitParam(name = "serviceId", required = true, value = "服务ID", paramType = "form"),
            @ApiImplicitParam(name = "path", required = false, value = "请求路径", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
            @ApiImplicitParam(name = "apiDesc", required = false, value = "描述", paramType = "form"),
            @ApiImplicitParam(name = "isAuth", required = false, defaultValue = "0", allowableValues = "0,1", value = "是否身份认证", paramType = "form"),
            @ApiImplicitParam(name = "isOpen", required = false, defaultValue = "0", allowableValues = "0,1", value = "是否公开: 0-内部的 1-公开的", paramType = "form")
    })
    @PostMapping("/api/update")
    public ResultBody updateApi(
            @RequestParam("apiId") Long apiId,
            @RequestParam(value = "apiCode") String apiCode,
            @RequestParam(value = "apiName") String apiName,
            @RequestParam(value = "apiCategory") String apiCategory,
            @RequestParam(value = "serviceId") String serviceId,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "apiDesc", required = false, defaultValue = "") String apiDesc,
            @RequestParam(value = "isAuth", required = false, defaultValue = "1") Integer isAuth,
            @RequestParam(value = "isOpen", required = false, defaultValue = "0") Integer isOpen
    ) {
        BaseApi api = new BaseApi();
        api.setApiId(apiId);
        api.setApiCode(apiCode);
        api.setApiName(apiName);
        api.setApiCategory(apiCategory);
        api.setServiceId(serviceId);
        api.setPath(path);
        api.setStatus(status);
        api.setPriority(priority);
        api.setApiDesc(apiDesc);
        api.setIsAuth(isAuth);
        api.setIsOpen(isOpen);
        apiService.updateApi(api);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 移除接口资源
     *
     * @param apiId
     * @return
     */
    @ApiOperation(value = "移除接口资源", notes = "移除接口资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apiId", required = true, value = "ApiId", paramType = "form"),
    })
    @PostMapping("/api/remove")
    public ResultBody removeApi(
            @RequestParam("apiId") Long apiId
    ) {
        apiService.removeApi(apiId);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 批量删除数据
     *
     * @return
     */
    @ApiOperation(value = "批量删除数据", notes = "批量删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form")
    })
    @PostMapping("/api/batch/remove")
    public ResultBody batchRemove(
            @RequestParam(value = "ids") String ids
    ) {
        QueryWrapper<BaseApi> wrapper = new QueryWrapper();
        wrapper.lambda().in(BaseApi::getApiId, Arrays.asList(ids.split(","))).eq(BaseApi::getIsPersist, 0);
        apiService.remove(wrapper);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 批量修改公开状态
     *
     * @return
     */
    @ApiOperation(value = "批量修改公开状态", notes = "批量修改公开状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "open", required = true, value = "是否公开访问:0-否 1-是", paramType = "form")
    })
    @PostMapping("/api/batch/update/open")
    public ResultBody batchUpdateOpen(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "open") Integer open
    ) {
        Assert.isTrue((open != 1 && open != 0), "isOpen只支持0,1");
        QueryWrapper<BaseApi> wrapper = new QueryWrapper();
        wrapper.lambda().in(BaseApi::getApiId, Arrays.asList(ids.split(",")));
        BaseApi entity = new BaseApi();
        entity.setIsOpen(open);
        apiService.update(entity, wrapper);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 批量修改状态
     *
     * @return
     */
    @ApiOperation(value = "批量修改状态", notes = "批量修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "接口状态:0-禁用 1-启用", paramType = "form")
    })
    @PostMapping("/api/batch/update/status")
    public ResultBody batchUpdateStatus(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "status") Integer status
    ) {
        Assert.isTrue((status != 0 && status != 1 && status != 2), "status只支持0,1,2");
        QueryWrapper<BaseApi> wrapper = new QueryWrapper();
        wrapper.lambda().in(BaseApi::getApiId, Arrays.asList(ids.split(",")));
        BaseApi entity = new BaseApi();
        entity.setStatus(status);
        apiService.update(entity, wrapper);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 批量修改身份认证
     *
     * @return
     */
    @ApiOperation(value = "批量修改身份认证", notes = "批量修改身份认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "auth", required = true, value = "是否身份认证:0-否 1-是", paramType = "form")
    })
    @PostMapping("/api/batch/update/auth")
    public ResultBody batchUpdateAuth(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "auth") Integer auth
    ) {
        Assert.isTrue((auth != 0 && auth != 1), "auth只支持0,1");
        QueryWrapper<BaseApi> wrapper = new QueryWrapper();
        wrapper.lambda().in(BaseApi::getApiId, Arrays.asList(ids.split(","))).eq(BaseApi::getIsPersist, 0);
        BaseApi entity = new BaseApi();
        entity.setStatus(auth);
        apiService.update(entity, wrapper);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 批量修改保留属性
     *
     * @return
     */
    @ApiOperation(value = "批量修改保留属性", notes = "批量修改保留属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "persist", required = true, value = "保留数据:0-否 1-是 不允许删除", paramType = "form")
    })
    @PostMapping("/api/batch/update/persist")
    public ResultBody batchUpdatePersist(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "persist") Integer persist
    ) {
        Assert.isTrue((persist != 0 && persist != 1), "persist只支持0,1");
        QueryWrapper<BaseApi> wrapper = new QueryWrapper();
        wrapper.lambda().in(BaseApi::getApiId, Arrays.asList(ids.split(",")));
        BaseApi entity = new BaseApi();
        entity.setIsPersist(persist);
        apiService.update(entity, wrapper);
        // 刷新网关
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
