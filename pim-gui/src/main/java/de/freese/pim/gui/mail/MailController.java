// Created: 13.12.2016
package de.freese.pim.gui.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.freese.pim.core.mail.MailProvider;
import de.freese.pim.core.mail.model.IMail;
import de.freese.pim.core.mail.model.IMailAccount;
import de.freese.pim.core.mail.model.IMailFolder;
import de.freese.pim.core.mail.model.ImapMailAccount;
import de.freese.pim.core.mail.model.MailConfig;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller des Mail-Clients.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class MailController extends AbstractController
{
    /**
     * DateTimeFormatter formatterReceivedDate = DateTimeFormatter.ofPattern("EE dd.MM.yyyy HH:mm:ss");
     */
    private final DateFormat formatterReceivedDate = new SimpleDateFormat("EE dd.MM.yyyy HH:mm:ss");

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
    @FXML
    private ProgressIndicator progressIndicator = null;

    /**
    *
    */
    private final SimpleObjectProperty<TreeItem<?>> selectedTreeItem = new SimpleObjectProperty<>();

    /**
     * Spalten für die Empfangs-Sicht.
     */
    private List<TableColumn<IMail, ?>> tableColumnsReceived = null;

    /**
     * Spalten für die Sende-Sicht.
     */
    private List<TableColumn<IMail, ?>> tableColumnsSend = null;

    /**
    *
    */
    @FXML
    private TableView<IMail> tableViewMail = null;

    /**
    *
    */
    @FXML
    private TreeView<Object> treeViewMail = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailController}
     */
    public MailController()
    {
        super();
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
        // Tabelle
        this.tableViewMail.setRowFactory(tableView -> {
            return new TableRow<IMail>()
            {
                /**
                 * @param item {@link IMail}
                 * @param empty boolean
                 */
                @Override
                public void updateItem(final IMail item, final boolean empty)
                {
                    super.updateItem(item, empty);

                    if ((item == null) || empty)
                    {
                        setStyle(null);
                        return;
                    }

                    if (!item.isSeen())
                    {
                        setStyle("-fx-font-weight: bold;");
                    }
                    else
                    {
                        setStyle(null);
                    }
                }
            };
        });

        this.selectedTreeItem.addListener((observable, oldValue, newValue) -> {

            this.tableViewMail.setItems(null);

            if (newValue == null)
            {
                return;
            }

            Object value = ((TreeItem) newValue).getValue();

            if (value instanceof IMailFolder)
            {
                setReceivedTableColumns(resources);

                Task<ObservableList<IMail>> loadMailsTask = new Task<ObservableList<IMail>>()
                {
                    /**
                     * @see javafx.concurrent.Task#call()
                     */
                    @Override
                    protected ObservableList<IMail> call() throws Exception
                    {
                        ObservableList<IMail> mails = ((IMailFolder) value).getMessages();

                        return mails;
                    }
                };
                loadMailsTask.setOnSucceeded(event -> {
                    this.tableViewMail.setItems(loadMailsTask.getValue());
                });
                loadMailsTask.setOnFailed(event -> {
                    Throwable th = loadMailsTask.getException();

                    getLogger().error(null, th);

                    new ErrorDialog().forThrowable(th).showAndWait();
                });

                // Sichtbarkeit des ProgressIndikators und Cursors mit dem Laufstatus des Service/Task verknüpfen.
                ReadOnlyBooleanProperty runningProperty = loadMailsTask.runningProperty();

                this.progressIndicator.visibleProperty().bind(runningProperty);
                PIMApplication.getMainWindow().getScene().cursorProperty().bind(Bindings.when(runningProperty).then(Cursor.WAIT).otherwise(Cursor.DEFAULT));

                getExecutorService().execute(loadMailsTask);
            }
        });

        // Tree
        this.selectedTreeItem.bind(this.treeViewMail.getSelectionModel().selectedItemProperty());

        this.treeViewMail.setShowRoot(false);
        this.treeViewMail.setCellFactory(v -> new TreeCell<Object>()
        {
            /**
             * @param item Object
             * @param empty boolean
             */
            @Override
            public void updateItem(final Object item, final boolean empty)
            {
                super.updateItem(item, empty);

                setStyle(null);

                if ((item == null) || empty)
                {
                    setText(null);

                    return;
                }

                String text = null;
                int newMails = 0;

                if (item instanceof IMailAccount)
                {
                    text = ((IMailAccount) item).getName();
                    newMails = ((IMailAccount) item).getUnreadMessageCount();
                }
                else if (item instanceof IMailFolder)
                {
                    text = ((IMailFolder) item).getName();
                    newMails = ((IMailFolder) item).getUnreadMessageCount();
                }

                if (newMails > 0)
                {
                    text += " (" + newMails + ")";
                    setStyle("-fx-font-weight: bold;");
                }

                setText(text);
            }
        });

        TreeItem<Object> root = new TreeItem<>("Mail-Accounts");
        this.treeViewMail.setRoot(root);

        // TODO Aus DB laden.
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        Path configs = getSettingService().getHome().resolve(".mailconfigs");
        List<MailConfig> configList = new ArrayList<>();

        if (Files.exists(configs))
        {
            try (InputStream is = Files.newInputStream(configs))
            {
                // MailAccount mailAccount = jsonMapper.readValue(is, MailAccount.class);
                // root.getChildren().add(new TreeItem<>(mailAccount));

                JavaType type = jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, MailConfig.class);
                configList.addAll(jsonMapper.readValue(is, type));
            }
            catch (IOException ex)
            {
                getLogger().error(null, ex);

                new ErrorDialog().forThrowable(ex).showAndWait();
            }
        }
        else
        {
            MailConfig config = new MailConfig();
            config.setMail("commercial@freese-home.de");
            config.setImapHost(MailProvider.EinsUndEins.getImapHost());
            config.setSmtpHost(MailProvider.EinsUndEins.getSmtpHost());
            configList.add(config);

            try (OutputStream os = Files.newOutputStream(configs))
            {
                jsonMapper.writer().writeValue(os, Arrays.asList(config));
            }
            catch (IOException ex)
            {
                getLogger().error(null, ex);

                new ErrorDialog().forThrowable(ex).showAndWait();
            }
        }

        try
        {
            for (MailConfig mailConfig : configList)
            {
                mailConfig.setExecutor(getExecutorService());

                IMailAccount mailAccount = new ImapMailAccount();
                TreeItem<Object> treeItem = new TreeItem<>(mailAccount);
                root.getChildren().add(treeItem);

                PIMApplication.registerCloseable(() -> {
                    getLogger().info("Close " + mailConfig.getMail());
                    mailAccount.disconnect();
                });

                getLogger().info("Init MailAccount {}", mailConfig.getMail());
                InitMailAccountService service = new InitMailAccountService(treeItem, mailConfig);
                service.start();
            }
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        }
    }

    /**
     * Setzte die Spalten für die Empfangs-Sicht der Tabelle.
     *
     * @param resources {@link ResourceBundle}
     */
    private void setReceivedTableColumns(final ResourceBundle resources)
    {
        if (this.tableColumnsReceived == null)
        {
            this.tableColumnsReceived = new ArrayList<>();

            TableColumn<IMail, String> columnFrom = new TableColumn<>(resources.getString("mail.from"));
            // columnFrom.setResizable(false);
            // columnFrom.setCellValueFactory(cell -> (ObservableValue) cell.getValue().getPropertytID()); // Für reine FX-Bean
            columnFrom.setCellValueFactory(new PropertyValueFactory<>("from")); // Updates erfolgen nur, wenn Bean PropertyChangeSupport hat
            columnFrom.setStyle("-fx-alignment: center-left;");
            columnFrom.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.30D)); // 30% Breite
            // columnFrom.setMaxWidth(50);

            TableColumn<IMail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            columnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.55D)); // 60% Breite

            TableColumn<IMail, Date> columnReceived = new TableColumn<>(resources.getString("mail.received"));
            columnReceived.setStyle("-fx-alignment: center-right;");
            columnReceived.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.15D)); // 10% Breite
            columnReceived.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
            columnReceived.setCellFactory(column -> {
                return new TableCell<IMail, Date>()
                {
                    /**
                     * @param item Date
                     * @param empty boolean
                     */
                    @Override
                    protected void updateItem(final Date item, final boolean empty)
                    {
                        super.updateItem(item, empty);

                        if ((item == null) || empty)
                        {
                            setText(null);
                            return;
                        }

                        setText(MailController.this.formatterReceivedDate.format(item));
                    }
                };
            });

            this.tableColumnsReceived.add(columnFrom);
            this.tableColumnsReceived.add(columnSubject);
            this.tableColumnsReceived.add(columnReceived);
        }

        this.tableViewMail.getColumns().clear();
        this.tableViewMail.getColumns().addAll(this.tableColumnsReceived);
    }

    /**
     * Setzte die Spalten für die Sende-Sicht der Tabelle.
     *
     * @param resources {@link ResourceBundle}
     */
    private void setSendTableColumns(final ResourceBundle resources)
    {
        if (this.tableColumnsSend == null)
        {
            this.tableColumnsSend = new ArrayList<>();

            TableColumn<IMail, String> columnFrom = new TableColumn<>(resources.getString("mail.to"));
            columnFrom.setCellValueFactory(new PropertyValueFactory<>("to"));
            columnFrom.setStyle("-fx-alignment: center-left;");
            columnFrom.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.30D)); // 30% Breite

            TableColumn<IMail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            columnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.55D)); // 60% Breite

            TableColumn<IMail, Date> columnSend = new TableColumn<>(resources.getString("mail.send"));
            columnSend.setStyle("-fx-alignment: center-right;");
            columnSend.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.15D)); // 10% Breite
            columnSend.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
            columnSend.setCellFactory(column -> {
                return new TableCell<IMail, Date>()
                {
                    /**
                     * @param item Date
                     * @param empty boolean
                     */
                    @Override
                    protected void updateItem(final Date item, final boolean empty)
                    {
                        super.updateItem(item, empty);

                        if ((item == null) || empty)
                        {
                            setText(null);
                            return;
                        }

                        setText(MailController.this.formatterReceivedDate.format(item));
                    }
                };
            });

            this.tableColumnsSend.add(columnFrom);
            this.tableColumnsSend.add(columnSubject);
            this.tableColumnsSend.add(columnSend);
        }

        this.tableViewMail.getColumns().clear();
        this.tableViewMail.getColumns().addAll(this.tableColumnsSend);
    }
}
