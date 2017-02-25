// Created: 07.02.2017
package de.freese.pim.common.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
    private ApplicationContext applicationContext = null;

    // /**
    // *
    // */
    // private ExecutorService executorService = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private AsyncTaskExecutor taskExecutor = null;

    // /**
    // *
    // */
    // private TaskScheduler taskScheduler = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractService}
     */
    public AbstractService()
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

    // /**
    // * @param executorService {@link ExecutorService}
    // */
    // @Resource
    // public void setExecutorService(final ExecutorService executorService)
    // {
    // this.executorService = executorService;
    // }

    /**
     * @param taskExecutor {@link AsyncTaskExecutor}
     */
    @Resource
    public void setTaskExecutor(final AsyncTaskExecutor taskExecutor)
    {
        this.taskExecutor = taskExecutor;
    }

    /**
     * @param value String
     * @return String
     */
    protected String urlDecode(final String value)
    {
        if (value == null)
        {
            return null;
        }

        try
        {
            String encoded = URLDecoder.decode(value.trim(), "UTF-8");

            return encoded;
        }
        catch (UnsupportedEncodingException ueex)
        {
            throw new RuntimeException(ueex);
        }
    }

    /**
     * @param value String
     * @return String
     */
    protected String urlEncode(final String value)
    {
        if (value == null)
        {
            return null;
        }

        try
        {
            String encoded = URLEncoder.encode(value.trim(), "UTF-8");

            return encoded;
        }
        catch (UnsupportedEncodingException ueex)
        {
            throw new RuntimeException(ueex);
        }
    }
}
