// Created: 13.12.2016
package de.freese.pim.gui.addressbook;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.addressbook.model.FXKontakt;
import de.freese.pim.gui.addressbook.service.FXAddressbookService;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.utils.FXUtils;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller des Addressbuchs.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class ContactController extends AbstractController
{
    /**
    *
    */
    private final FXAddressbookService addressbookService;

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
    private Node mainNode = null;

    /**
    *
    */
    @FXML
    private Node naviNode = null;

    /**
    *
    */
    private final ObjectProperty<FXKontakt> selectedKontakt = new SimpleObjectProperty<>();

    /**
    *
    */
    @FXML
    private TableView<FXKontakt> tableViewKontakt = null;

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
     * Erzeugt eine neue Instanz von {@link ContactController}
     */
    public ContactController()
    {
        super();

        this.addressbookService = PIMApplication.getApplicationContext().getBean("clientAddressBookService", FXAddressbookService.class);
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#activate()
     */
    @Override
    public void activate()
    {
        if (isActivated())
        {
            return;
        }

        setActivated(true);

        // Daten laden.
        loadKontakte();
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#getMainNode()
     */
    @Override
    public Node getMainNode()
    {
        return this.mainNode;
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#getNaviNode()
     */
    @Override
    public Node getNaviNode()
    {
        return this.naviNode;
    }

    /**
     * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources)
    {
        this.selectedKontakt.bind(this.tableViewKontakt.getSelectionModel().selectedItemProperty());

        // Workaround um Bind-Warnings "Exception while evaluating select-binding" zu verhindern.
        getKontakteList().add(new FXKontakt("", ""));
        this.tableViewKontakt.getSelectionModel().select(0);

        this.textFieldNachname.textProperty().bind(
                Bindings.when(this.selectedKontakt.isNull()).then("").otherwise(Bindings.selectString(this.selectedKontakt, "nachname")));
        this.textFieldVorname.textProperty().bind(
                Bindings.when(this.selectedKontakt.isNull()).then("").otherwise(Bindings.selectString(this.selectedKontakt, "vorname")));

        this.buttonAddContact.setOnAction(event ->
        {
            Dialog<Pair<String, String>> dialog = createAddEditKontaktDialog("contact.add", "contact.add", "imageview-add", null,
                    resources);

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(pair ->
            {
                try
                {
                    FXKontakt kontakt = new FXKontakt(pair.getLeft(), StringUtils.defaultIfBlank(pair.getRight(), null));

                    getAddressbookService().insertKontakt(kontakt);

                    getKontakteList().add(kontakt);

                    this.tableViewKontakt.getSelectionModel().select(kontakt);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        this.buttonEditContact.setOnAction(event ->
        {
            Dialog<Pair<String, String>> dialog = createAddEditKontaktDialog("contact.edit", "contact.edit", "imageview-edit",
                    this.selectedKontakt.getValue(), resources);

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(pair ->
            {
                try
                {
                    String nachname = pair.getLeft();
                    String vorname = StringUtils.defaultIfBlank(pair.getRight(), null);

                    getAddressbookService().updateKontakt(this.selectedKontakt.getValue().getID(), nachname, vorname);

                    this.selectedKontakt.getValue().setNachname(nachname);
                    this.selectedKontakt.getValue().setVorname(vorname);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        this.buttonDeleteContact.setOnAction(event ->
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add("styles/styles.css");
            alert.setTitle(resources.getString("contact.delete"));
            alert.setHeaderText(resources.getString("contact.delete"));
            alert.setContentText(resources.getString("contact.delete.wirklich"));
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            ImageView imageView = new ImageView();
            imageView.setFitHeight(32);
            imageView.setFitWidth(32);
            imageView.getStyleClass().add("imageview-delete");

            FXUtils.preloadImage(imageView);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(imageView.getImage());

            TextField nachname = new TextField(this.selectedKontakt.getValue().getNachname());
            nachname.setEditable(false);
            TextField vorname = new TextField(this.selectedKontakt.getValue().getVorname());
            vorname.setEditable(false);

            GridPane gridPane = new GridPane();
            gridPane.getStyleClass().addAll("gridpane", "padding");
            gridPane.add(new Label(resources.getString("nachname")), 0, 0);
            gridPane.add(nachname, 1, 0);
            gridPane.add(new Label(resources.getString("vorname")), 0, 1);
            gridPane.add(vorname, 1, 1);

            alert.getDialogPane().setContent(gridPane);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.YES)
            {
                try
                {
                    getAddressbookService().deleteKontakt(this.selectedKontakt.getValue().getID());
                    getKontakteList().remove(this.selectedKontakt.getValue());
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            }
        });
    }

    /**
     * <a href="http://code.makery.ch/blog/javafx-dialogs-official/">Dialog Tutorial</a>
     *
     * @param titleKey String
     * @param textKey String
     * @param imageStyleClass String
     * @param kontakt {@link FXKontakt}
     * @param resources {@link ResourceBundle}
     * @return {@link java.awt.Dialog}
     */
    private Dialog<Pair<String, String>> createAddEditKontaktDialog(final String titleKey, final String textKey,
            final String imageStyleClass, final FXKontakt kontakt, final ResourceBundle resources)
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.initOwner(getMainWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(resources.getString(titleKey));
        dialog.setHeaderText(resources.getString(textKey));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        imageView.getStyleClass().add(imageStyleClass);
        dialog.setGraphic(imageView);

        // Laden des Images antriggern für ImageView#getImage.
        imageView.applyCss();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().clear(); // Icons des Owners entfernen.
        stage.getIcons().add(imageView.getImage());

        // ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nachname = new TextField();
        nachname.setPromptText(resources.getString("nachname"));
        TextField vorname = new TextField();
        vorname.setPromptText(resources.getString("vorname"));

        if (kontakt != null)
        {
            nachname.setText(kontakt.getNachname());
            vorname.setText(kontakt.getVorname());
        }

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);

        if (StringUtils.isBlank(nachname.getText()))
        {
            okButton.setDisable(true);
        }

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");
        gridPane.add(new Label(resources.getString("nachname")), 0, 0);
        gridPane.add(nachname, 1, 0);
        gridPane.add(new Label(resources.getString("vorname")), 0, 1);
        gridPane.add(vorname, 1, 1);

        nachname.textProperty().addListener((observable, oldValue, newValue) ->
        {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> nachname.requestFocus());

        dialog.setResultConverter(buttonType ->
        {
            if (buttonType == ButtonType.OK)
            {
                return new MutablePair<>(nachname.getText(), vorname.getText());
            }

            return null;
        });

        return dialog;
    }

    /**
     * @return {@link FXAddressbookService}
     */
    private FXAddressbookService getAddressbookService()
    {
        return this.addressbookService;
    }

    /**
     * Als veränderbare Liste muss die Original-ObservableList zurückgegeben werden.
     *
     * @return {@link ObservableList}
     */
    @SuppressWarnings("unchecked")
    private ObservableList<FXKontakt> getKontakteList()
    {
        SortedList<FXKontakt> sortedList = (SortedList<FXKontakt>) this.tableViewKontakt.getItems();
        FilteredList<FXKontakt> filteredList = (FilteredList<FXKontakt>) sortedList.getSource();
        ObservableList<FXKontakt> dataList = (ObservableList<FXKontakt>) filteredList.getSource();

        return dataList;
    }

    /**
     * Kontakte laden
     */
    private void loadKontakte()
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Load Kontakte");
        }

        getKontakteList().clear();

        Task<List<FXKontakt>> task = new Task<List<FXKontakt>>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected List<FXKontakt> call() throws Exception
            {
                return getAddressbookService().getKontaktDetails();
            }
        };

        task.setOnSucceeded(event ->
        {
            getKontakteList().addAll(task.getValue());
        });

        task.setOnFailed(event ->
        {
            getLogger().error(null, task.getException());

            new ErrorDialog().forThrowable(task.getException()).showAndWait();
        });

        getTaskExecutor().execute(task);
    }
}
