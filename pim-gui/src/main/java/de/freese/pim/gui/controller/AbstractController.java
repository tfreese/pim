// Created: 29.11.2016
package de.freese.pim.gui.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.gui.PIMApplication;
import javafx.stage.Window;

/**
 * Abstract Implementierung von {@link IController}.
 *
 * @author Thomas Freese (EFREEST / AuVi)
 */
@SuppressWarnings("restriction")
public abstract class AbstractController implements IController
{
    /**
    *
    */
    public final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractController}
     */
    public AbstractController()
    {
        super();
    }

    /**
     * @return {@link DataSource}
     */
    public DataSource getDataSource()
    {
        return PIMApplication.getDataSource();
    }

    /**
     * @return {@link ExecutorService}
     */
    public ExecutorService getExecutorService()
    {
        return PIMApplication.getExecutorService();
    }

    /**
     * @return {@link Logger}
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link Window}
     */
    public Window getMainWindow()
    {
        return PIMApplication.getMainWindow();
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    public ScheduledExecutorService getScheduledExecutorService()
    {
        return PIMApplication.getScheduledExecutorService();
    }

    /**
     * @return {@link ISettingsService}
     */
    public ISettingsService getSettingService()
    {
        return PIMApplication.getSettingService();
    }
}
