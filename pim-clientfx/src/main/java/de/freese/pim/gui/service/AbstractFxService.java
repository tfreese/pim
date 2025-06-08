// Created: 16.02.2017
package de.freese.pim.gui.service;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.freese.pim.core.service.AbstractRemoteService;

/**
 * Basis-Service für JavaFX.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFxService extends AbstractRemoteService {
    private ObjectMapper jsonMapper;

    @Resource
    public void setJsonMapper(final ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    protected ObjectMapper getJsonMapper() {
        return jsonMapper;
    }
}
