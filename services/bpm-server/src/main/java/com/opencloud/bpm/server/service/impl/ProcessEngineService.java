package com.opencloud.bpm.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.opencloud.common.utils.StringUtils;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程引擎通用类
 *
 * @author: liuyadu
 * @date: 2019/4/4 10:53
 * @description:
 */
@Service
public class ProcessEngineService {
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected FormService formService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected IdentityService identityService;

    /**
     * 启动流程并返回流程实例
     *
     * @param processDefinitionKey
     * @param businessKey
     * @param variables
     * @return
     */
    public ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        if (variables == null) {
            variables = Maps.newHashMap();
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        return processInstance;
    }

    /**
     * 读取已结束的流程
     *
     * @param processDefinitionKey
     * @param startRow
     * @param endRow
     * @return
     */
    public List<HistoricProcessInstance> findFinishedProcessInstaces(String processDefinitionKey, int startRow, int endRow) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().finished().orderByProcessInstanceEndTime().desc();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }
        List<HistoricProcessInstance> list = query.listPage(startRow, endRow);
        return list;
    }

    /**
     * 读取运行中的流程
     *
     * @param processDefinitionKey
     * @param startRow
     * @param endRow
     * @return
     */
    public List<ProcessInstance> findRunningProcessInstaces(String processDefinitionKey, int startRow, int endRow) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().active()
                .orderByProcessInstanceId().desc();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }
        List<ProcessInstance> list = query.listPage(startRow, endRow);
        return list;
    }

    /**
     * 渲染任务表单
     *
     * @param taskId
     * @return 返回渲染后内容
     */
    public String getRenderedTaskForm(String taskId) {
        return formService.getRenderedTaskForm(taskId).toString();
    }

    /**
     * 渲染流程启动表单
     *
     * @param processDefinitionId
     * @return
     */
    public String getRenderedStartForm(String processDefinitionId) {
        return formService.getRenderedStartForm(processDefinitionId).toString();
    }


    /**
     * 根据流程实例ID获取对应的流程实例
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    public ProcessInstance findProcessInstanceById(String processInstanceId) throws Exception {
        // 找到流程实例
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance == null) {
            throw new Exception("流程不存在或已结束!");
        }
        return processInstance;
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception {
        return findProcessInstanceById(findTaskById(taskId).getProcessInstanceId());
    }


    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).includeTaskLocalVariables().singleResult();
        if (task == null) {
            throw new Exception("任务实例未找到!");
        }
        return task;
    }


    /**
     * 根据流程实例ID和任务key值查询所有同级任务集合
     *
     * @param processInstanceId
     * @param key
     * @return
     */
    private List<Task> findTaskListByKey(String processInstanceId, String key) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).includeTaskLocalVariables().taskDefinitionKey(key).list();
    }

    /**
     * 提交流程
     *
     * @param taskId    当前任务ID
     * @param variables 流程变量
     * @throws Exception
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        taskService.complete(taskId, variables);
    }

    /**
     * 将任务转移给其他人办理
     *
     * @param taskId   当前任务节点ID
     * @param userName 被转办人userName
     */
    public void transferAssignee(String taskId, String userName) {
        taskService.setAssignee(taskId, userName);
    }


    /**
     * 将任务委托给其他人办理
     *
     * @param taskId
     * @param userName
     */
    public void delegateTask(String taskId, String userName) {
        taskService.delegateTask(taskId, userName);
    }

    /**
     * 被委托人处理任务
     * 被委托人执行完毕后，任务工具又回到委托人名下，即A委托B处理，B处理完后，任务又回到A名下。
     *
     * @param taskId
     * @param variables
     */
    public void delegateTask(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        taskService.resolveTask(taskId, variables);
    }

    /**
     * 获取代办列表
     *
     * @param userId
     * @return
     */
    public IPage<Task> findTodoTask(String userId, int firstResult, int maxResults) {
        //得到用户待办
        TaskQuery query = taskService.createTaskQuery();
        query.includeTaskLocalVariables();
        if (StringUtils.isNotBlank(userId)) {
            query.taskAssignee(userId);
        }
        List<Task> list = query.listPage(firstResult, maxResults);
        IPage page = new Page();
        page.setRecords(list);
        page.setTotal(query.count());
        return page;
    }

    /**
     * 查询流程定义列表
     *
     * @return
     */
    public IPage<ProcessDefinition> findProcessDefinition(String key, int firstResult, int maxResults) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        // 只查询最新版本
        query.latestVersion();
        if (StringUtils.isNotBlank(key)) {
            String processKey = "%" + key + "%";
            query.processDefinitionKeyLike(processKey);
        }
        List<ProcessDefinition> list = query.listPage(firstResult, maxResults);
        IPage page = new Page();
        page.setRecords(list);
        page.setTotal(query.count());
        return page;
    }

    /**
     * 根据部署ID查询部署
     *
     * @param deploymentId
     * @return
     */
    public Deployment getDeploymentById(String deploymentId) {
        return repositoryService.createDeploymentQuery()
                // 根据部署ID查询
                .deploymentId(deploymentId)
                // 返回唯一结果
                .singleResult();
    }

    /**
     * 根据部署ID删除流程部署
     *
     * @param deploymentId
     */
    public void deleteDeployment(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId);

    }
}
