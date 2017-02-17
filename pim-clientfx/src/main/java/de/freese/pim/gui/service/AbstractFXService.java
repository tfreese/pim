// Created: 16.02.2017
package de.freese.pim.gui.service;

import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Basis-Service für JavaFX.
 *
 * @author Thomas Freese
 */
public class AbstractFXService
{
    /**
    *
    */
    private ExecutorService executorService = null;

    /**
    *
    */
    private ObjectMapper jsonMapper = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXService}
     */
    public AbstractFXService()
    {
        super();
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
     * @param jsonMapper {@link ObjectMapper}
     */
    @Resource
    public void setJsonMapper(final ObjectMapper jsonMapper)
    {
        this.jsonMapper = jsonMapper;
    }

    /**
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * @return {@link ObjectMapper}
     */
    protected ObjectMapper getJsonMapper()
    {
        return this.jsonMapper;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
