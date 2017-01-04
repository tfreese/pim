// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Folder;
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
     * @see de.freese.pim.core.mail.model.AbstractJavaMailAccount#connectStore(javax.mail.Session)
     */
    @Override
    protected Store connectStore(final Session session) throws MessagingException
    {
        Store store = session.getStore("imaps");
        store.connect(getMailConfig().getImapHost(), getMailConfig().getImapPort(), getMailConfig().getMail(), getMailConfig().getPassword());

        return store;
    }

    /**
     * @see de.freese.pim.core.mail.model.AbstractJavaMailAccount#connectTransport(javax.mail.Session)
     */
    @Override
    protected Transport connectTransport(final Session session) throws MessagingException
    {
        Transport transport = session.getTransport("smtp");
        transport.connect(getMailConfig().getSmtpHost(), getMailConfig().getSmtpPort(), getMailConfig().getMail(), getMailConfig().getPassword());

        return transport;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getTopLevelFolder()
     */
    @Override
    public List<IMailFolder> getTopLevelFolder() throws Exception
    {
        Folder root = getStore().getDefaultFolder();

        // @formatter:off
        List<IMailFolder> folder = Stream.of(root.list("%"))
            .map(f -> new ImapMailFolder(this, f))
            .collect(Collectors.toList());
        // @formatter:on

        return folder;
    }
}
