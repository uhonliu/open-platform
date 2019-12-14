package com.opencloud.msg.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.common.model.PageParams;
import com.opencloud.msg.client.model.entity.WebHookLogs;

/**
 * 异步通知日志接口
 *
 * @author: liuyadu
 * @date: 2019/2/13 14:39
 * @description:
 */
public interface WebHookLogsService {
    /**
     * 添加日志
     *
     * @param log
     */
    void addLog(WebHookLogs log);

    /**
     * 更细日志
     *
     * @param log
     */
    void modifyLog(WebHookLogs log);


    /**
     * 根据主键获取日志
     *
     * @param logId
     * @return
     */
    WebHookLogs getLog(String logId);

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<WebHookLogs> findListPage(PageParams pageParams);
}
