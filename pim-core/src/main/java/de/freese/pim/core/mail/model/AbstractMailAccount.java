// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.Objects;

import de.freese.pim.core.service.SettingService;

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
     * @see de.freese.pim.core.mail.model.IMailAccount#connect(de.freese.pim.core.mail.model.MailConfig)
     */
    @Override
    public void connect(final MailConfig mailConfig)
    {
        Objects.requireNonNull(mailConfig, "mailConfig required");

        this.mailConfig = mailConfig;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getMailConfig()
     */
    @Override
    public MailConfig getMailConfig()
    {
        return this.mailConfig;
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
     * @see de.freese.pim.core.mail.model.IMailAccount#getPath()
     */
    @Override
    public Path getPath()
    {
        return SettingService.getInstance().getHome().resolve(getName());
    }
}
