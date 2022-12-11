// Created: 29.11.2016
package de.freese.pim.gui.controller;

import de.freese.pim.core.spring.SpringContext;
import de.freese.pim.gui.PimClientApplication;
import de.freese.pim.gui.main.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;

/**
 * Abstract Implementierung eines Controllers.
 *
 * @author Thomas Freese
 */
public abstract class AbstractController implements Initializable
{
    public final Logger logger = LoggerFactory.getLogger(getClass());
    private final AsyncTaskExecutor taskExecutor;
    private final TaskScheduler taskScheduler;
    private boolean activated;

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

    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert den {@link Node} der Ansicht.
     */
    public Node getMainNode()
    {
        return null;
    }

    public Window getMainWindow()
    {
        return PimClientApplication.getMainWindow();
    }

    /**
     * Liefert den {@link Node} der Ansicht.
     */
    public Node getNaviNode()
    {
        return null;
    }

    public AsyncTaskExecutor getTaskExecutor()
    {
        return this.taskExecutor;
    }

    public TaskScheduler getTaskScheduler()
    {
        return this.taskScheduler;
    }

    /**
     * Liefert die {@link ToolBar} der Ansicht.
     */
    public ToolBar getToolBar()
    {
        return null;
    }

    /**
     * Liefert true, wenn der Controller bereits aktiviert wurde.
     */
    protected boolean isActivated()
    {
        return this.activated;
    }

    /**
     * Setzt true, wenn der Controller bereits aktiviert wurde.
     */
    protected void setActivated(final boolean activated)
    {
        this.activated = activated;
    }
}
