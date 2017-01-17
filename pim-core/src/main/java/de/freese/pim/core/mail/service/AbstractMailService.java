// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.MailAccount;

/**
 * Basis-Implementierung des {@link IMailService}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailService implements IMailService
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
    private Executor executor = null;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final IMailDAO mailDAO;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailService}
     *
     * @param account {@link MailAccount}
     * @param basePath {@link Path}
     * @param mailDAO {@link IMailDAO}
     */
    public AbstractMailService(final MailAccount account, final Path basePath, final IMailDAO mailDAO)
    {
        super();

        Objects.requireNonNull(account, "account required");
        Objects.requireNonNull(basePath, "basePath required");
        Objects.requireNonNull(mailDAO, "mailDAO required");

        this.account = account;
        this.basePath = basePath;
        this.mailDAO = mailDAO;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getAccount()
     */
    @Override
    public MailAccount getAccount()
    {
        return this.account;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getBasePath()
     */
    @Override
    public Path getBasePath()
    {
        return this.basePath;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#setExecutor(java.util.concurrent.Executor)
     */
    @Override
    public void setExecutor(final Executor executor)
    {
        this.executor = executor;
    }

    /**
     * Optionaler {@link Executor} für die Mail-API.
     *
     * @return {@link Executor}
     */
    protected Executor getExecutor()
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

    /**
     * @return {@link IMailDAO}
     */
    protected IMailDAO getMailDAO()
    {
        return this.mailDAO;
    }
}
