// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
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
        int coreSize = Runtime.getRuntime().availableProcessors();
        int maxSize = coreSize * 2;
        int queueSize = maxSize * 3;
        int keepAliveSeconds = 60;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setQueueCapacity(queueSize);
        bean.setKeepAliveSeconds(keepAliveSeconds);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("client-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @param pimHome String
     *
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
     * @param executorService {@link ExecutorService}
     *
     * @return {@link AsyncTaskExecutor}
     */
    @Bean(
            {
                "taskExecutor", "serverTaskExecutor"
            })
    public AsyncTaskExecutor taskExecutor(final ExecutorService executorService)
    {
        AsyncTaskExecutor bean = new ConcurrentTaskExecutor(executorService);

        return bean;
    }
}
