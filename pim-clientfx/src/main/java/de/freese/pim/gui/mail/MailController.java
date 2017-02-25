// Created: 13.12.2016
package de.freese.pim.gui.mail;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import de.freese.pim.common.model.mail.InternetAddress;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.gui.mail.service.FXMailService;
import de.freese.pim.gui.mail.utils.InlineUrlStreamHandler;
import de.freese.pim.gui.mail.utils.MailUrlStreamHandlerFactory;
import de.freese.pim.gui.utils.FXUtils;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

/**
 * Controller des Mail-Clients.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class MailController extends AbstractController
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

    /**
     *
     */
    private final String formatDate = "%1$ta %1$td.%1$tm.%1$ty %1$tH:%1$tM:%1$tS";

    // /**
    // * DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("EE dd.MM.yy HH:mm:ss");
    // */
    // private final DateFormat formatterDate = new SimpleDateFormat("EE dd.MM.yy HH:mm:ss");

    /**
     *
     */
    private final FXMailService mailService;

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
    private final ObjectProperty<FXMail> selectedMail = new SimpleObjectProperty<>();

    /**
     *
     */
    private final ObjectProperty<TreeItem<Object>> selectedTreeItem = new SimpleObjectProperty<>();

    /**
     * Spalten für die Empfangs-Sicht.
     */
    private List<TableColumn<FXMail, ?>> tableColumnsReceived = null;

    /**
     * Spalten für die Sende-Sicht.
     */
    private List<TableColumn<FXMail, ?>> tableColumnsSend = null;

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
    private WebView webView = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailController}
     */
    public MailController()
    {
        super();

        this.mailService = PIMApplication.getApplicationContext().getBean("clientMailService", FXMailService.class);
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
        TreeItem<Object> root = new TreeItem<>("Mail-Accounts");
        this.treeViewMail.setRoot(root);

        loadMailAccounts(root);
    }

    /**
     * Hinzufügen eines MailAccount in die GUI
     *
     * @param root {@link TreeItem}
     * @param account {@link FXMailAccount}
     * @throws Exception Falls was schief geht.
     */
    private void addMailAccountToGUI(final TreeItem<Object> root, final FXMailAccount account) throws Exception
    {
        // Path basePath = SettingService.getInstance().getHome();
        // Path accountPath = basePath.resolve(account.getMail());

        // IMailAPI mailAPI = new JavaMailAPI(account, accountPath);
        // mailAPI.setMailService(this.mailService);
        // mailAPI.setExecutorService(getExecutorService());

        TreeItem<Object> parent = new TreeItem<>(account);
        root.getChildren().add(parent);
        parent.setExpanded(true);

        InitMailAPITask service = new InitMailAPITask(this.treeViewMail, parent, getMailService(), account);
        getTaskExecutor().execute(service);
    }

    /**
     * Liefert den MailAccount.
     *
     * @param treeItem {@link TreeItem}
     * @return {@link FXMailAccount}
     */
    private FXMailAccount getAccount(final TreeItem<Object> treeItem)
    {
        TreeItem<Object> ti = treeItem;

        while (!(ti.getValue() instanceof FXMailAccount))
        {
            ti = ti.getParent();
        }

        FXMailAccount account = (FXMailAccount) ti.getValue();

        return account;
    }

    /**
     * @return {@link FXMailService}
     */
    private FXMailService getMailService()
    {
        return this.mailService;
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
     * @return {@link ProgressIndicator}
     */
    private ProgressIndicator getProgressIndicator()
    {
        return this.progressIndicator;
    }

    /**
     * @see de.freese.pim.gui.controller.AbstractController#getToolBar()
     */
    @Override
    public ToolBar getToolBar()
    {
        return this.toolBar;
    }

    /**
     * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources)
    {
        // Buttons
        this.buttonAddAccount.setOnAction(event -> {
            EditMailAccountDialog dialog = new EditMailAccountDialog();
            Optional<FXMailAccount> result = dialog.addAccount(getMailService(), resources);
            result.ifPresent(account -> {
                try
                {
                    getMailService().insertAccount(account);
                    getMailService().insertOrUpdateFolder(account.getID(), account.getFolder());

                    addMailAccountToGUI(this.treeViewMail.getRoot(), account);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        this.buttonEditAccount.disableProperty().bind(this.selectedTreeItem.isNull());
        this.buttonEditAccount.setOnAction(event -> {
            FXMailAccount ma = getAccount(this.selectedTreeItem.get());

            EditMailAccountDialog dialog = new EditMailAccountDialog();
            Optional<FXMailAccount> result = dialog.editAccount(getMailService(), resources, ma);
            result.ifPresent(account -> {
                try
                {
                    getMailService().updateAccount(account);
                    getMailService().insertOrUpdateFolder(account.getID(), account.getFolder());
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        // Tabelle
        this.selectedMail.bind(this.tableViewMail.getSelectionModel().selectedItemProperty());
        this.selectedMail.addListener((observable, oldValue, newValue) -> selectedMail(newValue));

        this.tableViewMail.setRowFactory(tableView -> {
            return new TableRow<FXMail>()
            {
                /**
                 * @param item {@link FXMail}
                 * @param empty boolean
                 */
                @Override
                public void updateItem(final FXMail item, final boolean empty)
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

        // Tree
        this.selectedTreeItem.bind(this.treeViewMail.getSelectionModel().selectedItemProperty());
        this.selectedTreeItem.addListener((observable, oldValue, newValue) -> selectedTreeItem(newValue, resources));

        this.treeViewMail.setShowRoot(false);
        this.treeViewMail.setRoot(null);
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

                if (item instanceof FXMailAccount)
                {
                    text = ((FXMailAccount) item).getMail();
                    newMails = ((FXMailAccount) item).getUnreadMailsCount();
                }
                else if (item instanceof FXMailFolder)
                {
                    text = ((FXMailFolder) item).getName();
                    newMails = ((FXMailFolder) item).getUnreadMailsCountTotal();
                }

                if (newMails > 0)
                {
                    text += " (" + newMails + ")";
                    setStyle("-fx-font-weight: bold;");
                }

                setText(text);
            }
        });

        URL.setURLStreamHandlerFactory(new MailUrlStreamHandlerFactory());

        getProgressIndicator().styleProperty().bind(Bindings.createStringBinding(() -> {
            double percent = getProgressIndicator().getProgress();

            if (percent < 0)
            {
                // indeterminate
                return null;
            }

            int[] rgb = FXUtils.getProgressRGB(percent, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN);

            String style = String.format("-fx-progress-color: rgb(%d,%d,%d)", rgb[0], rgb[1], rgb[2]);

            return style;
        }, getProgressIndicator().progressProperty()));

        this.webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            try
            {
                URI address = new URI(newValue);
                getLogger().debug(address.toString());

                // Desktop.getDesktop().browse(address);
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

                if ((desktop != null) && desktop.isSupported(Desktop.Action.BROWSE))
                {
                    desktop.browse(address);

                    Platform.runLater(() -> this.webView.getEngine().load(oldValue));
                }

                // Ansonsten wird der Link in der WebEngine geladen.
                // if ((address.getQuery() + "").indexOf("_openmodal=true") > -1)
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);

                new ErrorDialog().forThrowable(ex).showAndWait();
            }
        });
    }

    /**
     * Laden der MailAccounts und befüllen des Trees.
     *
     * @param root {@link TreeItem}
     */
    private void loadMailAccounts(final TreeItem<Object> root)
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Load MailAccounts");
        }

        // contextMenuProperty().bind(
        // Bindings.when(Bindings.equal(itemProperty(),"TABS"))
        // .then(addMenu)
        // .otherwise((ContextMenu)null));
        try
        {
            List<FXMailAccount> accountList = getMailService().getMailAccounts();
            // Collections.reverse(accountList);

            for (FXMailAccount account : accountList)
            {
                addMailAccountToGUI(root, account);
            }
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        }
    }

    /**
     * Listener Methode.
     *
     * @param mail {@link FXMail}
     */
    private void selectedMail(final FXMail mail)
    {
        // Delete cache for navigate back.
        this.webView.getEngine().load("about:blank");
        // this.webView.getEngine().loadContent("");

        // Delete cookies.
        java.net.CookieHandler.setDefault(new java.net.CookieManager());

        if (mail == null)
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("no mail selected");
            }

            return;
        }

        PIMApplication.blockGUI();
        FXMailAccount account = getAccount(this.selectedTreeItem.get());

        Task<MailContent> loadMailContentTask = new Task<MailContent>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected MailContent call() throws Exception
            {
                // MailContent mailContent = getMailService().loadMailContent(account.getID(), mail,
                // (current, size) -> updateProgress(current, size));
                MailContent mailContent = getMailService().loadMailContent(account, mail, this::updateProgress);

                return mailContent;
            }
        };
        loadMailContentTask.setOnSucceeded(event -> {
            PIMApplication.unblockGUI();
            MailContent mailContent = loadMailContentTask.getValue();

            if (mailContent == null)
            {
                // String msg = String.format("no content: %s/%s%n", mail.getFolder().getFullName(), mail.getSubject());
                // getLogger().error(msg);
                // new ErrorDialog().headerText(msg).showAndWait();

                String msg = String.format("<b>Error: no content found for</b><br>folder=%s<br>subject=%s<br>", mail.getFolderFullName(), mail.getSubject());
                this.webView.getEngine().loadContent("<h2><font color=\"red\">" + msg + "</font></h2>");

                return;
            }

            InlineUrlStreamHandler.setMailContent(mailContent);

            // this.webView.getEngine().load(mailContent.getUrl().toExternalForm());
            this.webView.getEngine().loadContent(mailContent.getMessageContent(), mailContent.getMessageContentType());
        });
        loadMailContentTask.setOnFailed(event -> {
            PIMApplication.unblockGUI();
            Throwable th = loadMailContentTask.getException();

            getLogger().error(null, th);

            this.webView.getEngine().loadContent(ErrorDialog.toString(th), "text/plain");
        });

        // loadMailTask.progressProperty().addListener((obs, old, progress) -> getLogger().debug("{} %", progress.doubleValue() * 100));
        // Sichtbarkeit des ProgressIndikators und Cursors mit dem Laufstatus des Service/Task verknüpfen.
        getProgressIndicator().progressProperty().bind(loadMailContentTask.progressProperty());

        ReadOnlyBooleanProperty runningProperty = loadMailContentTask.runningProperty();
        getProgressIndicator().visibleProperty().bind(runningProperty);
        PIMApplication.getMainWindow().getScene().cursorProperty().bind(Bindings.when(runningProperty).then(Cursor.WAIT).otherwise(Cursor.DEFAULT));

        getTaskExecutor().execute(loadMailContentTask);
    }

    /**
     * Listener Methode.
     *
     * @param treeItem {@link TreeItem}
     * @param resources {@link ResourceBundle}
     */
    private void selectedTreeItem(final TreeItem<Object> treeItem, final ResourceBundle resources)
    {
        this.tableViewMail.setItems(null);

        if (treeItem == null)
        {
            return;
        }

        if (!(treeItem.getValue() instanceof FXMailFolder))
        {
            return;
        }

        FXMailFolder folder = (FXMailFolder) treeItem.getValue();

        if (folder.isSendFolder())
        {
            setSendTableColumns(resources);
        }
        else
        {
            setReceivedTableColumns(resources);
        }

        SortedList<FXMail> mailsSorted = folder.getMailsSorted();

        // Damit ColumnsSortierung funktioniert, da ich schon eine SortedList verwende.
        // mailsSorted.comparatorProperty().unbind();
        // mailsSorted.comparatorProperty().bind(this.tableViewMail.comparatorProperty());
        this.tableViewMail.setItems(mailsSorted);

        if (!mailsSorted.isEmpty())
        {
            return;
        }

        LoadMailsTask loadMailsTask = new LoadMailsTask(this.treeViewMail, Collections.singletonList(folder), getMailService(), getAccount(treeItem));

        // Sichtbarkeit des ProgressIndikators und Cursors mit dem Laufstatus des Service/Task verknüpfen.
        ReadOnlyBooleanProperty runningProperty = loadMailsTask.runningProperty();

        getProgressIndicator().visibleProperty().bind(runningProperty);
        PIMApplication.getMainWindow().getScene().cursorProperty().bind(Bindings.when(runningProperty).then(Cursor.WAIT).otherwise(Cursor.DEFAULT));

        getTaskExecutor().execute(loadMailsTask);
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

            TableColumn<FXMail, InternetAddress> columnFrom = new TableColumn<>(resources.getString("mail.from"));
            TableColumn<FXMail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            TableColumn<FXMail, String> columnReceived = new TableColumn<>(resources.getString("mail.received"));

            // columnFrom.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.30D)); // 30% Breite
            columnFrom.setPrefWidth(300);
            columnReceived.setPrefWidth(180);
            columnSubject.prefWidthProperty()
                    .bind(this.tableViewMail.widthProperty().subtract(columnFrom.widthProperty().add(columnReceived.widthProperty()).add(2)));

            columnFrom.setSortable(false);
            columnFrom.setStyle("-fx-alignment: center-left;");
            columnFrom.setCellValueFactory(cell -> cell.getValue().fromProperty()); // Für reine FX-Bean.
            // columnFrom.setCellValueFactory(new PropertyValueFactory<>("from")); // Updates erfolgen nur, wenn Bean PropertyChangeSupport
            // hat.
            // columnFrom.setCellValueFactory(cell -> FXUtils.toStringObservable(cell.getValue().fromProperty(), addr ->
            // addr.getAddress()));
            // columnFrom.setCellFactory(new InternetAddressCellFactory<Mail>());

            columnSubject.setSortable(false);
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.setCellValueFactory(cell -> cell.getValue().subjectProperty());

            columnReceived.setSortable(false);
            columnReceived.setStyle("-fx-alignment: center-left;");
            columnReceived.setCellValueFactory(cell -> cell.getValue().receivedDateProperty().asString(this.formatDate));
            // columnReceived.setCellFactory(column -> {
            // return new TableCell<Mail, Date>()
            // {
            // /**
            // * @param item Date
            // * @param empty boolean
            // */
            // @Override
            // protected void updateItem(final Date item, final boolean empty)
            // {
            // super.updateItem(item, empty);
            //
            // if ((item == null) || empty)
            // {
            // setText(null);
            // return;
            // }
            //
            // setText(MailController.this.formatterReceivedDate.format(item));
            // }
            // };
            // });

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

            TableColumn<FXMail, InternetAddress[]> columnTo = new TableColumn<>(resources.getString("mail.to"));
            TableColumn<FXMail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            TableColumn<FXMail, String> columnSend = new TableColumn<>(resources.getString("mail.send"));

            columnTo.setPrefWidth(300);
            columnSend.setPrefWidth(180);
            columnSubject.prefWidthProperty()
                    .bind(this.tableViewMail.widthProperty().subtract(columnTo.widthProperty().add(columnSend.widthProperty()).add(2)));

            columnTo.setSortable(false);
            columnTo.setStyle("-fx-alignment: center-left;");
            columnTo.setCellValueFactory(cell -> cell.getValue().toProperty());
            columnTo.setCellFactory(new InternetAddressCellFactory<FXMail>());

            columnSubject.setSortable(false);
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.setCellValueFactory(cell -> cell.getValue().subjectProperty());

            columnSend.setSortable(false);
            columnSend.setStyle("-fx-alignment: center-left;");
            columnSend.setCellValueFactory(cell -> cell.getValue().sendDateProperty().asString(this.formatDate));

            this.tableColumnsSend.add(columnTo);
            this.tableColumnsSend.add(columnSubject);
            this.tableColumnsSend.add(columnSend);
        }

        this.tableViewMail.getColumns().clear();
        this.tableViewMail.getColumns().addAll(this.tableColumnsSend);
    }
}
