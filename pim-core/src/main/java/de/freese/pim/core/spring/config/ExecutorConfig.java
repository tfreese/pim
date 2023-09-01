// Created: 22.10.2021
package de.freese.pim.core.spring.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import de.freese.pim.core.concurrent.PIMForkJoinWorkerThreadFactory;

/**
 * @author Thomas Freese
 */
@Configuration
public class ExecutorConfig {
    public ExecutorConfig() {
        super();

        // System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
        // Integer.toString(Runtime.getRuntime().availableProcessors()));
        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", PIMForkJoinWorkerThreadFactory.class.getName());
    }

    @Bean
    @ConditionalOnMissingBean({Executor.class, ExecutorService.class})
    public ThreadPoolExecutorFactoryBean executorService() {
        int coreSize = Math.max(8, Runtime.getRuntime().availableProcessors());
        int maxSize = coreSize * 2;
        int queueSize = maxSize * 2;
        int keepAliveSeconds = 60;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setQueueCapacity(queueSize);
        bean.setKeepAliveSeconds(keepAliveSeconds);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("pim-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(ScheduledExecutorService.class)
    public ScheduledExecutorFactoryBean scheduledExecutorService() {
        int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 4);

        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(poolSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("scheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * Wird für {@link EnableAsync} benötigt.<br>
     *
     * @see AsyncConfigurer
     */
    @Bean({"taskExecutor", "asyncTaskExecutor"})
    @ConditionalOnMissingBean({TaskExecutor.class, AsyncTaskExecutor.class})
    public AsyncTaskExecutor springTaskExecutor(final ExecutorService executorService) {
        return new ConcurrentTaskExecutor(executorService);
    }

    /**
     * Wird für {@link EnableScheduling} benötigt.<br>
     *
     * @see SchedulingConfigurer
     */
    @Bean("taskScheduler")
    @ConditionalOnMissingBean(TaskScheduler.class)
    public TaskScheduler springTaskScheduler(final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService) {
        return new ConcurrentTaskScheduler(executorService, scheduledExecutorService);
    }
}
