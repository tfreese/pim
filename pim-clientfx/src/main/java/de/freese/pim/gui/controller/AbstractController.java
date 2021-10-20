// Created: 29.11.2016
package de.freese.pim.gui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import de.freese.pim.common.spring.SpringContext;
import de.freese.pim.gui.PimClientApplication;
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
public abstract class AbstractController implements Initializable
{
    /**
    *
    */
    private boolean activated;
    /**
    *
    */
    public final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private final AsyncTaskExecutor taskExecutor;
    /**
     *
     */
    private final TaskScheduler taskScheduler;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractController}
     */
    protected AbstractController()
    {
        super();

        this.taskExecutor = SpringContext.getAsyncTaskExecutor();
        this.taskScheduler = SpringContext.getTaskScheduler();
    }

    /**
     * Wird vom {@link MainController} aufgerufen, wenn der Controller aktiviert wird.
     */
    public abstract void activate();

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
        return PimClientApplication.getMainWindow();
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
     * @return {@link AsyncTaskExecutor}
     */
    public AsyncTaskExecutor getTaskExecutor()
    {
        return this.taskExecutor;
    }

    /**
     * @return {@link TaskScheduler}
     */
    public TaskScheduler getTaskScheduler()
    {
        return this.taskScheduler;
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
