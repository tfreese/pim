// Created: 05.01.2017
package de.freese.pim.core.mail.model;

import javax.mail.Message;

/**
 * IMAP-Implementierung einer {@link IMail}.
 *
 * @author Thomas Freese
 */
public class JavaMail extends AbstractJavaMail<IMailFolder>
{
    /**
     * Erzeugt eine neue Instanz von {@link JavaMail}
     *
     * @param mailFolder {@link IMailFolder}
     * @param message {@link Message}
     */
    public JavaMail(final IMailFolder mailFolder, final Message message)
    {
        super(mailFolder, message);
    }
}
