// Created: 29.11.2016
package de.freese.pim.gui.controller;

import de.freese.pim.gui.main.MainController;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

/**
 * Interface eines Controllers.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public interface IController extends Initializable
{
    /**
     * Wird vom {@link MainController} aufgerufen, wenn der Controller aktiviert wird.
     */
    public void activate();

    /**
     * Liefert den {@link Node} der Ansicht.
     *
     * @return {@link Node}
     */
    public Node getMainNode();

    /**
     * Liefert den {@link Node} der Ansicht.
     *
     * @return {@link Node}
     */
    public Node getNaviNode();

    /**
     * Liefert die {@link ToolBar} der Ansicht.
     *
     * @return {@link ToolBar}
     */
    public ToolBar getToolBar();
}
