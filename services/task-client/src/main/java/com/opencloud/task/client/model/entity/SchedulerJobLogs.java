package com.opencloud.task.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuyadu
 */
@Data
@Accessors(chain = true)
@TableName("scheduler_job_logs")
public class SchedulerJobLogs implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long logId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 任务执行类
     */
    private String jobClass;

    /**
     * 任务描述
     */
    private String jobDescription;

    /**
     * 任务触发器
     */
    private String triggerClass;

    /**
     * 任务表达式
     */
    private String cronExpression;

    /**
     * 运行时间
     */
    private Long runTime;

    /**
     * 运行开始时间
     */
    private Date runStartTime;

    /**
     * 运行结束时间
     */
    private Date runEndTime;

    /**
     * 日志创建时间
     */
    private Date createTime;

    /**
     * 任务执行数据
     */
    private String jobData;

    /**
     * 异常
     */
    private String exception;

    /**
     * 状态：0-失败 1-成功
     */
    private Integer status;

    /**
     * 间隔时间（毫秒）
     */
    private Long repeatInterval;

    /**
     * 重复次数
     */
    private Integer repeatCount;

    /**
     * 起始时间
     */
    private Date startDate;

    /**
     * 终止时间
     */
    private Date endDate;
}
