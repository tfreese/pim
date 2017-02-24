// Created: 23.02.2017
package de.freese.pim.common.spring.autoconfigure.taskexcecutor;

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
@ConditionalOnBean(ExecutorService.class) // Nur wenn ExecutorService im SpringContext ist.
// @AutoConfigureAfter(ThreadPoolExecutorAutoConfiguration.class)
public class TaskExecutorAutoConfiguration
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutorAutoConfiguration.class);

    /**
    *
    */
    @Resource
    private ExecutorService executorService = null;

    /**
     * Erzeugt eine neue Instanz von {@link TaskExecutorAutoConfiguration}
     */
    public TaskExecutorAutoConfiguration()
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

        TaskExecutor bean = new ConcurrentTaskExecutor(this.executorService);

        return bean;
    }
}
