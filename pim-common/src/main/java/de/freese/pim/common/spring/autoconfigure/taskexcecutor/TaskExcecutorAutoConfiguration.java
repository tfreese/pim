// Created: 23.02.2017
package de.freese.pim.common.spring.autoconfigure.taskexcecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

/**
 * @author Thomas Freese
 */
@Configuration
@ConditionalOnMissingBean(TaskExecutor.class) // Nur wenn TaskExecutor noch nicht im SpringContext ist.
@ConditionalOnBean(ExecutorService.class)
// @AutoConfigureAfter(ThreadPoolExecutorAutoConfiguration.class)
public class TaskExcecutorAutoConfiguration
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExcecutorAutoConfiguration.class);

    /**
    *
    */
    @Resource
    private Executor executor = null;

    /**
     * Erzeugt eine neue Instanz von {@link TaskExcecutorAutoConfiguration}
     */
    public TaskExcecutorAutoConfiguration()
    {
        super();
    }

    /**
     * @return {@link TaskExecutor}
     */
    @Bean
    public TaskExecutor taskExecutor()
    {
        LOGGER.info("Create TaskExecutor");

        TaskExecutor bean = new ConcurrentTaskExecutor(this.executor);

        return bean;
    }
}
