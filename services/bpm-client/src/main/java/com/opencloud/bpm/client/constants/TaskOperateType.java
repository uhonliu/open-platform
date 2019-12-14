package com.opencloud.bpm.client.constants;

/**
 * 流程操作类型
 *
 * @author: liuyadu
 * @date: 2019/4/4 13:25
 * @description:
 */
public enum TaskOperateType {
    /**
     * 任务审批
     */
    TASK_APPROVAL,
    /**
     * 任务退回
     */
    TASK_RETREAT,
    /**
     * 流程暂停/挂起
     */
    RPOCESS_PAUSE,
    /**
     * 流程恢复
     */
    PROCESS_RESUME,
    /**
     * 流程关闭/终止
     */
    PROCESS_CLOSE
}
