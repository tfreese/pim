// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Objects;

/**
 * Basis-Implementierung eines {@link IMailFolder}.
 *
 * @author Thomas Freese
 * @param <A> Konkreter MailAccount
 */
public abstract class AbstractMailFolder<A extends IMailAccount> implements IMailFolder
{
    /**
     *
     */
    private final A mailAccount;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailFolder}
     *
     * @param mailAccount {@link IMailAccount}
     */
    public AbstractMailFolder(final A mailAccount)
    {
        super();

        Objects.requireNonNull(mailAccount, "mailAccount required");

        this.mailAccount = mailAccount;
    }

    /**
     * @return {@link IMailAccount}
     */
    protected A getMailAccount()
    {
        return this.mailAccount;
    }
}
