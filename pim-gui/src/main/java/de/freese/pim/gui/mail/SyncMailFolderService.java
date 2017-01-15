/**
 * Created: 01.01.2017
 */

package de.freese.pim.gui.mail;

import java.util.Iterator;
import java.util.Objects;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@link Service} zu Synchronisieren der Folder-Hierarchie.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class SyncMailFolderService extends Service<Void>
{
    /**
    *
    */
    private final IMailService mailService;

    /**
    *
    */
    private final TreeItem<Object> root;

    /**
     * Erstellt ein neues {@link SyncMailFolderService} Object.
     *
     * @param root {@link TreeItem}
     * @param mailService {@link IMailService}
     */
    public SyncMailFolderService(final TreeItem<Object> root, final IMailService mailService)
    {
        super();

        Objects.requireNonNull(root, "root item required");
        Objects.requireNonNull(mailService, "mailService required");

        this.root = root;
        this.mailService = mailService;

        setExecutor(PIMApplication.getExecutorService());
    }

    /**
     * Hinzufügen des neuen Folders.
     *
     * @param parent {@link TreeItem}
     * @param folder {@link MailFolder}
     */
    private void addFolder(final TreeItem<Object> parent, final MailFolder folder)
    {
        Platform.runLater(() -> parent.getChildren().add(new TreeItem<Object>(folder)));
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
                for (TreeItem<Object> childItem : getRoot().getChildren())
                {
                    MailFolder folder = (MailFolder) childItem.getValue();

                    syncChildFolder(childItem, folder);
                }

                PIMApplication.LOGGER.info("Synchronisation of {} finished", SyncMailFolderService.this.mailService.getAccount().getMail());

                return null;
            }
        };

        setOnSucceeded(event -> {
        });

        setOnFailed(event -> {
            Throwable th = getException();

            PIMApplication.LOGGER.error(null, th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });

        return task;
    }

    /**
     * @return {@link TreeItem}
     */
    private TreeItem<Object> getRoot()
    {
        return this.root;
    }

    /**
     * Hinzufügen des neuen Folders.
     *
     * @param parent {@link TreeItem}
     * @param name {@link MailFolder}
     */
    private void removeFolder(final TreeItem<Object> parent, final String name)
    {
        Platform.runLater(() -> {
            for (Iterator<TreeItem<Object>> iterator = parent.getChildren().iterator(); iterator.hasNext();)
            {
                TreeItem<Object> child = iterator.next();
                MailFolder folder = (MailFolder) child.getValue();

                if (folder.getName().equals(name))
                {
                    iterator.remove();
                    break;
                }
            }
        });
    }

    /**
     * Synchronisiert die Folder-Hierarchie.
     *
     * @param treeItem {@link TreeItem}
     * @param parent {@link MailFolder}
     * @throws Exception Falls was schief geht.
     */
    private void syncChildFolder(final TreeItem<Object> treeItem, final MailFolder parent) throws Exception
    {
        this.mailService.syncChildFolder(parent, mf -> addFolder(treeItem, mf), name -> removeFolder(treeItem, name));

        for (TreeItem<Object> childItem : treeItem.getChildren())
        {
            MailFolder folder = (MailFolder) childItem.getValue();

            syncChildFolder(childItem, folder);
        }
    }
}
