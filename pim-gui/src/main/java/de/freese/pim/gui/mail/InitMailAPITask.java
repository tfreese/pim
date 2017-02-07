// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.gui.PIMApplication;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Initialisierung der MailAPI pro {@link MailAccount}.
 *
 * @author Thomas Freese
 */
public class InitMailAPITask extends Task<List<MailFolder>>
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(InitMailAPITask.class);

    /**
     *
     */
    private final MailAccount account;

    /**
     *
     */
    private final IMailService mailService;

    /**
     * Erzeugt eine neue Instanz von {@link InitMailAPITask}
     *
     * @param treeView {@link TreeView}
     * @param parent {@link TreeItem}
     * @param mailService {@link IMailService}
     * @param account {@link MailAccount}
     */
    public InitMailAPITask(final TreeView<Object> treeView, final TreeItem<Object> parent, final IMailService mailService, final MailAccount account)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(parent, "parent required");
        Objects.requireNonNull(mailService, "mailService required");
        Objects.requireNonNull(account, "account required");

        this.mailService = mailService;
        this.account = account;

        setOnSucceeded(event -> {
            List<MailFolder> folders = getValue();

            account.getFolderSubscribed().addListener(new TreeFolderListChangeListener(parent));
            account.getFolder().addAll(folders);

            LOGGER.info("Initialisation of {} finished", account.getMail());
            treeView.refresh();

            // Laden der Mails.
            PIMApplication.getExecutorService().execute(new LoadMailsTask(treeView, folders, mailService, account));
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
    protected List<MailFolder> call() throws Exception
    {
        LOGGER.info("Init MailAccount {}", this.account.getMail());

        this.mailService.connectAccount(this.account);

        // PIMApplication.registerCloseable(() ->
        // {
        // PIMApplication.LOGGER.info("Close " + this.mailAPI.getAccount().getMail());
        // this.mailAPI.disconnect();
        // });

        List<MailFolder> folders = this.mailService.loadFolder(this.account.getID());

        return folders;
    }
}
