// Created: 29.11.2016
package de.freese.pim.gui.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
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
    private final ExecutorService executorService;

    /**
    *
    */
    public final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractController}
     */
    public AbstractController()
    {
        super();

        this.executorService = PIMApplication.getExecutorService();
        this.scheduledExecutorService = PIMApplication.getScheduledExecutorService();
    }

    /**
     * @return {@link ExecutorService}
     */
    public ExecutorService getExecutorService()
    {
        return this.executorService;
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
        return this.scheduledExecutorService;
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
