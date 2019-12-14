package com.opencloud.bpm.server.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 用户任务执行监听
 */
public class UserTaskListener implements TaskListener {
    /**
     * 指定个人任务和组任务的办理人
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        //从数据库中查询出指定的班里人
        String assignee = "张无忌";
        //指定个人任务
        delegateTask.setAssignee(assignee);
    }
}
