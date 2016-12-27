// Created: 29.11.2016
package de.freese.pim.gui.controller;

import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 * Interface eines Controllers.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public interface IController extends Initializable
{
    /**
     * Liefert den {@link Node} der Haupt-Ansicht.
     *
     * @return {@link Node}
     */
    public Node getMainNode();

    /**
     * Liefert den {@link Node} der Navigations-Ansicht.
     *
     * @return {@link Node}
     */
    public Node getNaviNode();
}
