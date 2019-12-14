package com.opencloud.task.server.configuration;

import com.opencloud.task.server.listenter.JobLogsListener;
import com.opencloud.task.server.service.SchedulerJobLogsService;
import com.opencloud.task.server.service.feign.EmailServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author liuyadu
 */
@Configuration
public class SchedulerConfiguration implements SchedulerFactoryBeanCustomizer {
    @Autowired
    private JobLogsListener jobLogsListener;

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        //延时5秒启动
        schedulerFactoryBean.setStartupDelay(5);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // 任务执行日志监听
        schedulerFactoryBean.setGlobalJobListeners(jobLogsListener);
    }

    @Bean
    public JobLogsListener jobLogsListener(EmailServiceClient emailServiceClient, SchedulerJobLogsService schedulerJobLogsService) {
        return new JobLogsListener(emailServiceClient, schedulerJobLogsService);
    }
}
