// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.gui.view.ErrorDialog;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;
import de.freese.pim.server.mail.service.IMailService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeView;

/**
 * Laden der Mails pro {@link MailFolder}.
 *
 * @author Thomas Freese
 */
public class LoadMailsTask extends Task<Void>
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(LoadMailsTask.class);

    /**
    *
    */
    private final MailAccount account;

    /**
     *
     */
    private final List<MailFolder> folders;

    /**
    *
    */
    private final IMailService mailService;

    /**
     * Erzeugt eine neue Instanz von {@link LoadMailsTask}
     *
     * @param treeView {@link TreeView}
     * @param folders {@link List}
     * @param mailService {@link IMailService}
     * @param account {@link MailAccount}
     */
    public LoadMailsTask(final TreeView<Object> treeView, final List<MailFolder> folders, final IMailService mailService, final MailAccount account)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(folders, "mailFolder required");
        Objects.requireNonNull(mailService, "mailService required");
        Objects.requireNonNull(account, "account required");

        this.folders = folders;
        this.mailService = mailService;
        this.account = account;

        setOnSucceeded(event -> {
            treeView.refresh();
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
    protected Void call() throws Exception
    {
        for (MailFolder mf : this.folders)
        {
            List<Mail> mails = this.mailService.loadMails(mf.getAccountID(), mf.getID(), mf.getFullName());

            LOGGER.info("Load Mails finished: account={}, folder={}", this.account.getMail(), mf.getFullName());

            Platform.runLater(() -> mf.getMails().addAll(mails));
        }

        return null;
    }
}
