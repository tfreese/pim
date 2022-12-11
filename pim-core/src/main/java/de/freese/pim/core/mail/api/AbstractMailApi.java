// Created: 23.01.2017
package de.freese.pim.core.mail.api;

import java.util.Objects;
import java.util.concurrent.Executor;

import de.freese.pim.core.model.mail.MailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung der {@link MailApi}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailApi implements MailApi
{
    private final MailAccount account;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Executor executor;

    protected AbstractMailApi(final MailAccount account)
    {
        super();

        this.account = Objects.requireNonNull(account, "account required");
    }

    /**
     * @see MailApi#getAccount()
     */
    @Override
    public MailAccount getAccount()
    {
        return this.account;
    }

    /**
     * @see MailApi#setExecutor(java.util.concurrent.Executor)
     */
    @Override
    public void setExecutor(final Executor executor)
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
        builder.append("JavaMailApi [").append(getAccount()).append("]");

        return builder.toString();
    }

    /**
     * Optionaler {@link Executor} für die Mail-API.
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }
}
