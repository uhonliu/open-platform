package com.opencloud.msg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.msg.client.model.entity.EmailConfig;
import com.opencloud.msg.server.service.EmailConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 邮件发送配置 前端控制器
 *
 * @author admin
 * @date 2019-07-25
 */
@Api(value = "邮件发送配置", tags = "邮件发送配置")
@RestController
@RequestMapping("/emailConfig")
public class EmailConfigController {
    @Autowired
    private EmailConfigService targetService;

    /**
     * 获取分页数据
     *
     * @return
     */
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    @GetMapping(value = "/list")
    public ResultBody<IPage<EmailConfig>> list(@RequestParam(required = false) Map map) {
        PageParams pageParams = new PageParams(map);
        EmailConfig query = pageParams.mapToObject(EmailConfig.class);
        QueryWrapper<EmailConfig> queryWrapper = new QueryWrapper();
        return ResultBody.ok().data(targetService.page(new PageParams(map), queryWrapper));
    }

    /**
     * 根据ID查找数据
     */
    @ApiOperation(value = "根据ID查找数据", notes = "根据ID查找数据")
    @ResponseBody
    @GetMapping("/get")
    public ResultBody<EmailConfig> get(@RequestParam("id") Long id) {
        EmailConfig entity = targetService.getById(id);
        return ResultBody.ok().data(entity);
    }

    /**
     * 添加数据
     *
     * @return
     */
    @ApiOperation(value = "添加数据", notes = "添加数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "配置名称", paramType = "form"),
            @ApiImplicitParam(name = "smtpHost", required = true, value = "发件服务器域名", paramType = "form"),
            @ApiImplicitParam(name = "smtpUsername", required = true, value = "发件服务器账户", paramType = "form"),
            @ApiImplicitParam(name = "smtpPassword", required = true, value = "发件服务器密码", paramType = "form")
    })
    @PostMapping("/add")
    public ResultBody add(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "smtpHost") String smtpHost,
            @RequestParam(value = "smtpUsername") String smtpUsername,
            @RequestParam(value = "smtpPassword") String smtpPassword
    ) {
        EmailConfig entity = new EmailConfig();
        entity.setName(name);
        entity.setSmtpHost(smtpHost);
        entity.setSmtpUsername(smtpUsername);
        entity.setSmtpPassword(smtpPassword);
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
            @ApiImplicitParam(name = "configId", required = true, value = "", paramType = "form"),
            @ApiImplicitParam(name = "name", required = true, value = "配置名称", paramType = "form"),
            @ApiImplicitParam(name = "smtpHost", required = true, value = "发件服务器域名", paramType = "form"),
            @ApiImplicitParam(name = "smtpUsername", required = true, value = "发件服务器账户", paramType = "form"),
            @ApiImplicitParam(name = "smtpPassword", required = true, value = "发件服务器密码", paramType = "form")
    })
    @PostMapping("/update")
    public ResultBody add(
            @RequestParam(value = "configId") Long configId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "smtpHost") String smtpHost,
            @RequestParam(value = "smtpUsername") String smtpUsername,
            @RequestParam(value = "smtpPassword") String smtpPassword
    ) {
        EmailConfig entity = new EmailConfig();
        entity.setConfigId(configId);
        entity.setName(name);
        entity.setSmtpHost(smtpHost);
        entity.setSmtpUsername(smtpUsername);
        entity.setSmtpPassword(smtpPassword);
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
