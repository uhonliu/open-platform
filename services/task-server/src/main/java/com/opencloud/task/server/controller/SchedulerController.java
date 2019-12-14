package com.opencloud.task.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.task.client.model.TaskInfo;
import com.opencloud.task.client.model.entity.SchedulerJobLogs;
import com.opencloud.task.server.job.HttpExecuteJob;
import com.opencloud.task.server.service.SchedulerJobLogsService;
import com.opencloud.task.server.service.SchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: liuyadu
 * @date: 2019/3/29 14:12
 * @description:
 */
@Api(tags = "任务调度服务")
@RestController
public class SchedulerController {
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private SchedulerJobLogsService schedulerJobLogsService;

    /**
     * 获取任务执行日志列表
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "获取任务执行日志列表", notes = "获取任务执行日志列表")
    @GetMapping(value = "/job/logs")
    public ResultBody<IPage<SchedulerJobLogs>> getJobLogList(@RequestParam(required = false) Map map) {
        IPage<SchedulerJobLogs> result = schedulerJobLogsService.findListPage(new PageParams(map));
        return ResultBody.ok().data(result);
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    @ApiOperation(value = "获取任务列表", notes = "获取任务列表")
    @GetMapping(value = "/job")
    public ResultBody<IPage<TaskInfo>> getJobList(@RequestParam(required = false) Map map) {
        List<TaskInfo> list = schedulerService.getJobList();
        IPage page = new Page();
        page.setRecords(list);
        page.setTotal(list.size());
        return ResultBody.ok().data(page);
    }

    /**
     * 添加远程调度任务
     *
     * @param jobName        任务名称
     * @param jobDescription 任务描述
     * @param jobType        任务类型
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param repeatInterval 间隔时间
     * @param repeatCount    重试次数
     * @param cron           cron表达式
     * @param serviceId      服务名
     * @param path           请求路径
     * @param method         请求类型
     * @param contentType    响应类型
     * @param alarmMail      告警邮箱
     * @return
     */
    @ApiOperation(value = "添加远程调度任务", notes = "添加远程调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobDescription", value = "任务描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobType", value = "任务类型", required = true, allowableValues = "simple,cron", paramType = "form"),
            @ApiImplicitParam(name = "cron", value = "cron表达式", required = false, paramType = "form"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatInterval", value = "间隔时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatCount", value = "重试次数", required = false, paramType = "form"),
            @ApiImplicitParam(name = "serviceId", value = "服务名", required = true, paramType = "form"),
            @ApiImplicitParam(name = "path", value = "请求路径", required = true, paramType = "form"),
            @ApiImplicitParam(name = "method", value = "请求类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "contentType", value = "响应类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "alarmMail", value = "告警邮箱", required = false, paramType = "form"),
    })
    @PostMapping("/job/add/http")
    public ResultBody addHttpJob(@RequestParam(name = "jobName") String jobName,
                                 @RequestParam(name = "jobDescription") String jobDescription,
                                 @RequestParam(name = "jobType") String jobType,
                                 @RequestParam(name = "cron", required = false) String cron,
                                 @RequestParam(name = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                 @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                 @RequestParam(name = "repeatInterval", required = false, defaultValue = "0") Long repeatInterval,
                                 @RequestParam(name = "repeatCount", required = false, defaultValue = "0") Integer repeatCount,
                                 @RequestParam(name = "serviceId") String serviceId,
                                 @RequestParam(name = "path") String path,
                                 @RequestParam(name = "method", required = false) String method,
                                 @RequestParam(name = "contentType", required = false) String contentType,
                                 @RequestParam(name = "alarmMail", required = false) String alarmMail) {
        TaskInfo taskInfo = new TaskInfo();
        Map data = Maps.newHashMap();
        data.put("serviceId", serviceId);
        data.put("method", method);
        data.put("path", path);
        data.put("contentType", contentType);
        data.put("alarmMail", alarmMail);
        taskInfo.setData(data);
        taskInfo.setJobName(jobName);
        taskInfo.setJobDescription(jobDescription);
        taskInfo.setJobClassName(HttpExecuteJob.class.getName());
        taskInfo.setJobGroupName(Scheduler.DEFAULT_GROUP);
        taskInfo.setStartDate(startTime);
        taskInfo.setEndDate(endTime);
        taskInfo.setRepeatInterval(repeatInterval);
        taskInfo.setRepeatCount(repeatCount);
        taskInfo.setCronExpression(cron);
        if ("simple".equals(jobType)) {
            Assert.notNull(taskInfo.getStartDate(), "startTime不能为空");
            schedulerService.addSimpleJob(taskInfo);
        } else {
            Assert.notNull(taskInfo.getCronExpression(), "cron表达式不能为空");
            schedulerService.addCronJob(taskInfo);
        }
        return ResultBody.ok();
    }

    /**
     * 修改远程调度任务
     *
     * @param jobName        任务名称
     * @param jobDescription 任务描述
     * @param jobType        任务类型
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param repeatInterval 间隔时间
     * @param repeatCount    重试次数
     * @param cron           cron表达式
     * @param serviceId      服务名
     * @param path           请求路径
     * @param method         请求类型
     * @param contentType    响应类型
     * @param alarmMail      告警邮箱
     * @return
     */
    @ApiOperation(value = "修改远程调度任务", notes = "修改远程调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobDescription", value = "任务描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobType", value = "任务类型", required = true, allowableValues = "simple,cron", paramType = "form"),
            @ApiImplicitParam(name = "cron", value = "cron表达式", required = false, paramType = "form"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatInterval", value = "间隔时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatCount", value = "重试次数", required = false, paramType = "form"),
            @ApiImplicitParam(name = "serviceId", value = "服务名", required = true, paramType = "form"),
            @ApiImplicitParam(name = "path", value = "请求路径", required = true, paramType = "form"),
            @ApiImplicitParam(name = "method", value = "请求类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "contentType", value = "响应类型", required = false, paramType = "form"),
            @ApiImplicitParam(name = "alarmMail", value = "告警邮箱", required = false, paramType = "form"),
    })
    @PostMapping("/job/update/http")
    public ResultBody updateHttpJob(@RequestParam(name = "jobName") String jobName,
                                    @RequestParam(name = "jobDescription") String jobDescription,
                                    @RequestParam(name = "jobType") String jobType,
                                    @RequestParam(name = "cron", required = false) String cron,
                                    @RequestParam(name = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                    @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                    @RequestParam(name = "repeatInterval", required = false, defaultValue = "0") Long repeatInterval,
                                    @RequestParam(name = "repeatCount", required = false, defaultValue = "0") Integer repeatCount,
                                    @RequestParam(name = "serviceId") String serviceId,
                                    @RequestParam(name = "path") String path,
                                    @RequestParam(name = "method", required = false) String method,
                                    @RequestParam(name = "contentType", required = false) String contentType,
                                    @RequestParam(name = "alarmMail", required = false) String alarmMail) {
        TaskInfo taskInfo = new TaskInfo();
        Map data = Maps.newHashMap();
        data.put("serviceId", serviceId);
        data.put("method", method);
        data.put("path", path);
        data.put("contentType", contentType);
        data.put("alarmMail", alarmMail);
        taskInfo.setData(data);
        taskInfo.setJobName(jobName);
        taskInfo.setJobDescription(jobDescription);
        taskInfo.setJobClassName(HttpExecuteJob.class.getName());
        taskInfo.setJobGroupName(Scheduler.DEFAULT_GROUP);
        taskInfo.setStartDate(startTime);
        taskInfo.setEndDate(endTime);
        taskInfo.setRepeatInterval(repeatInterval);
        taskInfo.setRepeatCount(repeatCount);
        taskInfo.setCronExpression(cron);
        if ("simple".equals(jobType)) {
            Assert.notNull(taskInfo.getStartDate(), "startTime不能为空");
            schedulerService.editSimpleJob(taskInfo);
        } else {
            Assert.notNull(taskInfo.getCronExpression(), "cron表达式不能为空");
            schedulerService.editCronJob(taskInfo);
        }
        return ResultBody.ok();
    }


    /**
     * 删除任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "删除任务", notes = "删除任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/job/delete")
    public ResultBody deleteJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
        return ResultBody.ok();
    }

    /**
     * 暂停任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "暂停任务", notes = "暂停任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/job/pause")
    public ResultBody pauseJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.pauseJob(jobName, Scheduler.DEFAULT_GROUP);
        return ResultBody.ok();
    }


    /**
     * 恢复任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "恢复任务", notes = "恢复任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/job/resume")
    public ResultBody resumeJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.resumeJob(jobName, Scheduler.DEFAULT_GROUP);
        return ResultBody.ok();
    }
}
