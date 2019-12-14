package com.opencloud.msg.server.dispatcher;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.opencloud.msg.client.model.BaseMessage;
import com.opencloud.msg.server.exchanger.MessageExchanger;
import com.opencloud.msg.server.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author woodev
 */
@Component
@Slf4j
public class MessageDispatcher implements ApplicationContextAware {
    private Collection<MessageExchanger> exchangers;

    private ExecutorService executorService;

    public MessageDispatcher() {
        int numOfThreads = Runtime.getRuntime().availableProcessors() * 2;
        executorService = new ThreadPoolExecutor(numOfThreads, numOfThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build());
        log.info("Init Notify ExecutorService , numOfThread : " + numOfThreads);
    }

    public void dispatch(BaseMessage notification) {
        if (notification != null && exchangers != null) {
            exchangers.forEach((exchanger) -> {
                if (exchanger.support(notification)) {
                    //添加到线程池进行处理
                    executorService.submit(new MessageTask(exchanger, notification));
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MessageExchanger> beansOfType = applicationContext.getBeansOfType(MessageExchanger.class);
        this.exchangers = beansOfType.values();
    }
}