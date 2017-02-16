// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.gui.PIMApplication;
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
    public InitMailAPITask(final TreeView<Object> treeView, final TreeItem<Object> parent, final FXMailService mailService,
            final FXMailAccount account)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(parent, "parent required");
        Objects.requireNonNull(mailService, "mailService required");
        Objects.requireNonNull(account, "account required");

        this.mailService = mailService;
        this.account = account;

        setOnSucceeded(event ->
        {
            List<FXMailFolder> folders = getValue();

            account.getFolderSubscribed().addListener(new TreeFolderListChangeListener(parent));
            account.getFolder().addAll(folders);

            LOGGER.info("Initialisation of {} finished", account.getMail());

            Platform.runLater(() -> treeView.refresh());

            // for (FXMailFolder mf : folders)
            // {
            // // Laden der Mails.
            // PIMApplication.getExecutorService()
            // .execute(new LoadMailsTask(treeView, Collections.singletonList(mf), mailService, account));
            // }

            List<List<FXMailFolder>> partitions = ListUtils.partition(folders, folders.size());

            for (List<FXMailFolder> partition : partitions)
            {
                // Laden der Mails.
                PIMApplication.getExecutorService().execute(new LoadMailsTask(treeView, partition, mailService, account));
            }

            // try
            // {
            // Map<FXMailFolder, Future<List<FXMail>>> map = new LinkedHashMap<>();
            //
            // for (FXMailFolder mf : folders)
            // {
            // Future<List<FXMail>> future = this.mailService.loadMails2(mf.getAccountID(), mf.getID(), mf.getFullName());
            // map.put(mf, future);
            // }
            //
            // for (Entry<FXMailFolder, Future<List<FXMail>>> entry : map.entrySet())
            // {
            // FXMailFolder mf = entry.getKey();
            // Future<List<FXMail>> future = entry.getValue();
            //
            // List<FXMail> mails = future.get();
            // LOGGER.info("Load Mails finished: account={}, folder={}", this.account.getMail(), mf.getFullName());
            //
            // mf.getMails().addAll(mails);
            // }
            // }
            // catch (Exception ex)
            // {
            // throw new RuntimeException(ex);
            // }
        });
        setOnFailed(event ->
        {
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
}
