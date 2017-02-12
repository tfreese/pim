// Erzeugt: 02.03.2016
package de.freese.pim.common.spring.autoconfigure.executorservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import de.freese.pim.common.spring.TunedThreadPoolExecutorFactoryBean;

/**
 * AutoConfiguration für ein {@link ExecutorService}.<br>
 * Nur wenn noch kein {@link ExecutorService} vorhanden ist, wird ein {@link ExecutorService} erzeugt.
 * <p>
 * Beispiel ExecutorService: Threads leben max. 60 Sekunden, wenn es nix zu tun gibt; min. 2, max. 10 Threads, max. 100 Tasks in der Queue.<br>
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
@ConditionalOnMissingBean(ExecutorService.class)
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
        String threadNamePrefix = this.executorProperties.getThreadNamePrefix();
        int coreSize = this.executorProperties.getCorePoolSize();
        int maxSize = this.executorProperties.getMaxPoolSize();
        int queueCapacity = this.executorProperties.getQueueCapacity();
        int threadPriority = this.executorProperties.getThreadPriority();
        int keepAliveSeconds = this.executorProperties.getKeepAliveSeconds();
        RejectedExecutionHandler reh = this.executorProperties.getRejectedExecutionHandler();
        boolean allowCoreThreadTimeOut = this.executorProperties.isAllowCoreThreadTimeOut();

        StringBuilder sb = new StringBuilder();
        sb.append("Create ExecutorService with:");
        sb.append(" namePrefix={}");
        sb.append(", corePoolSize={}");
        sb.append(", maxPoolSize={}");
        sb.append(", queueCapacity={}");
        sb.append(", priority={}");
        sb.append(", keepAliveSeconds={}");

        LOGGER.info(sb.toString(), threadNamePrefix, coreSize, maxSize, queueCapacity, threadPriority, keepAliveSeconds);

        ThreadPoolExecutorFactoryBean bean = new TunedThreadPoolExecutorFactoryBean();
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

    // /**
    // * @return {@link ExecutorService}
    // */
    // @Bean(destroyMethod = "shutdownNow")
    // public ExecutorService executorService()
    // {
    // String threadNamePrefix = this.executorProperties.getThreadNamePrefix();
    // int coreSize = this.executorProperties.getCorePoolSize();
    // int maxSize = this.executorProperties.getMaxPoolSize();
    // int queueCapacity = this.executorProperties.getQueueCapacity();
    // int threadPriority = this.executorProperties.getThreadPriority();
    // int keepAliveSeconds = this.executorProperties.getKeepAliveSeconds();
    // RejectedExecutionHandler reh = this.executorProperties.getRejectedExecutionHandler();
    // boolean allowCoreThreadTimeOut = this.executorProperties.isAllowCoreThreadTimeOut();
    //
    // StringBuilder sb = new StringBuilder();
    // sb.append("Create ExecutorService with:");
    // sb.append(" namePrefix={}");
    // sb.append(", corePoolSize={}");
    // sb.append(", maxPoolSize={}");
    // sb.append(", queueCapacity={}");
    // sb.append(", priority={}");
    // sb.append(", keepAliveSeconds={}");
    //
    // LOGGER.info(sb.toString(), threadNamePrefix, coreSize, maxSize, queueCapacity, threadPriority, keepAliveSeconds);
    //
    // BlockingQueue<Runnable> queue = null;
    //
    // if (queueCapacity > 0)
    // {
    // queue = new TunedLinkedBlockingQueue<>(queueCapacity);
    // }
    // else
    // {
    // queue = new SynchronousQueue<>();
    // }
    //
    // ThreadFactory tf = new PIMThreadFactory(threadNamePrefix, threadPriority);
    // ThreadPoolExecutor tpe = new ThreadPoolExecutor(coreSize, maxSize, keepAliveSeconds, TimeUnit.SECONDS, queue, tf, reh);
    //
    // if (queue instanceof TunedLinkedBlockingQueue)
    // {
    // TunedLinkedBlockingQueue<Runnable> tlbq = (TunedLinkedBlockingQueue<Runnable>) queue;
    //
    // tlbq.setCurrentSize(tpe::getPoolSize);
    // tlbq.setMaxSize(tpe::getMaximumPoolSize);
    // }
    //
    // ExecutorService executorService = Executors.unconfigurableExecutorService(tpe);
    //
    // return executorService;
    // }
}