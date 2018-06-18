// Erzeugt: 03.03.2016
package de.freese.pim.common.spring.autoconfigure.executorservice;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties für {@link ThreadPoolExecutorAutoConfiguration}.
 *
 * @author Thomas Freese
 */
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolExecutorProperties
{
    /**
     *
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     *
     */
    private int corePoolSize = 1;

    /**
     *
     */
    private int keepAliveSeconds = 60;

    /**
     *
     */
    private int maxPoolSize = 10;

    /**
    *
    */
    private boolean poolSizeNotGreaterAsDataSourceMaxActive = false;

    /**
     *
     */
    private int queueCapacity = 0;

    /**
     *
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     *
     */
    private String threadNamePrefix = "thread";

    /**
     *
     */
    private int threadPriority = Thread.NORM_PRIORITY;

    /**
     * Erzeugt eine neue Instanz von {@link ThreadPoolExecutorProperties}
     */
    public ThreadPoolExecutorProperties()
    {
        super();
    }

    /**
     * @return int
     */
    public int getCorePoolSize()
    {
        return this.corePoolSize;
    }

    /**
     * @return int
     */
    public int getKeepAliveSeconds()
    {
        return this.keepAliveSeconds;
    }

    /**
     * @return int
     */
    public int getMaxPoolSize()
    {
        return this.maxPoolSize;
    }

    /**
     * @return int
     */
    public int getQueueCapacity()
    {
        return this.queueCapacity;
    }

    /**
     * @return {@link RejectedExecutionHandler}
     */
    public RejectedExecutionHandler getRejectedExecutionHandler()
    {
        return this.rejectedExecutionHandler;
    }

    /**
     * @return String
     */
    public String getThreadNamePrefix()
    {
        return this.threadNamePrefix;
    }

    /**
     * @return int
     */
    public int getThreadPriority()
    {
        return this.threadPriority;
    }

    /**
     * @return boolean
     */
    public boolean isAllowCoreThreadTimeOut()
    {
        return this.allowCoreThreadTimeOut;
    }

    /**
     * @return boolean
     */
    public boolean isPoolSizeNotGreaterAsDataSourceMaxActive()
    {
        return this.poolSizeNotGreaterAsDataSourceMaxActive;
    }

    /**
     * @param allowCoreThreadTimeOut boolean
     */
    public void setAllowCoreThreadTimeOut(final boolean allowCoreThreadTimeOut)
    {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    /**
     * @param corePoolSize int
     */
    public void setCorePoolSize(final int corePoolSize)
    {
        this.corePoolSize = corePoolSize;
    }

    /**
     * @param keepAliveSeconds int
     */
    public void setKeepAliveSeconds(final int keepAliveSeconds)
    {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    /**
     * @param maxPoolSize int
     */
    public void setMaxPoolSize(final int maxPoolSize)
    {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * @param poolSizeNotGreaterAsDataSourceMaxActive boolean
     */
    public void setPoolSizeNotGreaterAsDataSourceMaxActive(final boolean poolSizeNotGreaterAsDataSourceMaxActive)
    {
        this.poolSizeNotGreaterAsDataSourceMaxActive = poolSizeNotGreaterAsDataSourceMaxActive;
    }

    /**
     * @param queueCapacity int
     */
    public void setQueueCapacity(final int queueCapacity)
    {
        this.queueCapacity = queueCapacity;
    }

    /**
     * @param rejectedExecutionHandler String
     */
    public void setRejectedExecutionHandler(final String rejectedExecutionHandler)
    {
        switch (rejectedExecutionHandler)
        {
            case "caller-runs-policy":
                this.rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;

            case "discard-oldest-policy":
                this.rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;

            case "discard-policy":
                this.rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                break;

            default:
                this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
    }

    /**
     * @param threadNamePrefix String
     */
    public void setThreadNamePrefix(final String threadNamePrefix)
    {
        this.threadNamePrefix = threadNamePrefix;
    }

    /**
     * @param threadPriority int
     */
    public void setThreadPriority(final int threadPriority)
    {
        this.threadPriority = threadPriority;
    }
}
