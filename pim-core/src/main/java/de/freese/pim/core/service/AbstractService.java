// Created: 07.02.2017
package de.freese.pim.core.service;

import jakarta.annotation.Resource;

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
public abstract class AbstractService implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private AsyncTaskExecutor taskExecutor;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Resource(name = "taskExecutor")
    public void setTaskExecutor(final AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected AsyncTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
}
