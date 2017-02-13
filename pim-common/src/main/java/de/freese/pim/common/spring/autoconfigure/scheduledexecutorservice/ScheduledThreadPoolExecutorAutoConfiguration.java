// Erzeugt: 02.03.2016
package de.freese.pim.common.spring.autoconfigure.scheduledexecutorservice;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

/**
 * AutoConfiguration für ein {@link ScheduledExecutorService}.<br>
 * Nur wenn noch kein {@link ScheduledExecutorService} vorhanden ist, wird ein {@link ScheduledExecutorService} erzeugt.
 * <p>
 * Beispiel ScheduledExecutor: max. 3 Tasks<br>
 * scheduledthreadpool.thread-name-prefix=scheduler<br>
 * scheduledthreadpool.pool-size=3<br>
 * scheduledthreadpool.thread-priority=5<br>
 * scheduledthreadpool.rejected-execution-handler=abort-policy<br>
 *
 * @author Thomas Freese
 */
@Configuration
@ConditionalOnMissingBean(ScheduledExecutorService.class) // Nur wenn ScheduledExecutorService noch nicht im SpringContext ist.
@EnableConfigurationProperties(ScheduledThreadPoolExecutorProperties.class)
public class ScheduledThreadPoolExecutorAutoConfiguration
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledThreadPoolExecutorAutoConfiguration.class);

    /**
     *
     */
    @Resource
    private ScheduledThreadPoolExecutorProperties scheduledThreadPoolExecutorProperties = null;

    /**
     * Erzeugt eine neue Instanz von {@link ScheduledThreadPoolExecutorAutoConfiguration}
     */
    public ScheduledThreadPoolExecutorAutoConfiguration()
    {
        super();
    }

    /**
     * @return {@link ScheduledExecutorFactoryBean}
     */
    @Bean
    public ScheduledExecutorFactoryBean scheduledExecutorService()
    {
        String threadNamePrefix = this.scheduledThreadPoolExecutorProperties.getThreadNamePrefix();
        int poolSize = this.scheduledThreadPoolExecutorProperties.getPoolSize();
        int threadPriority = this.scheduledThreadPoolExecutorProperties.getThreadPriority();
        RejectedExecutionHandler reh = this.scheduledThreadPoolExecutorProperties.getRejectedExecutionHandler();

        StringBuilder sb = new StringBuilder();
        sb.append("Create ScheduledExecutorService with:");
        sb.append(" namePrefix={}");
        sb.append(", poolSize={}");
        sb.append(", priority={}");

        LOGGER.info(sb.toString(), threadNamePrefix, poolSize, threadPriority);

        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(poolSize);
        bean.setThreadPriority(threadPriority);
        bean.setThreadNamePrefix(threadNamePrefix);
        bean.setRejectedExecutionHandler(reh);

        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }
}
