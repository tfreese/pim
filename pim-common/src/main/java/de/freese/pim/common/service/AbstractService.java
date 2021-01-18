// Created: 07.02.2017
package de.freese.pim.common.service;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * Basis-Implementierung eines Service.
 *
 * @author Thomas Freese
 */
public abstract class AbstractService implements ApplicationContextAware
{
    /**
     *
     */
    private ApplicationContext applicationContext;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private AsyncTaskExecutor taskExecutor;

    // /**
    // *
    // */
    // private TaskScheduler taskScheduler = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractService}
     */
    protected AbstractService()
    {
        super();
    }

    /**
     * @return {@link ApplicationContext}
     */
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link AsyncTaskExecutor}
     */
    protected AsyncTaskExecutor getTaskExecutor()
    {
        return this.taskExecutor;
        // return this.executorService;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * @param taskExecutor {@link AsyncTaskExecutor}
     */
    @Resource(name = "taskExecutor")
    public void setTaskExecutor(final AsyncTaskExecutor taskExecutor)
    {
        this.taskExecutor = taskExecutor;
    }
}
