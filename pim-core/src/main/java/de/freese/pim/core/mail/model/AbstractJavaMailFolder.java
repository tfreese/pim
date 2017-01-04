// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Objects;

import javax.mail.Folder;

/**
 * Basis-Implementierung eines JavaMail {@link IMailFolder}.
 *
 * @author Thomas Freese
 * @param <A> Konkreter MailAccount
 */
public abstract class AbstractJavaMailFolder<A extends IMailAccount> extends AbstractMailFolder<A>
{
    /**
    *
    */
    private final Folder folder;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJavaMailFolder}
     *
     * @param mailAccount {@link IMailAccount}
     * @param folder {@link Folder}
     */
    public AbstractJavaMailFolder(final A mailAccount, final Folder folder)
    {
        super(mailAccount);

        Objects.requireNonNull(folder, "folder required");

        this.folder = folder;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getName()
     */
    @Override
    public String getName()
    {
        return getFolder().getName();
    }

    /**
     * @return {@link Folder}
     */
    protected Folder getFolder()
    {
        return this.folder;
    }
}
