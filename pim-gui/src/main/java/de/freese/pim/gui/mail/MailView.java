// Created: 13.12.2016
package de.freese.pim.gui.mail;

import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.gui.view.IView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

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
    private ProgressIndicator progressIndicator = null;

    /**
    *
    */
    @FXML
    private TableView<Mail> tableViewMail = null;

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
        // BorderPane borderPane = new BorderPane();
        // borderPane.setCenter(new Label("Mails"));
        //
        // return borderPane;
        this.tableViewMail = new TableView<>();
        this.tableViewMail.setEditable(false);
        this.tableViewMail.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // this.tableViewMail.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        this.progressIndicator = new ProgressIndicator();
        this.progressIndicator.setId("progressIndicator"); // Für lookupAll("#progressIndicator")
        this.progressIndicator.setVisible(false);
        this.progressIndicator.setMaxSize(250D, 250D);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(0));
        // StackPane.setMargin(this.tableView, new Insets(0));
        stackPane.getChildren().add(this.tableViewMail);
        stackPane.getChildren().add(this.progressIndicator);

        return stackPane;
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
