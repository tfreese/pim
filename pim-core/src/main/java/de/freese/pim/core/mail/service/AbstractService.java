// Created: 07.02.2017
package de.freese.pim.core.mail.service;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.service.ISettingsService;

/**
 * Basis-Implementierung eines Service.
 *
 * @author Thomas Freese
 */
public abstract class AbstractService
{
    /**
    *
    */
    private ExecutorService executorService = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private ISettingsService settingsService = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractService}
     */
    public AbstractService()
    {
        super();
    }

    /**
     * @param executorService {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    /**
     * @param settingsService {@link ISettingsService}
     */
    public void setSettingsService(final ISettingsService settingsService)
    {
        this.settingsService = settingsService;
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

    /**
     * @return {@link ISettingsService}
     */
    protected ISettingsService getSettingsService()
    {
        return this.settingsService;
    }
}
