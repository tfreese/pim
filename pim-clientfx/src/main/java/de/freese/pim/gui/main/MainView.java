// Created: 29.11.2016
package de.freese.pim.gui.main;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.freese.pim.gui.view.View;

/**
 * Main View.
 *
 * @author Thomas Freese
 */
public class MainView extends BorderPane implements View {
    public static final TextArea LOG_TEXT_AREA = new TextArea();
    @FXML
    private final Button buttonContactView;
    @FXML
    private final Button buttonMailView;
    private final SplitPane splitPane;
    private final VBox vBox;

    public MainView() {
        super();

        this.splitPane = new SplitPane();
        this.splitPane.setOrientation(Orientation.HORIZONTAL);
        this.splitPane.setDividerPositions(0.2D);
        setCenter(this.splitPane);

        this.vBox = new VBox();
        this.vBox.getStyleClass().add("vbox");

        // Platzhalter für Navigation.
        this.vBox.getChildren().add(new Pane());
        setNavNode(new ListView<>());

        // Image image = new Image("images/mail.png", 16, 16, true, true);
        final ImageView imageViewMail = new ImageView();
        imageViewMail.setFitHeight(32);
        imageViewMail.setFitWidth(32);
        imageViewMail.getStyleClass().add("imageview-mail");

        this.buttonMailView = new Button("%mails");
        // this.buttonMailView.setPadding(new Insets(0));
        // this.buttonMailView.setPrefSize(imageViewMail.getFitWidth(), imageViewMail.getFitHeight());
        this.buttonMailView.setGraphic(imageViewMail);
        this.buttonMailView.setTooltip(new Tooltip("%mails"));

        final ImageView imageViewContact = new ImageView();
        imageViewContact.setFitHeight(32);
        imageViewContact.setFitWidth(32);
        imageViewContact.getStyleClass().add("imageview-contact");

        this.buttonContactView = new Button("%contacts");
        // this.buttonContactView.setPrefSize(imageViewContact.getFitWidth(), imageViewContact.getFitHeight());
        this.buttonContactView.setGraphic(imageViewContact);
        this.buttonContactView.setTooltip(new Tooltip("%contacts"));

        final HBox hBox = new HBox();
        hBox.getStyleClass().add("hbox");
        hBox.getChildren().addAll(this.buttonMailView, this.buttonContactView);

        VBox.setVgrow(hBox, Priority.NEVER);
        this.vBox.getChildren().add(hBox);

        this.splitPane.getItems().addAll(this.vBox, new Pane());

        // Logs
        // TitledPane titledPane = new TitledPane();
        // titledPane.setText("Logs");
        // titledPane.setExpanded(true);
        // titledPane.setCollapsible(true);
        // titledPane.setMaxHeight(200);
        //
        // // titledPane.heightProperty().addListener((observable, oldValue, newValue) -> PIMApplication.getMainWindow().sizeToScene());
        //
        // titledPane.expandedProperty().addListener((obs, oldValue, newValue) ->
        // {
        // if (newValue)
        // {
        // titledPane.setMaxHeight(200);
        // }
        // else
        // {
        // titledPane.setMaxHeight(0);
        // }
        // });
        //
        // titledPane.setStyle("-fx-font-size: 75%;");
        // LOG_TEXT_AREA.setStyle("-fx-font-size: 75%;");
        // titledPane.setContent(LOG_TEXT_AREA);
        //
        // setBottom(titledPane);
    }

    public void setMainNode(final Node node) {
        this.splitPane.getItems().set(1, node);
        // this.splitPane.layout();

        // requestLayout();
        // layout();
    }

    public void setNavNode(final Node node) {
        // VBox.setVgrow(node, Priority.ALWAYS);
        this.vBox.getChildren().set(0, node);
        // this.vBox.layout();

        // requestLayout();
        // layout();
    }

    public void setToolbar(final ToolBar toolBar) {
        setTop(toolBar);
    }
}
