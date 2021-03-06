// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import de.freese.pim.common.spring.SpringContext;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.gui.mail.service.FXMailService;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Initialisierung der MailAPI pro MailAccount.
 *
 * @author Thomas Freese
 */
public class InitMailAPITask extends Task<List<FXMailFolder>>
{
    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(InitMailAPITask.class);

    /**
     *
     */
    private final FXMailAccount account;

    /**
     *
     */
    private final FXMailService mailService;

    /**
     * Erzeugt eine neue Instanz von {@link InitMailAPITask}
     *
     * @param treeView {@link TreeView}
     * @param parent {@link TreeItem}
     * @param mailService {@link FXMailService}
     * @param account {@link FXMailAccount}
     */
    public InitMailAPITask(final TreeView<Object> treeView, final TreeItem<Object> parent, final FXMailService mailService, final FXMailAccount account)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(parent, "parent required");

        this.mailService = Objects.requireNonNull(mailService, "mailService required");
        this.account = Objects.requireNonNull(account, "account required");

        setOnSucceeded(event -> {
            List<FXMailFolder> folders = getValue();

            account.getFolderSubscribed().addListener(new TreeFolderListChangeListener(parent));
            account.getFolder().addAll(folders);

            LOGGER.info("Initialisation of {} finished", account.getMail());

            treeView.refresh();
            // Platform.runLater(() -> treeView.refresh());
            loadMailsByPartitions(account.getFolderSubscribed(), treeView);
            // loadMailsByCompletableFuture(account.getFolderSubscribed(), treeView);
            // loadMailsByParallelStream(account.getFolderSubscribed(), treeView);
        });

        setOnFailed(event -> {
            Throwable th = getException();

            LOGGER.error(null, th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });
    }

    /**
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected List<FXMailFolder> call() throws Exception
    {
        LOGGER.info("Init MailAccount {}", this.account.getMail());

        this.mailService.connectAccount(this.account);

        List<FXMailFolder> folders = this.mailService.loadFolder(this.account.getID());

        return folders;
    }

    /**
     * @param folders {@link List}
     * @param treeView {@link TreeView}
     */
    protected void loadMailsByCompletableFuture(final List<FXMailFolder> folders, final TreeView<Object> treeView)
    {
        AsyncTaskExecutor taskExecutor = SpringContext.getAsyncTaskExecutor();
        CompletableFuture<Void> master = CompletableFuture.completedFuture(null);

        for (FXMailFolder mf : folders)
        {
            // @formatter:off
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
                this.mailService.loadMails(this.account, mf)
            , taskExecutor)
            .exceptionally(ex ->
            {
                LOGGER.error(null, ex);
                new ErrorDialog().forThrowable(ex).showAndWait();
                return null;
            })
            .thenAccept(mails ->
            {
                Runnable task = ()->
                {
                    if (mails != null)
                    {
                        mf.getMails().addAll(mails);
                    }
                };

//                task.run();
                Platform.runLater(task);
            });
            // @formatter:on

            // Merge
            master = CompletableFuture.allOf(master, cf);
        }

        master.thenAccept(result -> Platform.runLater(treeView::refresh));
    }

    /**
     * @param folders {@link List}
     * @param treeView {@link TreeView}
     */
    protected void loadMailsByParallelStream(final List<FXMailFolder> folders, final TreeView<Object> treeView)
    {
        // @formatter:off
        try(Stream<FXMailFolder> stream =folders.parallelStream())
        {
            stream.onClose(() -> Platform.runLater(treeView::refresh))
            .forEach(mf ->
            {
                List<FXMail> mails = this.mailService.loadMails(this.account, mf);

                Runnable task = ()->
                {
                    if (mails != null)
                    {
                        mf.getMails().addAll(mails);
                    }
                };

                Platform.runLater(task);
            });
        }
        // @formatter:on
    }

    /**
     * @param folders {@link List}
     * @param treeView {@link TreeView}
     */
    protected void loadMailsByPartitions(final List<FXMailFolder> folders, final TreeView<Object> treeView)
    {
        AsyncTaskExecutor taskExecutor = SpringContext.getAsyncTaskExecutor();

        int partitionSize = Math.max(1, (folders.size() / 3) + 1); // Jeweils 3 Stores pro MailAPI.
        // partitionSize = folders.size();
        List<List<FXMailFolder>> partitions = ListUtils.partition(folders, partitionSize);

        for (List<FXMailFolder> partition : partitions)
        {
            // Utils.executeSafely(() -> Thread.sleep(1000));

            // Laden der Mails.
            taskExecutor.execute(new LoadMailsTask(treeView, partition, this.mailService, this.account));
        }
    }
}
