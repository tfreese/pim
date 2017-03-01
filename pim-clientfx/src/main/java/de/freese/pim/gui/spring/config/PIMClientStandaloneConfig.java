// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("ClientStandalone")
public class PIMClientStandaloneConfig extends AbstractPIMClientConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link PIMClientStandaloneConfig}
     */
    public PIMClientStandaloneConfig()
    {
        super();
    }

    // /**
    // * @return {@link AsyncTaskExecutor}
    // */
    // @Bean
    // public AsyncTaskExecutor serverTaskExecutor()
    // {
    // int coreSize = Math.min(Runtime.getRuntime().availableProcessors() * 2, 8);
    // int maxSize = coreSize;
    // int queueSize = maxSize * 5;
    //
    // ThreadPoolTaskExecutor bean = new ThreadPoolTaskExecutor();
    // bean.setCorePoolSize(coreSize);
    // bean.setMaxPoolSize(maxSize);
    // bean.setQueueCapacity(queueSize);
    // bean.setKeepAliveSeconds(0);
    // bean.setThreadNamePrefix("server-");
    // bean.setThreadPriority(Thread.NORM_PRIORITY);
    // bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    // bean.setAllowCoreThreadTimeOut(false);
    //
    // return bean;
    // }
}
