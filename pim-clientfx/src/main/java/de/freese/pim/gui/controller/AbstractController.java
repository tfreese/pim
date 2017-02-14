// Created: 29.11.2016
package de.freese.pim.gui.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.main.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.stage.Window;

/**
 * Abstract Implementierung eines Controllers.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public abstract class AbstractController implements Initializable
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
     * Wird vom {@link MainController} aufgerufen, wenn der Controller aktiviert wird.
     */
    public abstract void activate();

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
     * Liefert den {@link Node} der Ansicht.
     *
     * @return {@link Node}
     */
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
     * Liefert den {@link Node} der Ansicht.
     *
     * @return {@link Node}
     */
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
     * Liefert die {@link ToolBar} der Ansicht.
     *
     * @return {@link ToolBar}
     */
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
