// Created: 13.12.2016
package de.freese.pim.gui.mail;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

import de.freese.pim.core.mail.InternetAddress;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.spring.SpringContext;
import de.freese.pim.gui.PimClientApplication;
import de.freese.pim.gui.controller.AbstractController;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;
import de.freese.pim.gui.mail.service.FxMailService;
import de.freese.pim.gui.mail.utils.MailUrlStreamHandlerFactory;
import de.freese.pim.gui.mail.view.MailContentView;
import de.freese.pim.gui.utils.FxUtils;
import de.freese.pim.gui.view.ErrorDialog;

/**
 * Controller des Mail-Clients.
 *
 * @author Thomas Freese
 */
public class MailController extends AbstractController {
    private static final String FORMAT_DATE = "%1$ta %1$td.%1$tm.%1$ty %1$tH:%1$tM:%1$tS";

    private final FxMailService mailService;

    private final ObjectProperty<FxMail> selectedMail = new SimpleObjectProperty<>();
    // /**
    // * DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("EE dd.MM.yy HH:mm:ss");
    // */
    // private final DateFormat formatterDate = new SimpleDateFormat("EE dd.MM.yy HH:mm:ss");

    private final ObjectProperty<TreeItem<Object>> selectedTreeItem = new SimpleObjectProperty<>();

    @FXML
    private Button buttonAddAccount;
    @FXML
    private Button buttonEditAccount;
    @FXML
    private MailContentView mailContentView;
    @FXML
    private Node mainNode;
    @FXML
    private Node naviNode;
    @FXML
    private ProgressIndicator progressIndicator;
    private List<TableColumn<FxMail, ?>> tableColumnsReceived;
    private List<TableColumn<FxMail, ?>> tableColumnsSend;
    @FXML
    private TableView<FxMail> tableViewMail;
    @FXML
    private ToolBar toolBar;
    @FXML
    private TreeView<Object> treeViewMail;

    public MailController() {
        super();

        this.mailService = SpringContext.getBean("clientMailService", FxMailService.class);
    }

    @Override
    public void activate() {
        if (isActivated()) {
            return;
        }

        setActivated(true);

        // Daten laden.
        final TreeItem<Object> root = new TreeItem<>("Mail-Accounts");
        this.treeViewMail.setRoot(root);

        loadMailAccounts(root);
    }

    @Override
    public Node getMainNode() {
        return this.mainNode;
    }

    @Override
    public Node getNaviNode() {
        return this.naviNode;
    }

