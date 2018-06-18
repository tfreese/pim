// Erzeugt: 02.03.2016
package de.freese.pim.common.spring.autoconfigure.taskscheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 * AutoConfiguration für einen {@link TaskScheduler}.<br>
 * Nur wenn ein {@link ExecutorService} und ein {@link ScheduledExecutorService} vorhanden ist, wird ein {@link TaskScheduler} erzeugt.<br>
 * Dieser wird für die Annotation {@link Async} benötigt, wenn kein {@link Executor} angegeben wurde.
 *
 * @author Thomas Freese
 */
@Configuration
@ConditionalOnMissingBean(TaskScheduler.class) // Nur wenn TaskScheduler noch nicht im SpringContext ist.
@ConditionalOnBean(
{
        ExecutorService.class, ScheduledExecutorService.class
}) // Nur wenn Executor und ScheduledExecutorService im SpringContext ist.
@ConditionalOnProperty(prefix = "taskscheduler", name = "enabled", matchIfMissing = false) // Nur wenn auch enabled.
// @AutoConfigureAfter(
// {
// ThreadPoolExecutorAutoConfiguration.class, ScheduledThreadPoolExecutorAutoConfiguration.class
// })
public class TaskSchedulerAutoConfiguration
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulerAutoConfiguration.class);

    /**
     *
     */
    @Resource
    private ExecutorService executorService = null;

    /**
     *
     */
    @Resource
    private ScheduledExecutorService scheduledExecutorService = null;

    /**
     * Erzeugt eine neue Instanz von {@link TaskSchedulerAutoConfiguration}
     */
    public TaskSchedulerAutoConfiguration()
    {
        super();
    }

    /**
     * , "taskExecutor"
     *
     * @return {@link TaskScheduler}
     */
    @Bean("taskScheduler")
    public TaskScheduler taskScheduler()
    {
        LOGGER.info("Create TaskScheduler");

        ConcurrentTaskScheduler bean = new ConcurrentTaskScheduler(this.executorService, this.scheduledExecutorService);

        return bean;
    }
}
