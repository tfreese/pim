// Created: 13.12.2016
package de.freese.pim.gui.mail;

import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.view.View;
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
import javafx.scene.web.WebView;

/**
 * View des Mail-Clients.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class MailView implements View
{
    /**
     *
     */
    @FXML
    private Button buttonAddAccount = null;

    /**
    *
    */
    @FXML
    private Button buttonEditAccount = null;

    // /**
    // *
    // */
    // @FXML
    // private HTMLEditor editor = null;

    /**
     *
     */
    @FXML
    private final Region mainNode;

    /**
     *
     */
    @FXML
    private final Region naviNode;

    /**
    *
    */
    @FXML
    private ProgressIndicator progressIndicator = null;

    /**
    *
    */
    @FXML
    private TableView<FXMail> tableViewMail = null;

    /**
     *
     */
    @FXML
    private ToolBar toolBar = null;

    /**
    *
    */
    @FXML
    private TreeView<Object> treeViewMail = null;

    /**
    *
    */
    @FXML
    private WebView webView;

    /**
     * Erzeugt eine neue Instanz von {@link MailView}
     */
    public MailView()
    {
        super();

        this.toolBar = createToolbar();
        this.mainNode = createMainNode();
        this.naviNode = createNaviNode();
    }

    /**
     * @return {@link Region}
     */
    private Region createMainNode()
    {
        // BorderPane borderPane = new BorderPane();
        // borderPane.setCenter(new Label("Mails"));
        //
        // return borderPane;
        this.tableViewMail = new TableView<>();
        this.tableViewMail.setEditable(false);
        this.tableViewMail.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // this.tableViewMail.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        // this.tableViewMail.setManaged(false);

        // ScrollPane scrollPane = new ScrollPane();
        // // scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        // scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // // scrollPane.prefWidthProperty().bind(this.tableViewMail.widthProperty());
        // // scrollPane.prefHeightProperty().bind(this.tableViewMail.heightProperty());
        // scrollPane.setFitToHeight(true);
        // scrollPane.setFitToWidth(true);
        // scrollPane.setContent(this.tableViewMail);

        this.progressIndicator = new ProgressIndicator();
        this.progressIndicator.setId("progressIndicator"); // Für lookupAll("#progressIndicator")
        this.progressIndicator.setVisible(false);
        this.progressIndicator.setMaxSize(250D, 250D);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().add(this.tableViewMail);

        this.webView = new WebView();
        splitPane.getItems().add(this.webView);

        // this.editor = new HTMLEditor();
        // splitPane.getItems().add(this.editor);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(0));
        // StackPane.setMargin(this.tableView, new Insets(0));
        stackPane.getChildren().add(splitPane);
        stackPane.getChildren().add(this.progressIndicator);

        return stackPane;
    }

    /**
     * @return {@link Region}
     */
    private Region createNaviNode()
    {
        this.treeViewMail = new TreeView<>();
        this.treeViewMail.setEditable(false);
        this.treeViewMail.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TitledPane titledPane = new TitledPane("%mails", this.treeViewMail);
        titledPane.setCollapsible(false);
        // titledPane.setPrefHeight(Double.MAX_VALUE);
        // titledPane.setContent(treeViewMail);

        // titledPane.setPrefWidth(200);

        return titledPane;
    }

    /**
     * @return {@link ToolBar}
     */
    private ToolBar createToolbar()
    {
        ToolBar toolBar = new ToolBar();

        // Image image = new Image("images/mail.png", 16, 16, true, true);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-add");
        this.buttonAddAccount = new Button();
        // this.buttonAddAccount.setPadding(new Insets(0));
        // this.buttonAddAccount.setPrefSize(imageViewMail.getFitWidth(), imageViewMail.getFitHeight());
        this.buttonAddAccount.setGraphic(imageView);
        this.buttonAddAccount.setTooltip(new Tooltip("%mailaccount.add"));

        imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-edit");
        this.buttonEditAccount = new Button();
        this.buttonEditAccount.setGraphic(imageView);
        this.buttonEditAccount.setTooltip(new Tooltip("%mailaccount.edit"));

        toolBar.getItems().add(this.buttonAddAccount);
        toolBar.getItems().add(this.buttonEditAccount);

        return toolBar;
    }
}
