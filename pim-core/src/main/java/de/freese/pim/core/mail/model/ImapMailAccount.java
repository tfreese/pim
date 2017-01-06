// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

/**
 * IMAP-Implementierung eines {@link IMailAccount}.
 *
 * @author Thomas Freese
 */
public class ImapMailAccount extends AbstractJavaMailAccount
{
    /**
     * Erzeugt eine neue Instanz von {@link ImapMailAccount}
     */
    public ImapMailAccount()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.model.AbstractJavaMailAccount#createStore(javax.mail.Session)
     */
    @Override
    protected Store createStore(final Session session) throws MessagingException
    {
        return session.getStore("imaps");
    }

    /**
     * @see de.freese.pim.core.mail.model.AbstractJavaMailAccount#createTransport(javax.mail.Session)
     */
    @Override
    protected Transport createTransport(final Session session) throws MessagingException
    {
        return session.getTransport("smtp");
    }
}
