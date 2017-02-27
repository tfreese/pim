// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@ComponentScan(basePackages =
{
        "de.freese.pim"
})
public abstract class AbstractPIMClientConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link AbstractPIMClientConfig}
     */
    public AbstractPIMClientConfig()
    {
        super();
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorService()
    {
        int coreSize = Math.min(Runtime.getRuntime().availableProcessors() * 2, 8);
        int maxSize = coreSize;
        int queueSize = maxSize * 10;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setKeepAliveSeconds(0);
        bean.setQueueCapacity(queueSize);
        bean.setThreadPriority(5);
        bean.setThreadNamePrefix("client-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @param pimHome String
     * @return {@link Path}
     */
    @Bean
    @Primary
    public Path pimHomePath(@Value("${pim.home}") final String pimHome)
    {
        Path path = Paths.get(pimHome);

        return path;
    }

    /**
     * @return {@link ScheduledExecutorFactoryBean}
     */
    @Bean
    public ScheduledExecutorFactoryBean scheduledExecutorService()
    {
        int poolSize = Math.max(Runtime.getRuntime().availableProcessors(), 4);

        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(poolSize);
        bean.setThreadPriority(5);
        bean.setThreadNamePrefix("scheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @param executorService {@link ExecutorService}
     * @return {@link AsyncTaskExecutor}
     */
    @Bean
    public AsyncTaskExecutor taskExecutor(final ExecutorService executorService)
    {
        AsyncTaskExecutor bean = new ConcurrentTaskExecutor(executorService);

        return bean;
    }

    /**
     * @Bean({"taskScheduler", "taskExecutor"})
     *
     * @param executorService {@link ExecutorService}
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     * @return {@link TaskScheduler}
     */
    @Bean
    public TaskScheduler taskScheduler(final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService)
    {
        ConcurrentTaskScheduler bean = new ConcurrentTaskScheduler(executorService, scheduledExecutorService);

        return bean;
    }
}