    @Override
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // Buttons
        this.buttonAddAccount.setOnAction(event -> {
            final EditMailAccountDialog dialog = new EditMailAccountDialog();
            final Optional<FxMailAccount> result = dialog.addAccount(getMailService(), resources);
            result.ifPresent(account -> {
                try {
                    getMailService().insertAccount(account);
                    getMailService().insertOrUpdateFolder(account.getID(), account.getFolder());

                    addMailAccountToGUI(this.treeViewMail.getRoot(), account);
                }
                catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        this.buttonEditAccount.disableProperty().bind(this.selectedTreeItem.isNull());
        this.buttonEditAccount.setOnAction(event -> {
            final FxMailAccount ma = getAccount(this.selectedTreeItem.get());

            final EditMailAccountDialog dialog = new EditMailAccountDialog();
            final Optional<FxMailAccount> result = dialog.editAccount(getMailService(), resources, ma);
            result.ifPresent(account -> {
                try {
                    getMailService().updateAccount(account);
                    getMailService().insertOrUpdateFolder(account.getID(), account.getFolder());
                }
                catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);

                    new ErrorDialog().forThrowable(ex).showAndWait();
                }
            });
        });

        // Tabelle
        this.selectedMail.bind(this.tableViewMail.getSelectionModel().selectedItemProperty());
        this.selectedMail.addListener((observable, oldValue, newValue) -> selectedMail(newValue));

        this.tableViewMail.setRowFactory(tableView -> new TableRow<>() {
            /**
             * @param item {@link FxMail}
             * @param empty boolean
             */
            @Override
            public void updateItem(final FxMail item, final boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle(null);
                    return;
                }

                if (!item.isSeen()) {
                    setStyle("-fx-font-weight: bold;");
                }
                else {
                    setStyle(null);
                }
            }
        });

        // Tree
        this.selectedTreeItem.bind(this.treeViewMail.getSelectionModel().selectedItemProperty());
        this.selectedTreeItem.addListener((observable, oldValue, newValue) -> selectedTreeItem(newValue, resources));

        this.treeViewMail.setShowRoot(false);
        this.treeViewMail.setRoot(null);
        this.treeViewMail.setCellFactory(v -> new TreeCell<>() {
            /**
             * @param item Object
             * @param empty boolean
             */
            @Override
            public void updateItem(final Object item, final boolean empty) {
                super.updateItem(item, empty);

                setStyle(null);

                if (item == null || empty) {
                    setText(null);

                    return;
                }

                String text = null;
                int newMails = 0;

                if (item instanceof FxMailAccount ma) {
                    text = ma.getMail();
                    newMails = ma.getUnreadMailsCount();
                }
                else if (item instanceof FxMailFolder mf) {
                    text = mf.getName();
                    newMails = mf.getUnreadMailsCountTotal();
                }

                if (newMails > 0) {
                    text += " (" + newMails + ")";
                    setStyle("-fx-font-weight: bold;");
                }

                setText(text);
            }
        });

        try {
            URL.setURLStreamHandlerFactory(new MailUrlStreamHandlerFactory());
        }
        catch (Error er) {
            // Wenn Tomcat der EmbeddedServer ist, war die Default URLStreamHandlerFactory hier schon gesetzt.
            // TomcatURLStreamHandlerFactory.getInstance().addUserFactory(new MailUrlStreamHandlerFactory());
            try {
                final Class<?> tomcatClazz = Class.forName("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
                Method method = tomcatClazz.getMethod("getInstance");
                final Object ref = method.invoke(null);

                method = tomcatClazz.getMethod("addUserFactory", URLStreamHandlerFactory.class);
                method.invoke(ref, new MailUrlStreamHandlerFactory());
            }
            catch (Exception ex) {
                getLogger().warn(ex.getMessage());
            }
        }

        getProgressIndicator().styleProperty().bind(Bindings.createStringBinding(() -> {
            final double percent = getProgressIndicator().getProgress();

            if (percent < 0) {
                // indeterminate
                return null;
            }

            final int[] rgb = FxUtils.getProgressRGB(percent, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN);

            return String.format("-fx-progress-color: rgb(%d,%d,%d)", rgb[0], rgb[1], rgb[2]);
        }, getProgressIndicator().progressProperty()));
    }

    private void addMailAccountToGUI(final TreeItem<Object> root, final FxMailAccount account) {
        // Path basePath = SettingService.getInstance().getHome();
        // Path accountPath = basePath.resolve(account.getMail());

        // IMailAPI mailAPI = new JavaMailApi(account, accountPath);
        // mailAPI.setMailService(this.mailService);
        // mailAPI.setExecutorService(getExecutorService());

        final TreeItem<Object> parent = new TreeItem<>(account);
        root.getChildren().add(parent);
        parent.setExpanded(true);

        final InitMailApiTask service = new InitMailApiTask(this.treeViewMail, parent, getMailService(), account);
        getTaskExecutor().execute(service);
    }

    private FxMailAccount getAccount(final TreeItem<Object> treeItem) {
        TreeItem<Object> ti = treeItem;

        while (!(ti.getValue() instanceof FxMailAccount)) {
            ti = ti.getParent();
        }

        return (FxMailAccount) ti.getValue();
    }

    private FxMailService getMailService() {
        return this.mailService;
    }

    private ProgressIndicator getProgressIndicator() {
        return this.progressIndicator;
    }

    private void loadMailAccounts(final TreeItem<Object> root) {
        getLogger().debug("Load MailAccounts");

        // contextMenuProperty().bind(
        // Bindings.when(Bindings.equal(itemProperty(),"TABS"))
        // .then(addMenu)
        // .otherwise((ContextMenu)null));
        try {
            final List<FxMailAccount> accountList = getMailService().getMailAccounts();
            // Collections.reverse(accountList);

            for (FxMailAccount account : accountList) {
                addMailAccountToGUI(root, account);
            }
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);

            new ErrorDialog().forThrowable(ex).showAndWait();
        }
    }

    private void selectedMail(final FxMail mail) {
        this.mailContentView.newMailContent(null, null);

        if (mail == null) {
            getLogger().debug("no mail selected");

            return;
        }

        PimClientApplication.blockGUI();
        final FxMailAccount account = getAccount(this.selectedTreeItem.get());

        final Task<MailContent> loadMailContentTask = new Task<>() {
            @Override
            protected MailContent call() throws Exception {
                return getMailService().loadMailContent(account, mail, this::updateProgress);
            }
        };
        loadMailContentTask.setOnSucceeded(event -> {
            PimClientApplication.unblockGUI();
            final MailContent mailContent = loadMailContentTask.getValue();

            this.mailContentView.newMailContent(mail, mailContent);
        });
        loadMailContentTask.setOnFailed(event -> {
            PimClientApplication.unblockGUI();
            final Throwable th = loadMailContentTask.getException();

            getLogger().error(th.getMessage(), th);

            this.mailContentView.displayThrowable(th);
        });

        // loadMailTask.progressProperty().addListener((obs, old, progress) -> getLogger().debug("{} %", progress.doubleValue() * 100));
        // Sichtbarkeit des ProgressIndikators und Cursors mit dem Laufstatus des Service/Task verknüpfen.
        getProgressIndicator().progressProperty().bind(loadMailContentTask.progressProperty());

        final ReadOnlyBooleanProperty runningProperty = loadMailContentTask.runningProperty();
        getProgressIndicator().visibleProperty().bind(runningProperty);
        PimClientApplication.getMainWindow().getScene().cursorProperty().bind(Bindings.when(runningProperty).then(Cursor.WAIT).otherwise(Cursor.DEFAULT));

        getTaskExecutor().execute(loadMailContentTask);
    }

