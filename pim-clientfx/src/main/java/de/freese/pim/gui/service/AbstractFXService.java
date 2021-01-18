// Created: 16.02.2017
package de.freese.pim.gui.service;

import javax.annotation.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.freese.pim.common.service.AbstractRemoteService;

/**
 * Basis-Service für JavaFX.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFXService extends AbstractRemoteService
{
    /**
    *
    */
    private ObjectMapper jsonMapper;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFXService}
     */
    protected AbstractFXService()
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
