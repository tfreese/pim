// Created: 13.12.2016
package de.freese.pim.gui.mail.view;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.view.View;

/**
 * View des Mail-Clients.
 *
 * @author Thomas Freese
 */
public class MailView implements View {
    @FXML
    private final Region mainNode;
    @FXML
    private final Region naviNode;
    @FXML
    private final ToolBar toolBar;
    // @FXML
    // private HTMLEditor editor = null;
    @FXML
    private Button buttonAddAccount;
    @FXML
    private Button buttonEditAccount;
    @FXML
    private MailContentView mailContentView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TableView<FxMail> tableViewMail;
    @FXML
    private TreeView<Object> treeViewMail;

    public MailView() {
        super();

        toolBar = createToolbar();
        mainNode = createMainNode();
        naviNode = createNaviNode();
    }

    private Region createMainNode() {
        // BorderPane borderPane = new BorderPane();
        // borderPane.setCenter(new Label("Mails"));
        //
        // return borderPane;
        tableViewMail = new TableView<>();
        tableViewMail.setEditable(false);
        tableViewMail.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // tableViewMail.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        // tableViewMail.setManaged(false);

        // ScrollPane scrollPane = new ScrollPane();
        // // scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        // scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // // scrollPane.prefWidthProperty().bind(tableViewMail.widthProperty());
        // // scrollPane.prefHeightProperty().bind(tableViewMail.heightProperty());
        // scrollPane.setFitToHeight(true);
        // scrollPane.setFitToWidth(true);
        // scrollPane.setContent(tableViewMail);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setId("progressIndicator"); // Für lookupAll("#progressIndicator")
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(250D, 250D);

        final SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().add(tableViewMail);

        mailContentView = new MailContentView();
        // mailContentView.setMaxWidth(Double.MAX_VALUE);
        splitPane.getItems().add(mailContentView);

        // editor = new HTMLEditor();
        // splitPane.getItems().add(editor);

        final StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(0));
        // StackPane.setMargin(tableView, new Insets(0));
        stackPane.getChildren().add(splitPane);
        stackPane.getChildren().add(progressIndicator);

        return stackPane;
    }

    private Region createNaviNode() {
        treeViewMail = new TreeView<>();
        treeViewMail.setEditable(false);
        treeViewMail.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final TitledPane titledPane = new TitledPane("%mails", treeViewMail);
        titledPane.setCollapsible(false);
        // titledPane.setPrefHeight(Double.MAX_VALUE);
        // titledPane.setContent(treeViewMail);

        // titledPane.setPrefWidth(200);

        return titledPane;
    }

    private ToolBar createToolbar() {
        final ToolBar tb = new ToolBar();

        // Image image = new Image("images/mail.png", 16, 16, true, true);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-add");
        buttonAddAccount = new Button();
        // buttonAddAccount.setPadding(new Insets(0));
        // buttonAddAccount.setPrefSize(imageViewMail.getFitWidth(), imageViewMail.getFitHeight());
        buttonAddAccount.setGraphic(imageView);
        buttonAddAccount.setTooltip(new Tooltip("%mailaccount.add"));

        imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-edit");
        buttonEditAccount = new Button();
        buttonEditAccount.setGraphic(imageView);
        buttonEditAccount.setTooltip(new Tooltip("%mailaccount.edit"));

        tb.getItems().add(buttonAddAccount);
        tb.getItems().add(buttonEditAccount);

        return tb;
    }
}
