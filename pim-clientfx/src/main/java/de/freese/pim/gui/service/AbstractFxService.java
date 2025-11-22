// Created: 16.02.2017
package de.freese.pim.gui.service;

import jakarta.annotation.Resource;

import tools.jackson.databind.json.JsonMapper;

import de.freese.pim.core.service.AbstractRemoteService;

/**
 * Basis-Service für JavaFX.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFxService extends AbstractRemoteService {
    private JsonMapper jsonMapper;

    @Resource
    public void setJsonMapper(final JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    protected JsonMapper getJsonMapper() {
        return jsonMapper;
    }
}
