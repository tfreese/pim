// Created: 16.02.2017
package de.freese.pim.gui.service;

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
     * @param jsonMapper {@link ObjectMapper}
     */
    public void setJsonMapper(final ObjectMapper jsonMapper)
    {
        this.jsonMapper = jsonMapper;
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
