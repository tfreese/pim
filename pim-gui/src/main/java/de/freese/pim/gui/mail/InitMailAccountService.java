/**
 * Created: 01.01.2017
 */

package de.freese.pim.gui.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

import de.freese.pim.core.mail.model.IMailAccount;
import de.freese.pim.core.mail.model.IMailFolder;
import de.freese.pim.core.mail.model.MailConfig;
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
@SuppressWarnings("restriction")
public class InitMailAccountService extends Service<Void>
{
    /**
    *
    */
    private final MailConfig mailConfig;

    /**
    *
    */
    private final TreeItem<Object> root;

    /**
     * Erstellt ein neues {@link InitMailAccountService} Object.
     *
     * @param root {@link TreeItem}
     * @param mailConfig {@link MailConfig}
     */
    public InitMailAccountService(final TreeItem<Object> root, final MailConfig mailConfig)
    {
        super();

        Objects.requireNonNull(root, "root item required");
        Objects.requireNonNull(mailConfig, "mailConfig required");

        this.root = root;
        this.mailConfig = mailConfig;

        setExecutor(PIMApplication.getExecutorService());
    }

    /**
     * Liefert den {@link IMailAccount}.
     *
     * @return {@link IMailAccount}
     */
    private IMailAccount getMailAccount()
    {
        return (IMailAccount) this.root.getValue();
    }

    /**
     * Laden der direkten Children, nicht die ganze Hierarchie.
     *
     * @param parent {@link IMailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    private List<TreeItem<Object>> loadChildFolders(final IMailFolder parent) throws Exception
    {
        List<IMailFolder> children = parent.getChildren();

        if (CollectionUtils.isEmpty(children))
        {
            return Collections.emptyList();
        }

        List<TreeItem<Object>> childItems = new ArrayList<>();

        for (IMailFolder child : children)
        {
            TreeItem<Object> treeItem = new TreeItem<>(child);
            childItems.add(treeItem);

            treeItem.getChildren().addAll(loadChildFolders(child));
        }

        return childItems;
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
                IMailAccount mailAccount = getMailAccount();
                mailAccount.connect(InitMailAccountService.this.mailConfig);

                List<IMailFolder> topLevelFolder = mailAccount.getTopLevelFolder();

                List<TreeItem<Object>> childItems = new ArrayList<>();

                for (IMailFolder folder : topLevelFolder)
                {
                    TreeItem<Object> treeItem = new TreeItem<>(folder);
                    childItems.add(treeItem);

                    treeItem.getChildren().addAll(loadChildFolders(folder));
                }

                Platform.runLater(() ->
                {
                    InitMailAccountService.this.root.getChildren().addAll(childItems);
                    InitMailAccountService.this.root.setExpanded(true);
                });

                return null;
            }
        };

        setOnSucceeded(event ->
        {
            // List<TreeItem<Object>> childItems = task.get();
            // TreeModificationEvent<Object> treeEvent = new TreeModificationEvent<>(TreeItem.childrenModificationEvent(), accountItem);
            // Event.fireEvent(accountItem, treeEvent);
        });

        setOnFailed(event ->
        {
            Throwable th = getException();

            PIMApplication.LOGGER.error(null, th);

            Alert alert = new Alert(AlertType.ERROR, th.getMessage());
            alert.showAndWait();
        });

        return task;
    }
}
