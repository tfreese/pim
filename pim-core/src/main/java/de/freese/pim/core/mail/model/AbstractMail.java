// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Objects;

/**
 * Basis-Implementierung einer {@link IMail}.
 *
 * @author Thomas Freese
 * @param <F> Konkreter MailFolder
 */
public abstract class AbstractMail<F extends IMailFolder> implements IMail
{
    /**
    *
    */
    private final F mailFolder;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMail}
     *
     * @param mailFolder {@link IMailFolder}
     */
    public AbstractMail(final F mailFolder)
    {
        super();

        Objects.requireNonNull(mailFolder, "mailFolder required");

        this.mailFolder = mailFolder;
    }

    /**
     * @return {@link IMailFolder}
     */
    protected F getMailFolder()
    {
        return this.mailFolder;
    }
}
