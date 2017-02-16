// Created: 23.01.2017
package de.freese.pim.server.mail.api;

import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.server.mail.model.MailAccount;

/**
 * Basis-Implementierung der {@link MailAPI}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailAPI implements MailAPI
{
    /**
     *
     */
    private final MailAccount account;

    /**
    *
    */
    private ExecutorService executor = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailAPI}
     *
     * @param account {@link MailAccount}
     */
    public AbstractMailAPI(final MailAccount account)
    {
        super();

        Objects.requireNonNull(account, "account required");

        this.account = account;
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#getAccount()
     */
    @Override
    public MailAccount getAccount()
    {
        return this.account;
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#setExecutorService(java.util.concurrent.ExecutorService)
     */
    @Override
    public void setExecutorService(final ExecutorService executor)
    {
        this.executor = executor;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaMailAPI [").append(getAccount()).append("]");

        return builder.toString();
    }
    /**
     * @see de.freese.pim.server.mail.api.MailAPI#loadMail(java.lang.String, long, java.io.OutputStream)
     */
    @Override
    public void loadMail(final String folderFullName, final long uid, final OutputStream outputStream) throws Exception
    {
        loadMail(folderFullName, uid, message -> ((MimeMessage) message).writeTo(outputStream));
    }
    /**
     * Optionaler {@link ExecutorService} für die Mail-API.
     *
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutor()
    {
        return this.executor;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
