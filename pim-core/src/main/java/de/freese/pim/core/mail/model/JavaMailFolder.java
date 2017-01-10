// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import javax.mail.Folder;

/**
 * IMAP-Implementierung eines {@link IMailFolder}.
 *
 * @author Thomas Freese
 */
public class JavaMailFolder extends AbstractJavaMailFolder<AbstractJavaMailAccount>
{
    /**
     * Erzeugt eine neue Instanz von {@link JavaMailFolder}
     *
     * @param mailAccount {@link AbstractJavaMailAccount}
     * @param folder {@link Folder}
     */
    public JavaMailFolder(final AbstractJavaMailAccount mailAccount, final Folder folder)
    {
        super(mailAccount, folder);
    }
}