    private void selectedTreeItem(final TreeItem<Object> treeItem, final ResourceBundle resources) {
        this.tableViewMail.setItems(null);

        if (treeItem == null || !(treeItem.getValue() instanceof FxMailFolder folder)) {
            return;
        }

        if (folder.isSendFolder()) {
            setSendTableColumns(resources);
        }
        else {
            setReceivedTableColumns(resources);
        }

        final SortedList<FxMail> mailsSorted = folder.getMailsSorted();

        // Damit ColumnsSortierung funktioniert, da ich schon eine SortedList verwende.
        // mailsSorted.comparatorProperty().unbind();
        // mailsSorted.comparatorProperty().bind(this.tableViewMail.comparatorProperty());
        this.tableViewMail.setItems(mailsSorted);

        if (!mailsSorted.isEmpty()) {
            return;
        }

        final LoadMailsTask loadMailsTask = new LoadMailsTask(this.treeViewMail, Collections.singletonList(folder), getMailService(), getAccount(treeItem));

        // Sichtbarkeit des ProgressIndikators und Cursors mit dem Laufstatus des Service/Task verknüpfen.
        final ReadOnlyBooleanProperty runningProperty = loadMailsTask.runningProperty();

        getProgressIndicator().visibleProperty().bind(runningProperty);
        PimClientApplication.getMainWindow().getScene().cursorProperty().bind(Bindings.when(runningProperty).then(Cursor.WAIT).otherwise(Cursor.DEFAULT));

        getTaskExecutor().execute(loadMailsTask);
    }

    private void setReceivedTableColumns(final ResourceBundle resources) {
        if (this.tableColumnsReceived == null) {
            this.tableColumnsReceived = new ArrayList<>();

            final TableColumn<FxMail, InternetAddress> columnFrom = new TableColumn<>(resources.getString("mail.from"));
            final TableColumn<FxMail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            final TableColumn<FxMail, String> columnReceived = new TableColumn<>(resources.getString("mail.received"));

            // columnFrom.prefWidthProperty().bind(this.tableViewMail.widthProperty().multiply(0.30D)); // 30% Breite
            columnFrom.setPrefWidth(300);
            columnReceived.setPrefWidth(180);
            columnSubject.prefWidthProperty().bind(this.tableViewMail.widthProperty().subtract(columnFrom.widthProperty().add(columnReceived.widthProperty()).add(2)));

            columnFrom.setSortable(false);
            columnFrom.setStyle("-fx-alignment: center-left;");
            columnFrom.setCellValueFactory(cell -> cell.getValue().fromProperty()); // Für reine FX-Bean.
            // columnFrom.setCellValueFactory(new PropertyValueFactory<>("from")); // Updates erfolgen nur, wenn Bean PropertyChangeSupport hat.
            // columnFrom.setCellValueFactory(cell -> FxUtils.toStringObservable(cell.getValue().fromProperty(), addr ->
            // addr.getAddress()));
            // columnFrom.setCellFactory(new InternetAddressCellFactory<Mail>());

            columnSubject.setSortable(false);
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.setCellValueFactory(cell -> cell.getValue().subjectProperty());

            columnReceived.setSortable(false);
            columnReceived.setStyle("-fx-alignment: center-left;");
            columnReceived.setCellValueFactory(cell -> cell.getValue().receivedDateProperty().asString(FORMAT_DATE));
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

    private void setSendTableColumns(final ResourceBundle resources) {
        if (this.tableColumnsSend == null) {
            this.tableColumnsSend = new ArrayList<>();

            final TableColumn<FxMail, InternetAddress[]> columnTo = new TableColumn<>(resources.getString("mail.to"));
            final TableColumn<FxMail, String> columnSubject = new TableColumn<>(resources.getString("mail.subject"));
            final TableColumn<FxMail, String> columnSend = new TableColumn<>(resources.getString("mail.send"));

            columnTo.setPrefWidth(300);
            columnSend.setPrefWidth(180);
            columnSubject.prefWidthProperty().bind(this.tableViewMail.widthProperty().subtract(columnTo.widthProperty().add(columnSend.widthProperty()).add(2)));

            columnTo.setSortable(false);
            columnTo.setStyle("-fx-alignment: center-left;");
            columnTo.setCellValueFactory(cell -> cell.getValue().toProperty());
            columnTo.setCellFactory(new InternetAddressCellFactory<>());

            columnSubject.setSortable(false);
            columnSubject.setStyle("-fx-alignment: center-left;");
            columnSubject.setCellValueFactory(cell -> cell.getValue().subjectProperty());

            columnSend.setSortable(false);
            columnSend.setStyle("-fx-alignment: center-left;");
            columnSend.setCellValueFactory(cell -> cell.getValue().sendDateProperty().asString(FORMAT_DATE));

            this.tableColumnsSend.add(columnTo);
            this.tableColumnsSend.add(columnSubject);
            this.tableColumnsSend.add(columnSend);
        }

        this.tableViewMail.getColumns().clear();
        this.tableViewMail.getColumns().addAll(this.tableColumnsSend);
    }
}
