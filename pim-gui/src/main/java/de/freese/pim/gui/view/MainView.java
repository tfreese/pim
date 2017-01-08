// Created: 29.11.2016
package de.freese.pim.gui.view;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Main View.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class MainView extends BorderPane implements IView
{
    /**
     *
     */
    @FXML
    private Button buttonContactView = null;

    /**
     *
     */
    @FXML
    private Button buttonMailView = null;

    /**
     *
     */
    private final SplitPane splitPane;

    /**
    *
    */
    @FXML
    private ToolBar toolBar = null;

    /**
     *
     */
    private VBox vBox = null;

    /**
     * Erzeugt eine neue Instanz von {@link MainView}
     */
    public MainView()
    {
        super();

        this.splitPane = new SplitPane();
        this.splitPane.setOrientation(Orientation.HORIZONTAL);
        this.splitPane.setDividerPositions(0.15D);
        setCenter(this.splitPane);

        this.toolBar = new ToolBar();
        setTop(this.toolBar);

        this.vBox = new VBox();
        this.vBox.getStyleClass().add("vbox");

        // Platzhalter für Navigation.
        this.vBox.getChildren().add(new Pane());
        setNavNode(new ListView<>());

        // Image image = new Image("images/mail.png", 16, 16, true, true);
        ImageView imageViewMail = new ImageView();
        imageViewMail.setFitHeight(32);
        imageViewMail.setFitWidth(32);
        imageViewMail.getStyleClass().add("imageview-mail");

        this.buttonMailView = new Button("%mails");
        // this.buttonMailView.setPadding(new Insets(0));
        // this.buttonMailView.setPrefSize(imageViewMail.getFitWidth(), imageViewMail.getFitHeight());
        this.buttonMailView.setGraphic(imageViewMail);
        this.buttonMailView.setTooltip(new Tooltip("%mails"));

        ImageView imageViewContact = new ImageView();
        imageViewContact.setFitHeight(32);
        imageViewContact.setFitWidth(32);
        imageViewContact.getStyleClass().add("imageview-contact");

        this.buttonContactView = new Button("%kontakte");
        // this.buttonContactView.setPrefSize(imageViewContact.getFitWidth(), imageViewContact.getFitHeight());
        this.buttonContactView.setGraphic(imageViewContact);
        this.buttonContactView.setTooltip(new Tooltip("%kontakte"));

        HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox");
        hBox.getChildren().addAll(this.buttonMailView, this.buttonContactView);

        VBox.setVgrow(hBox, Priority.NEVER);
        this.vBox.getChildren().add(hBox);

        this.splitPane.getItems().addAll(this.vBox, new Pane());
    }

    /**
     * Setzt den {@link Node} der Haupt-Ansicht.
     *
     * @param node {@link Node}
     */
    public void setMainNode(final Node node)
    {
        this.splitPane.getItems().set(1, node);
        // setCenter(node);
    }

    /**
     * Setzt den {@link Node} der Navigations-Ansicht.
     *
     * @param node {@link Pane}
     */
    public void setNavNode(final Node node)
    {
        VBox.setVgrow(node, Priority.ALWAYS);
        this.vBox.getChildren().set(0, node);
    }
}
