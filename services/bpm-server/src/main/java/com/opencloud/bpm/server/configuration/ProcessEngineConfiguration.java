package com.opencloud.bpm.server.configuration;

import com.opencloud.bpm.server.listener.TaskCompletedListener;
import com.opencloud.bpm.server.listener.TaskCreatedListener;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 声名为配置类，继承Activiti抽象配置类
 *
 * @author liuyadu
 */
@Configuration
public class ProcessEngineConfiguration extends AbstractProcessEngineAutoConfiguration {
    @Bean
    public TaskCompletedListener taskCompletedListener() {
        return new TaskCompletedListener();
    }

    @Bean
    public TaskCreatedListener taskCreatedListener() {
        return new TaskCreatedListener();
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager transactionManager,
                                                                             SpringAsyncExecutor springAsyncExecutor) throws IOException {
        SpringProcessEngineConfiguration engineConfiguration = baseSpringProcessEngineConfiguration(
                dataSource,
                transactionManager,
                springAsyncExecutor);
        Map<String, List<ActivitiEventListener>> typedListeners = new HashMap<>();
        typedListeners.put("TASK_CREATED", Collections.singletonList(taskCreatedListener()));
        typedListeners.put("TASK_COMPLETED", Collections.singletonList(taskCompletedListener()));
        engineConfiguration.setTypedEventListeners(typedListeners);
        return engineConfiguration;
    }
}