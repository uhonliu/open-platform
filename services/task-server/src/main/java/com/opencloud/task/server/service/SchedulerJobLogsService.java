package com.opencloud.task.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.model.PageParams;
import com.opencloud.task.client.model.entity.SchedulerJobLogs;

/**
 * 异步通知日志接口
 *
 * @author: liuyadu
 * @date: 2019/2/13 14:39
 * @description:
 */
public interface SchedulerJobLogsService {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<SchedulerJobLogs> findListPage(PageParams pageParams);

    /**
     * 添加日志
     *
     * @param log
     */
    void addLog(SchedulerJobLogs log);

    /**
     * 更细日志
     *
     * @param log
     */
    void modifyLog(SchedulerJobLogs log);


    /**
     * 根据主键获取日志
     *
     * @param logId
     * @return
     */
    SchedulerJobLogs getLog(String logId);
}
