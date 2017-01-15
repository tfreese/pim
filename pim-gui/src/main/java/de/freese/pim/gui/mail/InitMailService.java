/**
 * Created: 01.01.2017
 */

package de.freese.pim.gui.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@link Service} zu Laden der Folder-Hierarchie.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
public class InitMailService extends Service<Void>
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
     * Erstellt ein neues {@link InitMailService} Object.
     *
     * @param root {@link TreeItem}
     * @param mailService {@link IMailService}
     */
    public InitMailService(final TreeItem<Object> root, final IMailService mailService)
    {
        super();

        Objects.requireNonNull(root, "root item required");
        Objects.requireNonNull(mailService, "mailService required");

        this.root = root;
        this.mailService = mailService;

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
                Platform.runLater(() -> {
                    getRoot().setExpanded(true);
                });

                IMailService mailService = InitMailService.this.mailService;

                mailService.connect();

                List<MailFolder> rootFolders = mailService.getRootFolder();

                for (MailFolder rootFolder : rootFolders)
                {
                    TreeItem<Object> treeItem = new TreeItem<>(rootFolder);

                    Platform.runLater(() -> {
                        getRoot().getChildren().add(treeItem);
                    });

                    treeItem.getChildren().addAll(loadChildFolders(rootFolder));
                }

                PIMApplication.LOGGER.info("Initialisation of {} finished", mailService.getAccount().getMail());

                return null;
            }
        };

        setOnSucceeded(event -> {
            PIMApplication.LOGGER.info("Start Synchronisation of {}", this.mailService.getAccount().getMail());

            SyncMailFolderService service = new SyncMailFolderService(getRoot(), this.mailService);
            service.start();
        });

        setOnFailed(event -> {
            Throwable th = getException();

            PIMApplication.LOGGER.error(null, th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });

        return task;
    }

    /**
     * @return {@link TreeItem}<Object>
     */
    private TreeItem<Object> getRoot()
    {
        return this.root;
    }

    /**
     * Laden der direkten Children, nicht die ganze Hierarchie.
     *
     * @param parent {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    private List<TreeItem<Object>> loadChildFolders(final MailFolder parent) throws Exception
    {
        List<MailFolder> childs = this.mailService.getChilds(parent);

        if (CollectionUtils.isEmpty(childs))
        {
            return Collections.emptyList();
        }

        List<TreeItem<Object>> childItems = new ArrayList<>();

        for (MailFolder child : childs)
        {
            parent.getChilds().add(child);

            TreeItem<Object> childItem = new TreeItem<>(child);
            childItems.add(childItem);

            childItem.getChildren().addAll(loadChildFolders(child));
        }

        return childItems;
    }
}
