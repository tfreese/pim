// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

import de.freese.pim.core.spring.SpringContext;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;
import de.freese.pim.gui.mail.service.FxMailService;
import de.freese.pim.gui.view.ErrorDialog;

/**
 * Initialisierung der MailApi pro MailAccount.
 *
 * @author Thomas Freese
 */
public class InitMailApiTask extends Task<List<FxMailFolder>> {
    public static final Logger LOGGER = LoggerFactory.getLogger(InitMailApiTask.class);

    private final FxMailAccount account;

    private final FxMailService mailService;

    public InitMailApiTask(final TreeView<Object> treeView, final TreeItem<Object> parent, final FxMailService mailService, final FxMailAccount account) {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(parent, "parent required");

        this.mailService = Objects.requireNonNull(mailService, "mailService required");
        this.account = Objects.requireNonNull(account, "account required");

        setOnSucceeded(event -> {
            final List<FxMailFolder> folders = getValue();

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
            final Throwable th = getException();

            LOGGER.error(th.getMessage(), th);

            new ErrorDialog().forThrowable(th).showAndWait();
        });
    }

    @Override
    protected List<FxMailFolder> call() throws Exception {
        LOGGER.info("Init MailAccount {}", this.account.getMail());

        this.mailService.connectAccount(this.account);

        return this.mailService.loadFolder(this.account.getID());
    }

    protected void loadMailsByCompletableFuture(final List<FxMailFolder> folders, final TreeView<Object> treeView) {
        final AsyncTaskExecutor taskExecutor = SpringContext.getAsyncTaskExecutor();
        CompletableFuture<Void> master = CompletableFuture.completedFuture(null);

        for (FxMailFolder mf : folders) {
            // @formatter:off
            final CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
                this.mailService.loadMails(this.account, mf)
            , taskExecutor)
            .exceptionally(ex ->
            {
                LOGGER.error(ex.getMessage(), ex);
                new ErrorDialog().forThrowable(ex).showAndWait();
                return null;
            })
            .thenAccept(mails ->
            {
                final Runnable task = () -> {
                    if (mails != null) {
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

    protected void loadMailsByParallelStream(final List<FxMailFolder> folders, final TreeView<Object> treeView) {
        // @formatter:off
        try(Stream<FxMailFolder> stream = folders.parallelStream()) {
            stream.onClose(() -> Platform.runLater(treeView::refresh))
            .forEach(mf -> {
                final List<FxMail> mails = this.mailService.loadMails(this.account, mf);

                final Runnable task = () -> {
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

    protected void loadMailsByPartitions(final List<FxMailFolder> folders, final TreeView<Object> treeView) {
        final AsyncTaskExecutor taskExecutor = SpringContext.getAsyncTaskExecutor();

        final int partitionSize = Math.max(1, (folders.size() / 3) + 1); // Jeweils 3 Stores pro MailApi.

        // List<List<FxMailFolder>> partitions = ListUtils.partition(folders, partitionSize);

        // Nachteil: Die Reihenfolge der Elemente ist hinüber.
        final Map<Integer, List<FxMailFolder>> partitionMap = new HashMap<>();

        for (int i = 0; i < folders.size(); i++) {
            final FxMailFolder value = folders.get(i);
            final int indexToUse = i % partitionSize;

            partitionMap.computeIfAbsent(indexToUse, key -> new ArrayList<>()).add(value);
        }

        final Collection<List<FxMailFolder>> partitions = partitionMap.values();

        for (List<FxMailFolder> partition : partitions) {
            // Utils.executeSafely(() -> TimeUnit.MILLISECONDS.sleep(1000));

            // Laden der Mails.
            taskExecutor.execute(new LoadMailsTask(treeView, partition, this.mailService, this.account));
        }
    }
}
