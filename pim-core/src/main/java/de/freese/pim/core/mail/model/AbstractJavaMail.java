// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Objects;

import javax.mail.Message;

/**
 * Basis-Implementierung einer JavaMail {@link IMail}.
 *
 * @author Thomas Freese
 * @param <F> Konkreter MailFolder
 */
public abstract class AbstractJavaMail<F extends IMailFolder> extends AbstractMail<F>
{
    /**
     *
     */
    private final Message message;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJavaMail}
     *
     * @param mailFolder {@link IMailFolder}
     * @param message {@link Message}
     */
    public AbstractJavaMail(final F mailFolder, final Message message)
    {
        super(mailFolder);

        Objects.requireNonNull(message, "message required");

        this.message = message;
    }

    /**
     * @return {@link Message}
     */
    protected Message getMessage()
    {
        return this.message;
    }
}
