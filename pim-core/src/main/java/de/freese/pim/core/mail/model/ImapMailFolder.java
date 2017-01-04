// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Folder;

/**
 * IMAP-Implementierung eines {@link IMailFolder}.
 *
 * @author Thomas Freese
 */
public class ImapMailFolder extends AbstractJavaMailFolder<ImapMailAccount>
{
    /**
     * Erzeugt eine neue Instanz von {@link ImapMailFolder}
     *
     * @param mailAccount {@link ImapMailAccount}
     * @param folder {@link Folder}
     */
    public ImapMailFolder(final ImapMailAccount mailAccount, final Folder folder)
    {
        super(mailAccount, folder);
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getChildren()
     */
    @Override
    public List<IMailFolder> getChildren() throws Exception
    {
        // @formatter:off
        List<IMailFolder> children = Stream.of(getFolder().list("%"))
            .map(f -> new ImapMailFolder(getMailAccount(), f))
            .collect(Collectors.toList());
        // @formatter:on

        return children;
    }
}
