/**
 * Created: 01.01.2017
 */

package de.freese.pim.gui.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import org.apache.commons.lang3.ArrayUtils;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.gui.PIMApplication;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeItem;

/**
 * {@link Service} zu Laden der Folder-Hierarchie.
 *
 * @author Thomas Freese
 */
public class InitMailAccountService extends Service<Void>
{
    /**
    *
    */
    private final MailAccount mailAccount;

    /**
    *
    */
    private final TreeItem<Object> root;

    /**
     * Erstellt ein neues {@link InitMailAccountService} Object.
     *
     * @param root {@link TreeItem}
     * @param mailAccount {@link Store}
     */
    public InitMailAccountService(final TreeItem<Object> root, final MailAccount mailAccount)
    {
        super();

        Objects.requireNonNull(root, "root item required");
        Objects.requireNonNull(mailAccount, "mailAccount required");

        this.root = root;
        this.mailAccount = mailAccount;

        setExecutor(PIMApplication.getExecutorService());
    }

    /**
     * @see javafx.concurrent.Service#createTask()
     */
    @Override
    protected Task<Void> createTask()
    {
        Task<Void> task = new Task<Void>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected Void call() throws Exception
            {
                InitMailAccountService.this.mailAccount.getSession(); // Trigger Connect

                List<TreeItem<Object>> childItems = loadChildFolder(InitMailAccountService.this.mailAccount.getStore().getDefaultFolder());

                Platform.runLater(() -> {
                    InitMailAccountService.this.root.getChildren().addAll(childItems);
                    InitMailAccountService.this.root.setExpanded(true);
                });

                return null;
            }
        };

        setOnSucceeded(event -> {
            // List<TreeItem<Object>> childItems = task.get();
            // TreeModificationEvent<Object> treeEvent = new TreeModificationEvent<>(TreeItem.childrenModificationEvent(), accountItem);
            // Event.fireEvent(accountItem, treeEvent);
        });

        setOnFailed(event -> {
            Throwable th = getException();

            PIMApplication.LOGGER.error(null, th);

            Alert alert = new Alert(AlertType.ERROR, th.getMessage());
            alert.showAndWait();
        });

        return task;
    }

    /**
     * Laden der direkten Children, nicht die ganze Hierarchie.
     *
     * @param parent {@link Folder}
     * @return {@link Folder}[]
     * @throws MessagingException Falls was schief geht.
     */
    private List<TreeItem<Object>> loadChildFolder(final Folder parent) throws MessagingException
    {
        Folder[] childFolders = parent.list("%");

        if (ArrayUtils.isEmpty(childFolders))
        {
            return Collections.emptyList();
        }

        List<TreeItem<Object>> childList = new ArrayList<>();

        for (Folder child : childFolders)
        {
            TreeItem<Object> treeItem = new TreeItem<>(child);
            childList.add(treeItem);

            List<TreeItem<Object>> childsOfChild = loadChildFolder(child);

            for (TreeItem<Object> childOfChild : childsOfChild)
            {
                treeItem.getChildren().add(childOfChild);
            }
        }

        return childList;
    }
}
