package com.opencloud.msg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.msg.client.model.entity.EmailTemplate;
import com.opencloud.msg.server.service.EmailTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 邮件模板配置 前端控制器
 *
 * @author admin
 * @date 2019-07-25
 */
@Api(value = "邮件模板配置", tags = "邮件模板配置")
@RestController
@RequestMapping("/emailTemplate")
public class EmailTemplateController {
    @Autowired
    private EmailTemplateService targetService;

    /**
     * 获取分页数据
     *
     * @return
     */
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    @GetMapping(value = "/list")
    public ResultBody<IPage<EmailTemplate>> list(@RequestParam(required = false) Map map) {
        PageParams pageParams = new PageParams(map);
        EmailTemplate query = pageParams.mapToObject(EmailTemplate.class);
        QueryWrapper<EmailTemplate> queryWrapper = new QueryWrapper();
        return ResultBody.ok().data(targetService.page(new PageParams(map), queryWrapper));
    }

    /**
     * 根据ID查找数据
     */
    @ApiOperation(value = "根据ID查找数据", notes = "根据ID查找数据")
    @ResponseBody
    @GetMapping("/get")
    public ResultBody<EmailTemplate> get(@RequestParam("id") Long id) {
        EmailTemplate entity = targetService.getById(id);
        return ResultBody.ok().data(entity);
    }

    /**
     * 添加数据
     *
     * @return
     */
    @ApiOperation(value = "添加数据", notes = "添加数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "模板名称", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "模板编码", paramType = "form"),
            @ApiImplicitParam(name = "configId", required = true, value = "发送服务器配置", paramType = "form"),
            @ApiImplicitParam(name = "template", required = true, value = "模板", paramType = "form"),
            @ApiImplicitParam(name = "params", required = true, value = "模板参数", paramType = "form")
    })
    @PostMapping("/add")
    public ResultBody add(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "code") String code,
            @RequestParam(value = "configId") Long configId,
            @RequestParam(value = "template") String template,
            @RequestParam(value = "params") String params
    ) {
        EmailTemplate entity = new EmailTemplate();
        entity.setName(name);
        entity.setCode(code);
        entity.setConfigId(configId);
        entity.setTemplate(template);
        entity.setParams(params);
        targetService.save(entity);
        return ResultBody.ok();
    }

    /**
     * 更新数据
     *
     * @return
     */
    @ApiOperation(value = "更新数据", notes = "更新数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplId", required = true, value = "", paramType = "form"),
            @ApiImplicitParam(name = "name", required = true, value = "模板名称", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "模板编码", paramType = "form"),
            @ApiImplicitParam(name = "configId", required = true, value = "发送服务器配置", paramType = "form"),
            @ApiImplicitParam(name = "template", required = true, value = "模板", paramType = "form"),
            @ApiImplicitParam(name = "params", required = true, value = "模板参数", paramType = "form")
    })
    @PostMapping("/update")
    public ResultBody add(
            @RequestParam(value = "tplId") Long tplId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "code") String code,
            @RequestParam(value = "configId") Long configId,
            @RequestParam(value = "template") String template,
            @RequestParam(value = "params") String params
    ) {
        EmailTemplate entity = new EmailTemplate();
        entity.setTplId(tplId);
        entity.setName(name);
        entity.setCode(code);
        entity.setConfigId(configId);
        entity.setTemplate(template);
        entity.setParams(params);
        targetService.updateById(entity);
        return ResultBody.ok();
    }

    /**
     * 删除数据
     *
     * @return
     */
    @ApiOperation(value = "删除数据", notes = "删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "id", paramType = "form")
    })
    @PostMapping("/remove")
    public ResultBody remove(
            @RequestParam(value = "id") Long id
    ) {
        targetService.removeById(id);
        return ResultBody.ok();
    }

    /**
     * 批量删除数据
     *
     * @return
     */
    @ApiOperation(value = "批量删除数据", notes = "批量删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "id", paramType = "form")
    })
    @PostMapping("/batch/remove")
    public ResultBody batchRemove(
            @RequestParam(value = "ids") String ids
    ) {
        targetService.removeByIds(Arrays.asList(ids.split(",")));
        return ResultBody.ok();
    }
}