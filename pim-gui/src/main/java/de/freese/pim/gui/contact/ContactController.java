// Created: 13.12.2016
package de.freese.pim.gui.contact;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import de.freese.pim.core.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.core.addressbook.model.Kontakt;
import de.freese.pim.core.addressbook.service.DefaultAddressBookService;
import de.freese.pim.core.addressbook.service.IAddressBookService;
import de.freese.pim.core.persistence.TransactionalInvocationHandler;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.utils.FXUtils;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
    private final SimpleObjectProperty<Kontakt> selectedKontakt = new SimpleObjectProperty<>();

    /**
    *
    */
    private final IAddressBookService service;

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
     * Erzeugt eine neue Instanz von {@link ContactController}
     */
    public ContactController()
    {
        super();

        this.service = (IAddressBookService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]
        {
                IAddressBookService.class
        }, new TransactionalInvocationHandler(getDataSource(), new DefaultAddressBookService(new DefaultAddressBookDAO().dataSource(getDataSource()))));
    }

    /**
     * @see de.freese.pim.gui.controller.IController#activate()
     */
    @Override
    public void activate()
    {
        // Wenn die Liste leer ist -> befüllen.
        if (getKontakteList().size() > 1)
        {
            return;
        }

        loadKontakte();
    }

    /**
     * <a href="http://code.makery.ch/blog/javafx-dialogs-official/">Dialog Tutorial</a>
     *
     * @param titleKey String
     * @param textKey String
     * @param imageStyleClass String
     * @param kontakt {@link Kontakt}
     * @param resources {@link ResourceBundle}
     * @return {@link java.awt.Dialog}
     */
    private Dialog<Pair<String, String>> createAddEditKontaktDialog(final String titleKey, final String textKey, final String imageStyleClass,
                                                                    final Kontakt kontakt, final ResourceBundle resources)
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

        nachname.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> nachname.requestFocus());

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK)
            {
                return new MutablePair<>(nachname.getText(), vorname.getText());
            }

            return null;
        });

        return dialog;
    }

    /**
     * Als veränderbare Liste muss die Original-ObservableList zurückgegeben werden.
     *
     * @return {@link ObservableList}
     */
    @SuppressWarnings("unchecked")
    private ObservableList<Kontakt> getKontakteList()
    {
        SortedList<Kontakt> sortedList = (SortedList<Kontakt>) this.tableViewKontakt.getItems();
        FilteredList<Kontakt> filteredList = (FilteredList<Kontakt>) sortedList.getSource();
        ObservableList<Kontakt> dataList = (ObservableList<Kontakt>) filteredList.getSource();

        return dataList;
    }

    /**
     * @see de.freese.pim.gui.controller.IController#getMainNode()
     */
    @Override
    public Node getMainNode()
    {
        return this.mainNode;
    }

    /**
     * @see de.freese.pim.gui.controller.IController#getNaviNode()
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
        getKontakteList().add(new Kontakt(-1, "", ""));
        this.tableViewKontakt.getSelectionModel().select(0);

        this.textFieldNachname.textProperty()
                .bind(Bindings.when(this.selectedKontakt.isNull()).then("").otherwise(Bindings.selectString(this.selectedKontakt, "nachname")));
        this.textFieldVorname.textProperty()
                .bind(Bindings.when(this.selectedKontakt.isNull()).then("").otherwise(Bindings.selectString(this.selectedKontakt, "vorname")));

        this.buttonAddContact.setOnAction(event -> {
            Dialog<Pair<String, String>> dialog = createAddEditKontaktDialog("contact.add", "contact.add", "imageview-add", null, resources);

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(pair -> {
                try
                {
                    long id = this.service.insertKontakt(pair.getLeft(), StringUtils.defaultIfBlank(pair.getRight(), null));

                    Kontakt kontakt = new Kontakt(id, pair.getLeft(), StringUtils.defaultIfBlank(pair.getRight(), null));
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

        this.buttonEditContact.setOnAction(event -> {
            Dialog<Pair<String, String>> dialog =
                    createAddEditKontaktDialog("contact.edit", "contact.edit", "imageview-edit", this.selectedKontakt.getValue(), resources);

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(pair -> {
                try
                {
                    this.service.updateKontakt(this.selectedKontakt.getValue().getID(), pair.getLeft(), pair.getRight());

                    this.selectedKontakt.getValue().setNachname(pair.getLeft());
                    this.selectedKontakt.getValue().setVorname(StringUtils.defaultIfBlank(pair.getRight(), null));
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        this.buttonDeleteContact.setOnAction(event -> {
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
                    this.service.deleteKontakt(this.selectedKontakt.getValue().getID());
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
     * Kontakte laden
     */
    private void loadKontakte()
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Load Kontakte");
        }

        getKontakteList().clear();

        Task<List<Kontakt>> task = new Task<List<Kontakt>>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected List<Kontakt> call() throws Exception
            {
                return ContactController.this.service.getKontaktDetails();
            }
        };

        task.setOnSucceeded(event -> {
            getKontakteList().addAll(task.getValue());
        });

        task.setOnFailed(event -> {
            getLogger().error(null, task.getException());

            new ErrorDialog().forThrowable(task.getException()).showAndWait();
        });

        PIMApplication.getExecutorService().execute(task);
    }
}
