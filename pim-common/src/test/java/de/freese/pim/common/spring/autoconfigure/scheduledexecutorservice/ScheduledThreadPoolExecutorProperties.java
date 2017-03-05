// Erzeugt: 03.03.2016
package de.freese.pim.common.spring.autoconfigure.scheduledexecutorservice;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties für {@link ScheduledThreadPoolExecutorAutoConfiguration}.
 *
 * @author Thomas Freese
 */
@ConfigurationProperties(prefix = "scheduledthreadpool")
public class ScheduledThreadPoolExecutorProperties
{
    /**
     *
     */
    private int poolSize = 1;

    /**
    *
    */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     *
     */
    private String threadNamePrefix = "scheduler";

    /**
     *
     */
    private int threadPriority = Thread.NORM_PRIORITY;

    /**
     * Erzeugt eine neue Instanz von {@link ScheduledThreadPoolExecutorProperties}
     */
    public ScheduledThreadPoolExecutorProperties()
    {
        super();
    }

    /**
     * @return int
     */
    public int getPoolSize()
    {
        return this.poolSize;
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
     * @param poolSize int
     */
    public void setPoolSize(final int poolSize)
    {
        this.poolSize = poolSize;
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
