// Created: 29.11.2016
package de.freese.pim.gui.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.gui.PIMApplication;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.stage.Window;

/**
 * Abstract Implementierung von {@link IController}.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public abstract class AbstractController implements IController
{
    /**
    *
    */
    private boolean activated = false;

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
     * @see de.freese.pim.gui.controller.IController#getMainNode()
     */
    @Override
    public Node getMainNode()
    {
        return null;
    }

    /**
     * @return {@link Window}
     */
    public Window getMainWindow()
    {
        return PIMApplication.getMainWindow();
    }

    /**
     * @see de.freese.pim.gui.controller.IController#getNaviNode()
     */
    @Override
    public Node getNaviNode()
    {
        return null;
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    public ScheduledExecutorService getScheduledExecutorService()
    {
        return PIMApplication.getScheduledExecutorService();
    }

    /**
     * @see de.freese.pim.gui.controller.IController#getToolBar()
     */
    @Override
    public ToolBar getToolBar()
    {
        return null;
    }

    /**
     * Liefert true, wenn der Controller bereits aktiviert wurde.
     *
     * @return boolean
     */
    protected boolean isActivated()
    {
        return this.activated;
    }

    /**
     * Setzt true, wenn der Controller bereits aktiviert wurde.
     *
     * @param activated boolean
     */
    protected void setActivated(final boolean activated)
    {
        this.activated = activated;
    }
}
