/**
 * Created: 01.01.2017
 */

package de.freese.pim.gui.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailAccountService;
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
public class InitMailAccountService extends Service<Void>
{
    /**
     *
     */
    private static final Logger LOGGER = PIMApplication.LOGGER;

    /**
    *
    */
    private final IMailAccountService mailAccountService;

    /**
    *
    */
    private final TreeItem<Object> root;

    /**
     * Erstellt ein neues {@link InitMailAccountService} Object.
     *
     * @param root {@link TreeItem}
     * @param mailAccountService {@link IMailAccountService}
     */
    public InitMailAccountService(final TreeItem<Object> root, final IMailAccountService mailAccountService)
    {
        super();

        Objects.requireNonNull(root, "root item required");
        Objects.requireNonNull(mailAccountService, "mailAccountService required");

        this.root = root;
        this.mailAccountService = mailAccountService;

        setExecutor(PIMApplication.getExecutorService());
    }

    /**
     * @return {@link IMailAccountService}
     */
    private IMailAccountService getMailAccountService()
    {
        return this.mailAccountService;
    }

    /**
     * @return {@link TreeItem}<Object>
     */
    private TreeItem<Object> getRoot()
    {
        return this.root;
    }

    /**
     * Laden der Child-Hierarchie.
     *
     * @param parent {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    private List<TreeItem<Object>> loadChildFolders(final MailFolder parent) throws Exception
    {
        List<MailFolder> childs = getMailAccountService().getChilds(parent);

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
                Platform.runLater(() ->
                {
                    getRoot().setExpanded(true);
                });

                if (StringUtils.isBlank(getMailAccountService().getAccount().getPassword()))
                {
                    throw new Exception("empty password for {}" + getMailAccountService().getAccount().getMail());
                }

                getMailAccountService().connect();

                List<MailFolder> rootFolders = getMailAccountService().getRootFolder();

                for (MailFolder rootFolder : rootFolders)
                {
                    TreeItem<Object> treeItem = new TreeItem<>(rootFolder);

                    Platform.runLater(() ->
                    {
                        getRoot().getChildren().add(treeItem);
                    });

                    treeItem.getChildren().addAll(loadChildFolders(rootFolder));
                }

                PIMApplication.LOGGER.info("Initialisation of {} finished", getMailAccountService().getAccount().getMail());

                return null;
            }
        };

        setOnSucceeded(event ->
        {
            LOGGER.info("Start Synchronisation of {}", getMailAccountService().getAccount().getMail());

            SyncMailFolderService service = new SyncMailFolderService(getRoot(), getMailAccountService());
            service.start();
        });

        setOnFailed(event ->
        {
            Throwable th = getException();

            LOGGER.error(null, th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });

        return task;
    }
}
