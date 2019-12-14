package com.opencloud.msg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.msg.client.model.entity.EmailLogs;
import com.opencloud.msg.server.service.EmailLogsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮件发送日志 前端控制器
 *
 * @author admin
 * @date 2019-07-25
 */
@Api(value = "邮件发送日志", tags = "邮件发送日志")
@RestController
@RequestMapping("/emailLogs")
public class EmailLogsController {
    @Autowired
    private EmailLogsService targetService;

    /**
     * 获取分页数据
     *
     * @return
     */
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    @GetMapping(value = "/list")
    public ResultBody<IPage<EmailLogs>> list(@RequestParam(required = false) Map map) {
        PageParams pageParams = new PageParams(map);
        EmailLogs query = pageParams.mapToObject(EmailLogs.class);
        QueryWrapper<EmailLogs> queryWrapper = new QueryWrapper();
        return ResultBody.ok().data(targetService.page(new PageParams(map), queryWrapper));
    }

    /**
     * 根据ID查找数据
     */
    @ApiOperation(value = "根据ID查找数据", notes = "根据ID查找数据")
    @ResponseBody
    @GetMapping("/get")
    public ResultBody<EmailLogs> get(@RequestParam("id") Long id) {
        EmailLogs entity = targetService.getById(id);
        return ResultBody.ok().data(entity);
    }
}