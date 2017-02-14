// Created: 23.01.2017
package de.freese.pim.server.mail.api;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

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
    private final Path basePath;

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
     * @param basePath {@link Path}
     */
    public AbstractMailAPI(final MailAccount account, final Path basePath)
    {
        super();

        Objects.requireNonNull(account, "account required");
        Objects.requireNonNull(basePath, "basePath required");

        this.account = account;
        this.basePath = basePath;
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
     * @see de.freese.pim.server.mail.api.MailAPI#getBasePath()
     */
    @Override
    public Path getBasePath()
    {
        return this.basePath;
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
