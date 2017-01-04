// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Objects;

/**
 * Basis-Implementierung eines {@link IMailAccount}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailAccount implements IMailAccount
{
    /**
     *
     */
    protected static final boolean DEBUG = true;

    /**
     *
     */
    private MailConfig mailConfig = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailAccount}
     */
    public AbstractMailAccount()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getName()
     */
    @Override
    public String getName()
    {
        return getMailConfig().getMail();
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#init(de.freese.pim.core.mail.model.MailConfig)
     */
    @Override
    public void init(final MailConfig mailConfig) throws Exception
    {
        Objects.requireNonNull(mailConfig, "mailConfig required");

        this.mailConfig = mailConfig;
    }

    /**
     * @return {@link MailConfig}
     */
    protected MailConfig getMailConfig()
    {
        return this.mailConfig;
    }
}
