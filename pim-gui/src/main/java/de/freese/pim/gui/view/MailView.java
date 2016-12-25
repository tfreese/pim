// Created: 13.12.2016
package de.freese.pim.gui.view;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

/**
 * View des Mail-Clients.
 *
 * @author Thomas Freese (EFREEST / AuVi)
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
        return new ListView<>();
    }
}
