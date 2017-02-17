// Created: 07.02.2017
package de.freese.pim.server.service;

import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

    /**
    *
    */
    private ExecutorService executorService = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * @param executorService {@link ExecutorService}
     */
    @Resource
    public void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    /**
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
