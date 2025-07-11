// Created: 13.12.2016
package de.freese.pim.gui.addressbook;

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

import de.freese.pim.gui.addressbook.model.FxKontakt;
import de.freese.pim.gui.view.View;

/**
 * View des Adressbuchs.
 *
 * @author Thomas Freese
 */
public class ContactView implements View {
    @FXML
    private final Region mainNode;
    @FXML
    private final Region naviNode;

    @FXML
    private Button buttonAddContact;
    @FXML
    private Button buttonDeleteContact;
    @FXML
    private Button buttonEditContact;
    @FXML
    private Label labelFilter;
    @FXML
    private Label labelNachname;
    @FXML
    private Label labelVorname;
    @FXML
    private TableView<FxKontakt> tableViewKontakt;
    @FXML
    private TextField textFieldNachname;
    @FXML
    private TextField textFieldVorname;

    public ContactView() {
        super();

        mainNode = createMainNode();
        naviNode = createNaviNode();
    }

    private Region createMainNode() {
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridpane");

        // Nachname
        labelNachname = new Label("%nachname");
        gridPane.add(labelNachname, 0, 0);
        GridPane.setHalignment(labelNachname, HPos.RIGHT);

        textFieldNachname = new TextField();
        textFieldNachname.setEditable(false);
        GridPane.setHgrow(textFieldNachname, Priority.ALWAYS);
        gridPane.add(textFieldNachname, 1, 0);

        // Vorname
        labelVorname = new Label("%vorname");
        gridPane.add(labelVorname, 0, 1);
        GridPane.setHalignment(labelVorname, HPos.RIGHT);

        textFieldVorname = new TextField();
        textFieldVorname.setEditable(false);
        gridPane.add(textFieldVorname, 1, 1);

        final TitledPane titledPane = new TitledPane("%details", gridPane);
        titledPane.setCollapsible(false);
        titledPane.setPrefHeight(Double.MAX_VALUE);
        titledPane.setContent(gridPane);

        return titledPane;
    }

    private Region createNaviNode() {
        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");

        // Toolbar
        final ToolBar toolBar = new ToolBar();
        gridPane.add(toolBar, 0, 0, 2, 1);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-add");
        buttonAddContact = new Button();
        buttonAddContact.setGraphic(imageView);
        buttonAddContact.setTooltip(new Tooltip("%contact.add"));
        toolBar.getItems().add(buttonAddContact);

        imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-edit");
        buttonEditContact = new Button();
        buttonEditContact.setGraphic(imageView);
        buttonEditContact.setTooltip(new Tooltip("%contact.edit"));
        toolBar.getItems().add(buttonEditContact);

        imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add("imageview-delete");
        buttonDeleteContact = new Button();
        buttonDeleteContact.setGraphic(imageView);
        buttonDeleteContact.setTooltip(new Tooltip("%contact.delete"));
        toolBar.getItems().add(buttonDeleteContact);

        // FilterLabel
        labelFilter = new Label("%filter");
        gridPane.add(labelFilter, 0, 1);

        // FilterTextField
        final TextField textField = new TextField();
        gridPane.add(textField, 1, 1);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        // Tabelle
        tableViewKontakt = createTableViewKontakt(textField.textProperty());
        gridPane.add(tableViewKontakt, 0, 2, 2, 1);
        GridPane.setVgrow(tableViewKontakt, Priority.ALWAYS);

        final TitledPane titledPane = new TitledPane("%contacts", gridPane);
        titledPane.setCollapsible(false);
        // titledPane.setPrefHeight(Double.MAX_VALUE);
        // titledPane.setContent(gridPane);
        // titledPane.setPrefWidth(200);

        return titledPane;
    }

    private TableView<FxKontakt> createTableViewKontakt(final StringProperty propertyKontaktFilter) {
        final TableView<FxKontakt> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setTableMenuButtonVisible(true);
        // tableView.setColumnResizePolicy(param -> true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        buttonEditContact.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        buttonDeleteContact.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        // tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final TableColumn<FxKontakt, Number> columnID = new TableColumn<>("%id");
        final TableColumn<FxKontakt, String> columnNachname = new TableColumn<>("%nachname");
        final TableColumn<FxKontakt, String> columnVorname = new TableColumn<>("%vorname");

        columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1D)); // 10 % Breite

        columnID.setResizable(false);
        columnID.setCellValueFactory(cell -> cell.getValue().idProperty());
        columnID.setStyle("-fx-alignment: center-right;");

        columnNachname.setCellValueFactory(cell -> cell.getValue().nachnameProperty());

        columnVorname.setCellValueFactory(cell -> cell.getValue().vornameProperty());

        // tableView.getColumns().add(columnID);
        tableView.getColumns().add(columnNachname);
        tableView.getColumns().add(columnVorname);

        // Für Filter
        final FilteredList<FxKontakt> filteredData = new FilteredList<>(FXCollections.observableArrayList());

        // Filter-Textfeld mit FilteredList verbinden.
        propertyKontaktFilter.addListener((observable, oldValue, newValue) -> filteredData.setPredicate(kontakt -> {
            if (newValue == null || newValue.isBlank()) {
                return true;
            }

            final String text = kontakt.getNachname() + " " + kontakt.getVorname();

            return text.equalsIgnoreCase(newValue);
        }));

        // Da die ObservableList der TableItems neu gesetzt wird, muss auch die Sortierung neu gemacht werden.
        final SortedList<FxKontakt> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);

        // ContextMenu
        final ContextMenu contextMenu = new ContextMenu();
        tableView.setContextMenu(contextMenu);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-add");

        final MenuItem menuItemAddContact = new MenuItem("%contact.add", imageView);
        // menuItemAddContact.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        menuItemAddContact.setOnAction(event -> buttonAddContact.fire());
        contextMenu.getItems().add(menuItemAddContact);

        imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-edit");

        final MenuItem menuItemEditContact = new MenuItem("%contact.edit", imageView);
        menuItemAddContact.disableProperty().bind(buttonAddContact.disableProperty());
        menuItemEditContact.setOnAction(event -> buttonEditContact.fire());
        contextMenu.getItems().add(menuItemEditContact);

        imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-delete");

        final MenuItem menuItemDeleteContact = new MenuItem("%contact.delete", imageView);
        menuItemDeleteContact.disableProperty().bind(buttonDeleteContact.disableProperty());
        menuItemDeleteContact.setOnAction(event -> buttonDeleteContact.fire());
        contextMenu.getItems().add(menuItemDeleteContact);

        // menuItemExcelExport.setOnAction(event -> excelExport());
        // menuItemExcelExport.disableProperty().bind(Bindings.when(Bindings.isEmpty(tableView.getItems())).then(true).otherwise(false));
        // contextMenu.getItems().add(menuItemExcelExport);

        return tableView;
    }
}
