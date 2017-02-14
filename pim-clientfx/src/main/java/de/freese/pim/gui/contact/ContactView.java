// Created: 13.12.2016
package de.freese.pim.gui.contact;

import org.apache.commons.lang3.StringUtils;

import de.freese.pim.gui.view.IView;
import de.freese.pim.server.addressbook.model.Kontakt;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * View des Addressbuchs.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class ContactView implements IView
{
    /**
    *
    */
    @FXML
    private Button buttonAddContact = null;

    /**
    *
    */
    @FXML
    private Button buttonDeleteContact = null;

    /**
    *
    */
    @FXML
    private Button buttonEditContact = null;

    /**
     *
     */
    @FXML
    private Label labelFilter = null;

    /**
     *
     */
    @FXML
    private Label labelNachname = null;

    /**
     *
     */
    @FXML
    private Label labelVorname = null;

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
    private TableView<Kontakt> tableViewKontakt = null;

    /**
     *
     */
    @FXML
    private TextField textFieldNachname = null;

    /**
     *
     */
    @FXML
    private TextField textFieldVorname = null;

    /**
     * Erzeugt eine neue Instanz von {@link ContactView}
     */
    public ContactView()
    {
        super();

        this.mainNode = createMainNode();
        this.naviNode = createNaviNode();
    }

    /**
     * @return {@link Region}
     */
    private Region createMainNode()
    {
        // return new Label("Contacts");
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridpane");

        // Nachname
        this.labelNachname = new Label("%nachname");
        gridPane.add(this.labelNachname, 0, 0);
        GridPane.setHalignment(this.labelNachname, HPos.RIGHT);

        this.textFieldNachname = new TextField();
        this.textFieldNachname.setEditable(false);
        GridPane.setHgrow(this.textFieldNachname, Priority.ALWAYS);
        gridPane.add(this.textFieldNachname, 1, 0);

        // Vorname
        this.labelVorname = new Label("%vorname");
        gridPane.add(this.labelVorname, 0, 1);
        GridPane.setHalignment(this.labelVorname, HPos.RIGHT);

        this.textFieldVorname = new TextField();
        this.textFieldVorname.setEditable(false);
        gridPane.add(this.textFieldVorname, 1, 1);

        TitledPane titledPane = new TitledPane("%details", gridPane);
        titledPane.setCollapsible(false);
        titledPane.setPrefHeight(Double.MAX_VALUE);
        titledPane.setContent(gridPane);

        return titledPane;
    }

    /**
     * @return {@link Region}
     */
    private Region createNaviNode()
    {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");

        // Toolbar
        ToolBar toolBar = new ToolBar();
        gridPane.add(toolBar, 0, 0, 2, 1);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-add");
        this.buttonAddContact = new Button();
        this.buttonAddContact.setGraphic(imageView);
        this.buttonAddContact.setTooltip(new Tooltip("%contact.add"));
        toolBar.getItems().add(this.buttonAddContact);

        imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-edit");
        this.buttonEditContact = new Button();
        this.buttonEditContact.setGraphic(imageView);
        this.buttonEditContact.setTooltip(new Tooltip("%contact.edit"));
        toolBar.getItems().add(this.buttonEditContact);

        imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-delete");
        this.buttonDeleteContact = new Button();
        this.buttonDeleteContact.setGraphic(imageView);
        this.buttonDeleteContact.setTooltip(new Tooltip("%contact.delete"));
        toolBar.getItems().add(this.buttonDeleteContact);

        // FilterLabel
        this.labelFilter = new Label("%filter");
        gridPane.add(this.labelFilter, 0, 1);

        // FilterTextField
        TextField textField = new TextField();
        gridPane.add(textField, 1, 1);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        // Tabelle
        this.tableViewKontakt = createTableViewKontakt(textField.textProperty());
        gridPane.add(this.tableViewKontakt, 0, 2, 2, 1);
        GridPane.setVgrow(this.tableViewKontakt, Priority.ALWAYS);

        TitledPane titledPane = new TitledPane("%contacts", gridPane);
        titledPane.setCollapsible(false);
        // titledPane.setPrefHeight(Double.MAX_VALUE);
        // titledPane.setContent(gridPane);
        // titledPane.setPrefWidth(200);

        return titledPane;
    }

    /**
     * @param propertyKontaktFilter {@link StringProperty}
     * @return {@link TableView}
     */
    private TableView<Kontakt> createTableViewKontakt(final StringProperty propertyKontaktFilter)
    {
        TableView<Kontakt> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setTableMenuButtonVisible(true);
        // tableView.setColumnResizePolicy(param -> true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        this.buttonEditContact.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonDeleteContact.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        // tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Kontakt, Number> columnID = new TableColumn<>("%id");
        TableColumn<Kontakt, String> columnNachname = new TableColumn<>("%nachname");
        TableColumn<Kontakt, String> columnVorname = new TableColumn<>("%vorname");

        columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1D)); // 10% Breite

        columnID.setResizable(false);
        columnID.setCellValueFactory(cell -> cell.getValue().idProperty());
        columnID.setStyle("-fx-alignment: center-right;");

        columnNachname.setCellValueFactory(cell -> cell.getValue().nachnameProperty());

        columnVorname.setCellValueFactory(cell -> cell.getValue().vornameProperty());

        // tableView.getColumns().add(columnID);
        tableView.getColumns().add(columnNachname);
        tableView.getColumns().add(columnVorname);

        // Für Filter
        FilteredList<Kontakt> filteredData = new FilteredList<>(FXCollections.observableArrayList());

        // Filter-Textfeld mit FilteredList verbinden.
        propertyKontaktFilter.addListener((observable, oldValue, newValue) ->
        {
            filteredData.setPredicate(kontakt ->
            {
                if (StringUtils.isBlank(newValue))
                {
                    return true;
                }

                String text = kontakt.getNachname() + " " + kontakt.getVorname();

                if (StringUtils.containsIgnoreCase(text, newValue))
                {
                    return true;
                }

                return false;
            });
        });

        // Da die ObservableList der TableItems neu gesetzt wird, muss auch die Sortierung neu gemacht werden.
        SortedList<Kontakt> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);

        // ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        tableView.setContextMenu(contextMenu);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-add");
        MenuItem menuItemAddContact = new MenuItem("%contact.add", imageView);
        // menuItemAddContact.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        menuItemAddContact.setOnAction(event -> this.buttonAddContact.fire());
        contextMenu.getItems().add(menuItemAddContact);

        imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-edit");
        MenuItem menuItemEditContact = new MenuItem("%contact.edit", imageView);
        menuItemAddContact.disableProperty().bind(this.buttonAddContact.disableProperty());
        menuItemEditContact.setOnAction(event -> this.buttonEditContact.fire());
        contextMenu.getItems().add(menuItemEditContact);

        imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-delete");
        MenuItem menuItemDeleteContact = new MenuItem("%contact.delete", imageView);
        menuItemDeleteContact.disableProperty().bind(this.buttonDeleteContact.disableProperty());
        menuItemDeleteContact.setOnAction(event -> this.buttonDeleteContact.fire());
        contextMenu.getItems().add(menuItemDeleteContact);

        // menuItemExcelExport.setOnAction(event -> excelExport());
        // menuItemExcelExport.disableProperty().bind(Bindings.when(Bindings.isEmpty(this.tableView.getItems())).then(true).otherwise(false));
        // contextMenu.getItems().add(menuItemExcelExport);

        return tableView;
    }
}
