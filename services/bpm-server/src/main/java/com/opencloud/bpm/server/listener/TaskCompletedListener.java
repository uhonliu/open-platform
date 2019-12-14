package com.opencloud.bpm.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * 任务完成监听
 *
 * @author liuyadu
 */
@Slf4j
public class TaskCompletedListener implements ActivitiEventListener {
    @Override
    public void onEvent(ActivitiEvent event) {
        TaskEntity taskEntity = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();
        log.debug("==> eventType=[{}]", event.getType().name());
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }
}