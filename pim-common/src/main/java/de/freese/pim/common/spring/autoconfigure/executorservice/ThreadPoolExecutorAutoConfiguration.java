// Erzeugt: 02.03.2016
package de.freese.pim.common.spring.autoconfigure.executorservice;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

/**
 * AutoConfiguration für ein {@link ExecutorService}.<br>
 * Nur wenn noch kein {@link ExecutorService} vorhanden ist, wird ein {@link ExecutorService} erzeugt.
 * <p>
 * Beispiel ExecutorService: Threads leben max. 60 Sekunden, wenn es nix zu tun gibt; min. 2, max. 10 Threads, max. 100 Tasks in der
 * Queue.<br>
 * threadpool.thread-name-prefix=thread<br>
 * threadpool.thread-priority=5<br>
 * threadpool.core-pool-size=2<br>
 * threadpool.max-pool-size=10<br>
 * threadpool.queue-capacity=100<br>
 * threadpool.keep-alive-seconds=60<br>
 * threadpool.rejected-execution-handler=abort-policy<br>
 * <p>
 * Beispiel ExecutorService: Nur ein einziger Thread, max. 50 Tasks in der Queue.<br>
 * threadpool.thread-name-prefix=singlethread<br>
 * threadpool.core-pool-size=1<br>
 * threadpool.max-pool-size=1<br>
 * threadpool.queue-capacity=50<br>
 * threadpool.keep-alive-seconds=0<br>
 *
 * @author Thomas Freese
 */
@Configuration
@ConditionalOnMissingBean(ExecutorService.class) // Nur wenn ExecutorService noch nicht im SpringContext ist.
@ConditionalOnProperty(prefix = "threadpool", name = "enabled", matchIfMissing = false) // Nur wenn auch enabled.
@EnableConfigurationProperties(ThreadPoolExecutorProperties.class)
public class ThreadPoolExecutorAutoConfiguration
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolExecutorAutoConfiguration.class);

    /**
    *
    */
    @Resource
    private DataSource dataSource = null;

    /**
    *
    */
    @Resource
    private DataSourcePoolMetadataProvider dataSourcePoolMetadataProvider = null;

    /**
     *
     */
    @Resource
    private ThreadPoolExecutorProperties executorProperties = null;

    /**
     * Erzeugt eine neue Instanz von {@link ThreadPoolExecutorAutoConfiguration}
     */
    public ThreadPoolExecutorAutoConfiguration()
    {
        super();
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorService()
    {
        // http://www.nurkiewicz.com/2014/11/executorservice-10-tips-and-tricks.html

        String threadNamePrefix = this.executorProperties.getThreadNamePrefix();
        int coreSize = this.executorProperties.getCorePoolSize();
        int maxSize = this.executorProperties.getMaxPoolSize();
        int queueCapacity = this.executorProperties.getQueueCapacity();
        int threadPriority = this.executorProperties.getThreadPriority();
        int keepAliveSeconds = this.executorProperties.getKeepAliveSeconds();
        RejectedExecutionHandler reh = this.executorProperties.getRejectedExecutionHandler();
        boolean allowCoreThreadTimeOut = this.executorProperties.isAllowCoreThreadTimeOut();

        if (this.executorProperties.isPoolSizeNotGreaterAsDataSourceMaxActive())
        {
            // PoolSize orientiert sich an DataSource.
            DataSourcePoolMetadata metaData = this.dataSourcePoolMetadataProvider.getDataSourcePoolMetadata(this.dataSource);
            int dsMax = Optional.ofNullable(metaData).map(DataSourcePoolMetadata::getMax).orElse(Integer.MAX_VALUE);

            if ((dsMax > 0) && (maxSize > dsMax))
            {
                int oldCore = coreSize;
                int oldMax = maxSize;

                maxSize = dsMax;

                // Annahme, einfach 1/4 von maxSize.
                coreSize = Math.max(maxSize / 4, 1);

                LOGGER.info("Resize ThreadPool because DataSource dependency: old coreSize/maxSize={}/{}, new coreSize/maxSize={}/{}",
                        oldCore, oldMax, coreSize, maxSize);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Create ExecutorService with:");
        sb.append(" namePrefix={}");
        sb.append(", corePoolSize={}");
        sb.append(", maxPoolSize={}");
        sb.append(", queueCapacity={}");
        sb.append(", priority={}");
        sb.append(", keepAliveSeconds={}");

        LOGGER.info(sb.toString(), threadNamePrefix, coreSize, maxSize, queueCapacity, threadPriority, keepAliveSeconds);

        // ThreadPoolExecutorFactoryBean bean = new TunedThreadPoolExecutorFactoryBean();
        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setKeepAliveSeconds(keepAliveSeconds);
        bean.setQueueCapacity(queueCapacity);
        bean.setThreadPriority(threadPriority);
        bean.setThreadNamePrefix(threadNamePrefix);
        bean.setRejectedExecutionHandler(reh);
        bean.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);

        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }
}
