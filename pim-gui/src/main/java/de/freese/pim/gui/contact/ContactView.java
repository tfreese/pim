// Created: 13.12.2016
package de.freese.pim.gui.contact;

import org.apache.commons.lang3.StringUtils;
import de.freese.pim.core.addressbook.model.Kontakt;
import de.freese.pim.gui.view.IView;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Button buttonDelete = null;

    /**
    *
    */
    @FXML
    private Button buttonEdit = null;

    /**
    *
    */
    @FXML
    private Button buttonNew = null;

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
     * @return {@link Node}
     */
    private Node createMainNode()
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
        titledPane.setPrefHeight(Double.MAX_VALUE);
        titledPane.setContent(gridPane);

        return titledPane;
    }

    /**
     * @return {@link Node}
     */
    private Node createNaviNode()
    {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");

        // Toolbar
        ToolBar toolBar = new ToolBar();
        gridPane.add(toolBar, 0, 0, 2, 1);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-new");
        this.buttonNew = new Button("%neu", imageView);
        this.buttonNew.setTooltip(new Tooltip("%kontakt.neu"));
        toolBar.getItems().add(this.buttonNew);

        imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-edit");
        this.buttonEdit = new Button("%aendern", imageView);
        this.buttonEdit.setTooltip(new Tooltip("%kontakt.aendern"));
        toolBar.getItems().add(this.buttonEdit);

        imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.getStyleClass().add("imageview-delete");
        this.buttonDelete = new Button("%loeschen", imageView);
        this.buttonDelete.setTooltip(new Tooltip("%kontakt.loeschen"));
        toolBar.getItems().add(this.buttonDelete);

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

        TitledPane titledPane = new TitledPane("%kontakte", gridPane);
        titledPane.setPrefHeight(Double.MAX_VALUE);
        // titledPane.setContent(gridPane);

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

        this.buttonEdit.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonDelete.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        // tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Kontakt, Number> columnID = new TableColumn<>("%id");
        columnID.setResizable(false);
        columnID.setCellValueFactory(cell -> cell.getValue().idProperty()); // Für reine FX-Bean
        // columnID.setCellValueFactory(new PropertyValueFactory<>("ID")); // Updates erfolgen nur, wenn Bean PropertyChangeSupport hat
        columnID.setStyle("-fx-alignment: center-right;");
        columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1D)); // 10% Breite
        // columnID.setMaxWidth(50);

        TableColumn<Kontakt, String> columnNachname = new TableColumn<>("%nachname");
        columnNachname.setCellValueFactory(cell -> cell.getValue().nachnameProperty());
        // columnNachname.prefWidthProperty().bind(tableView.widthProperty().multiply(0.45D).add(-1.5D)); // 45% Breite
        // columnNachname.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<Kontakt, String> columnVorname = new TableColumn<>("%vorname");
        columnVorname.setCellValueFactory(cell -> cell.getValue().vornameProperty());
        // columnVorname.prefWidthProperty().bind(tableView.widthProperty().multiply(0.45D).add(-1.5D)); // 45% Breite

        // Aller verfügbarer Platz für Vorname-Spalte, Rest hat feste Breite.
        // columnVorname.prefWidthProperty().bind(tableView.widthProperty().subtract(columnID.getMaxWidth() +
        // columnNachname.getPrefWidth()));

        // tableView.getColumns().add(columnID);
        tableView.getColumns().add(columnNachname);
        tableView.getColumns().add(columnVorname);

        // Für Filter
        FilteredList<Kontakt> filteredData = new FilteredList<>(FXCollections.observableArrayList(), p -> true);

        // Filter-Textfeld mit FilteredList verbinden.
        propertyKontaktFilter.addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(kontakt -> {
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

        return tableView;
    }
}
