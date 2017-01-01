// Created: 13.12.2016
package de.freese.pim.gui.mail;

import de.freese.pim.gui.view.IView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 * View des Mail-Clients.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class MailView implements IView
{
    /**
     *
     */
    @FXML
    private final Node mainNode;

    /**
     *
     */
    @FXML
    private final Node naviNode;

    /**
    *
    */
    @FXML
    private TreeView<Object> treeViewMail = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailView}
     */
    public MailView()
    {
        super();

        this.mainNode = createMainNode();
        this.naviNode = createNaviNode();
    }

    /**
     * @return {@link Node}
     */
    private Node createMainNode()
    {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(new Label("Mails"));

        return borderPane;
    }

    /**
     * @return {@link Node}
     */
    private Node createNaviNode()
    {
        this.treeViewMail = new TreeView<>();
        this.treeViewMail.setEditable(false);
        this.treeViewMail.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TitledPane titledPane = new TitledPane("%mails", this.treeViewMail);
        titledPane.setPrefHeight(Double.MAX_VALUE);
        // titledPane.setContent(treeViewMail);

        return titledPane;
    }
}
