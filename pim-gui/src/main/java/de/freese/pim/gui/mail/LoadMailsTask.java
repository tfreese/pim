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
import javafx.concurrent.Task;

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
    private final MailFolder mailFolder;

    /**
     * Erzeugt eine neue Instanz von {@link LoadMailsTask}
     *
     * @param mailFolder {@link MailFolder}
     */
    public LoadMailsTask(final MailFolder mailFolder)
    {
        super();

        Objects.requireNonNull(mailFolder, "mailFolder required");

        this.mailFolder = mailFolder;

        setOnSucceeded(event ->
        {
            this.mailFolder.getMails().addAll(getValue());

            LOGGER.info("Load Mails finished: account={}, folder={}", this.mailFolder.getMailAPI().getAccount().getMail(),
                    mailFolder.getFullName());
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
    protected List<Mail> call() throws Exception
    {
        IMailAPI mailAPI = this.mailFolder.getMailAPI();

        LOGGER.info("Load Mails: account={}, folder={}", mailAPI.getAccount().getMail(), this.mailFolder.getFullName());

        List<Mail> mails = mailAPI.loadMails(this.mailFolder);

        return mails;
    }
}
