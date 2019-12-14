package com.opencloud.bpm.server.service.impl;

import com.google.common.collect.Maps;
import com.opencloud.bpm.client.constants.BpmConstants;
import com.opencloud.bpm.client.model.TaskOperate;
import com.opencloud.bpm.server.service.ProcessService;
import com.opencloud.common.exception.OpenAlertException;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 自定义流程接口
 *
 * @author: liuyadu
 * @date: 2019/4/4 14:00
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessServiceImpl extends ProcessEngineService implements ProcessService {
    /**
     * 执行任务
     *
     * @param taskOperate
     */
    @Override
    public void complete(TaskOperate taskOperate) {
        String taskId = taskOperate.getTaskId();
        String user = taskOperate.getUser();
        // 放入流程变量
        Map<String, Object> variables = Maps.newHashMap();
        // 设置任务变量_OPT
        variables.put(BpmConstants.TASK_OPERATE_KEY, taskOperate);
        // 使用任务id,获取任务对象，获取流程实例id
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new OpenAlertException("任务不存在");
        }
        //利用任务对象，获取流程实例id
        String processInstancesId = task.getProcessInstanceId();
        //由于流程用户上下文对象是线程独立的，所以要在需要的位置设置，要保证设置和获取操作在同一个线程中
        //批注人的名称  一定要写，不然查看的时候不知道人物信息
        Authentication.setAuthenticatedUserId(user);
        taskService.addComment(taskId, processInstancesId, taskOperate.getOperateType().name(), taskOperate.getComment());
        //执行任务
        completeTask(taskOperate.getTaskId(), variables);
    }
}
