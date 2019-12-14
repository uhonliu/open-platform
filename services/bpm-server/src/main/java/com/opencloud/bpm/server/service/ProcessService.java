package com.opencloud.bpm.server.service;

import com.opencloud.bpm.client.model.TaskOperate;

/**
 * 自定义流程接口
 *
 * @author: liuyadu
 * @date: 2019/4/4 13:54
 * @description:
 */
public interface ProcessService {
    /**
     * 执行日志
     *
     * @param taskOperate
     */
    void complete(TaskOperate taskOperate);
}
