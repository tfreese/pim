// Created: 16.02.2017
package de.freese.pim.gui.service;

import javax.annotation.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.freese.pim.common.service.AbstractService;

/**
 * Basis-Service für JavaFX.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFXService extends AbstractService
{
    /**
    *
    */
    private ObjectMapper jsonMapper = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXService}
     */
    public AbstractFXService()
    {
        super();
    }

    /**
     * @return {@link ObjectMapper}
     */
    protected ObjectMapper getJsonMapper()
    {
        return this.jsonMapper;
    }

    /**
     * @param jsonMapper {@link ObjectMapper}
     */
    @Resource
    public void setJsonMapper(final ObjectMapper jsonMapper)
    {
        this.jsonMapper = jsonMapper;
    }
}
