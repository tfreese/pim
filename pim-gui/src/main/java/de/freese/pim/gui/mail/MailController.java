// Created: 13.12.2016
package de.freese.pim.gui.mail;

import java.net.URL;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import de.freese.pim.core.mail.dao.DefaultMailDAO;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.core.mail.service.JavaMailService;
import de.freese.pim.core.service.SettingService;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
    private IMailDAO mailDAO = null;

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
    private List<TableColumn<Mail, ?>> tableColumnsReceived = null;

    /**
     * Spalten für die Sende-Sicht.
     */
    private List<TableColumn<Mail, ?>> tableColumnsSend = null;

    /**
    *
    */
    @FXML
    private TableView<Mail> tableViewMail = null;

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
        this.mailDAO = new DefaultMailDAO();

        // Tabelle
        this.tableViewMail.setRowFactory(tableView -> {
            return new TableRow<Mail>()
            {
                /**
                 * @param item {@link Mail}
                 * @param empty boolean
                 */
                @Override
                public void updateItem(final Mail item, final boolean empty)
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

            if (value instanceof MailFolder)
            {
                MailFolder folder = (MailFolder) value;

                setReceivedTableColumns(resources);

                this.tableViewMail.setItems(folder.getMailsSorted());

                if (!folder.getMailsSorted().isEmpty())
                {
                    return;
                }

                Task<Void> loadMailsTask = new Task<Void>()
                {
                    /**
                     * @see javafx.concurrent.Task#call()
                     */
                    @Override
                    protected Void call() throws Exception
                    {
                        IMailService mailService = folder.getMailService();

                        mailService.loadMails(folder, m -> {
                            Platform.runLater(() -> {
                                folder.getMails().add(m);
                            });
                        });

                        return null;
                    }
                };
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

                if (item instanceof IMailService)
                {
                    text = ((IMailService) item).getAccount().getMail();
                    // newMails = ((IMailFolder) item).getUnreadMessageCount();
                }
                else if (item instanceof MailFolder)
                {
                    text = ((MailFolder) item).getName();
                    // newMails = ((IMailFolder) item).getUnreadMessageCount();
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

        try
        {
            List<MailAccount> accountList = this.mailDAO.getMailAccounts();
            Path basePath = SettingService.getInstance().getHome();

            for (MailAccount mailAccount : accountList)
            {
                Path accountPath = basePath.resolve(mailAccount.getMail());
                IMailService mailService = new JavaMailService(mailAccount, accountPath);
                mailService.setExecutor(getExecutorService());

                TreeItem<Object> treeItem = new TreeItem<>(mailService);
                root.getChildren().add(treeItem);

                PIMApplication.registerCloseable(() -> {
                    getLogger().info("Close " + mailService.getAccount().getMail());
                    mailService.disconnect();
                });

                getLogger().info("Init MailAccount {}", mailService.getAccount().getMail());
                InitMailService service = new InitMailService(treeItem, mailService);
                service.start();
            }

            // for (MailConfig mailConfig : configList)
            // {
            // mailConfig.setExecutor(getExecutorService());
            //
            // IMailAccount mailAccount = new ImapMailAccount();
            // TreeItem<Object> treeItem = new TreeItem<>(mailAccount);
            // root.getChildren().add(treeItem);
            //
            // PIMApplication.registerCloseable(() -> {
            // getLogger().info("Close " + mailConfig.getMail());
            // mailAccount.disconnect();
            // });
            //
            // getLogger().info("Init MailAccount {}", mailConfig.getMail());
            // InitMailAccountService service = new InitMailAccountService(treeItem, mailConfig);
            // service.start();
            // }
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

            TableColumn<Mail, String> columnFrom = new TableColumn<>(resources.getString("mail.from"));
            // columnFrom.setResizable(false);
            columnFrom.setCellValueFactory(cell -> cell.getValue().fromProperty().asString()); // Für reine FX-Bean
            // columnFrom.setCellValueFactory(new PropertyValueFactory<>("from")); // Updates erfolgen nur, wenn Bean PropertyChangeSupport hat
            columnFrom.setStyle("-fx-alignment: center-left;");
            columnFrom.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.30D)); // 30% Breite
            // columnFrom.setMaxWidth(50);

            TableColumn<Mail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            columnSubject.setCellValueFactory(cell -> cell.getValue().subjectProperty());
            // columnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.55D)); // 60% Breite

            TableColumn<Mail, Date> columnReceived = new TableColumn<>(resources.getString("mail.received"));
            columnReceived.setStyle("-fx-alignment: center-right;");
            columnReceived.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.15D)); // 10% Breite
            columnReceived.setCellValueFactory(cell -> cell.getValue().receivedDateProperty());
            // columnReceived.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
            columnReceived.setCellFactory(column -> {
                return new TableCell<Mail, Date>()
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

            TableColumn<Mail, String> columnFrom = new TableColumn<>(resources.getString("mail.to"));
            // columnFrom.setCellValueFactory(new PropertyValueFactory<>("to"));
            columnFrom.setCellValueFactory(cell -> cell.getValue().toProperty().asString());
            columnFrom.setStyle("-fx-alignment: center-left;");
            columnFrom.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.30D)); // 30% Breite

            TableColumn<Mail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            // columnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
            columnSubject.setCellValueFactory(cell -> cell.getValue().subjectProperty());
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.55D)); // 60% Breite

            TableColumn<Mail, Date> columnSend = new TableColumn<>(resources.getString("mail.send"));
            columnSend.setStyle("-fx-alignment: center-right;");
            columnSend.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.15D)); // 10% Breite
            columnSend.setCellValueFactory(cell -> cell.getValue().sendDateProperty());
            // columnSend.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
            columnSend.setCellFactory(column -> {
                return new TableCell<Mail, Date>()
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
