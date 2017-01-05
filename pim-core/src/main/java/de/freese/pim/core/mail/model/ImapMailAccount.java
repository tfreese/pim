// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

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
     * @see de.freese.pim.core.mail.model.AbstractJavaMailAccount#connectStore(javax.mail.Session)
     */
    @Override
    protected Store connectStore(final Session session) throws MessagingException
    {
        Store store = session.getStore("imaps");
        store.connect(getMailConfig().getImapHost(), getMailConfig().getImapPort(), getMailConfig().getMail(),
                getMailConfig().getPassword());

        return store;
    }
}
