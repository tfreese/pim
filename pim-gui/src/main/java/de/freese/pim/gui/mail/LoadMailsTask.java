// Created: 25.01.2017
package de.freese.pim.gui.mail;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailAPI;
import de.freese.pim.gui.view.ErrorDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeView;

/**
 * Laden der Mails pro {@link MailFolder}.
 *
 * @author Thomas Freese
 */
public class LoadMailsTask extends Task<List<Mail>>
{
    /**
    *
    */
    public static final Logger LOGGER = LoggerFactory.getLogger(LoadMailsTask.class);

    /**
     *
     */
    private final List<MailFolder> folders;

    /**
     * Erzeugt eine neue Instanz von {@link LoadMailsTask}
     *
     * @param treeView {@link TreeView}
     * @param folders {@link List}
     */
    public LoadMailsTask(final TreeView<Object> treeView, final List<MailFolder> folders)
    {
        super();

        Objects.requireNonNull(treeView, "treeView required");
        Objects.requireNonNull(folders, "mailFolder required");

        this.folders = folders;

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
    protected List<Mail> call() throws Exception
    {
        for (MailFolder mf : this.folders)
        {
            IMailAPI mailAPI = mf.getMailAPI();

            LOGGER.info("Load Mails: account={}, folder={}", mailAPI.getAccount().getMail(), mf.getFullName());
            List<Mail> mails = mailAPI.loadMails(mf);
            LOGGER.info("Load Mails finished: account={}, folder={}", mailAPI.getAccount().getMail(), mf.getFullName());

            Platform.runLater(() -> mf.getMails().addAll(mails));
        }

        return null;
    }
}
