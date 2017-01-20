// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.model.MailAccount;

/**
 * Basis-Implementierung des {@link IMailAccountService}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailAccountService implements IMailAccountService
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
    private final IMailService mailService;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailAccountService}
     *
     * @param account {@link MailAccount}
     * @param basePath {@link Path}
     * @param mailService {@link IMailService}
     */
    public AbstractMailAccountService(final MailAccount account, final Path basePath, final IMailService mailService)
    {
        super();

        Objects.requireNonNull(account, "account required");
        Objects.requireNonNull(basePath, "basePath required");

        this.account = account;
        this.basePath = basePath;
        this.mailService = mailService;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAccountService#getAccount()
     */
    @Override
    public MailAccount getAccount()
    {
        return this.account;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAccountService#getBasePath()
     */
    @Override
    public Path getBasePath()
    {
        return this.basePath;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAccountService#setExecutor(java.util.concurrent.Executor)
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
     * Darf auch null sein !
     *
     * @return {@link IMailService}
     */
    protected IMailService getMailService()
    {
        return this.mailService;
    }
}
