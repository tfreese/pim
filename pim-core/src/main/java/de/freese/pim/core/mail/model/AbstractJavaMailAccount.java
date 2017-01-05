// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Basis-Implementierung eines JavaMail {@link IMailAccount}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractJavaMailAccount extends AbstractMailAccount
{
    /**
    *
    */
    private Session session = null;

    /**
     *
     */
    private Store store = null;

    /**
     *
     */
    private ObservableList<IMailFolder> topLevelFolder = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJavaMailAccount}
     */
    public AbstractJavaMailAccount()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.model.AbstractMailAccount#connect(de.freese.pim.core.mail.model.MailConfig)
     */
    @Override
    public void connect(final MailConfig mailConfig) throws Exception
    {
        super.connect(mailConfig);

        this.session = createSession();
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#disconnect()
     */
    @Override
    public void disconnect() throws Exception
    {
        // Folder schliessen.
        for (IMailFolder folder : getTopLevelFolder())
        {
            folder.close();
        }

        disconnectStore(this.store);
        // disconnectTransport(transport);

        this.store = null;
        // transport = null;

        this.topLevelFolder.clear();
        this.topLevelFolder = null;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getTopLevelFolder()
     */
    @Override
    public ObservableList<IMailFolder> getTopLevelFolder() throws Exception
    {
        if (this.topLevelFolder == null)
        {
            Folder root = getStore().getDefaultFolder();

            // @formatter:off
            this.topLevelFolder = Stream.of(root.list("%"))
                .map(f -> new MailFolder(this, f))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            // @formatter:on
        }

        return this.topLevelFolder;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getUnreadMessageCount()
     */
    @Override
    public int getUnreadMessageCount() throws Exception
    {
        int count = getTopLevelFolder().stream().mapToInt(f ->
        {
            try
            {
                return f.getUnreadMessageCount();
            }
            catch (Exception ex)
            {
                // Ignore
            }

            return 0;
        }).sum();

        return count;
    }

    /**
     * Erzeugt die Mail-Session.
     *
     * @return {@link Session}
     * @throws MessagingException Falls was schief geht.
     */
    private Session createSession() throws MessagingException
    {
        Authenticator authenticator = null;

        Properties properties = new Properties();

        if (DEBUG)
        {
            properties.put("mail.debug", Boolean.TRUE.toString());
        }

        // Legitimation für Empfang.
        if (getMailConfig().isImapLegitimation())
        {
            properties.put("mail.imap.auth", "true");
            properties.put("mail.imap.starttls.enable", "true");
        }

        // Legitimation für Versand.
        if (getMailConfig().isSmtpLegitimation())
        {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
        }

        if (getMailConfig().getExecutor() != null)
        {
            properties.put("mail.event.executor", getMailConfig().getExecutor());
        }

        Session session = Session.getInstance(properties, authenticator);

        // Test Connection Empfang.
        Store s = connectStore(session);
        disconnectStore(s);
        s = null;

        // Test Connection Versand.
        Transport t = connectTransport(session);
        disconnectTransport(t);
        t = null;

        return session;
    }

    /**
     * Connecten des {@link Store}.
     *
     * @param session {@link Session}
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected abstract Store connectStore(final Session session) throws MessagingException;

    /**
     * Connecten des {@link Transport}.
     *
     * @param session {@link Session}
     * @return {@link Transport}
     * @throws MessagingException Falls was schief geht.
     */
    protected Transport connectTransport(final Session session) throws MessagingException
    {
        Transport transport = session.getTransport("smtp");
        transport.connect(getMailConfig().getSmtpHost(), getMailConfig().getSmtpPort(), getMailConfig().getMail(),
                getMailConfig().getPassword());

        return transport;
    }

    /**
     * Schliessen des {@link Store}.
     *
     * @param store {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected void disconnectStore(final Store store) throws MessagingException
    {
        if ((store != null) && store.isConnected())
        {
            store.close();
        }
    }

    /**
     * Schliessen des {@link Store}.
     *
     * @param transport {@link Transport}
     * @throws MessagingException Falls was schief geht.
     */
    protected void disconnectTransport(final Transport transport) throws MessagingException
    {
        if ((transport != null) && transport.isConnected())
        {
            transport.close();
        }
    }

    /**
     * @return {@link Session}
     */
    protected Session getSession()
    {
        return this.session;
    }

    /**
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected Store getStore() throws MessagingException
    {
        if ((this.store != null) && !this.store.isConnected())
        {
            // disconnectStore(this.store);
            this.store = null;
        }

        if (this.store == null)
        {
            this.store = connectStore(getSession());

            this.store.addFolderListener(new FolderListener()
            {
                /**
                 * @see javax.mail.event.FolderListener#folderCreated(javax.mail.event.FolderEvent)
                 */
                @Override
                public void folderCreated(final FolderEvent e)
                {
                    ObservableList<IMailFolder> list = AbstractJavaMailAccount.this.topLevelFolder;

                    if (list == null)
                    {
                        return;
                    }

                    list.add(new MailFolder(AbstractJavaMailAccount.this, e.getFolder()));
                }

                /**
                 * @see javax.mail.event.FolderListener#folderDeleted(javax.mail.event.FolderEvent)
                 */
                @Override
                public void folderDeleted(final FolderEvent e)
                {
                    ObservableList<IMailFolder> list = AbstractJavaMailAccount.this.topLevelFolder;

                    if (list == null)
                    {
                        return;
                    }

                    Folder folder = e.getFolder();
                    IMailFolder mf = list.stream().filter(f -> f.getFullName().equals(folder.getFullName())).findFirst().get();

                    if (mf != null)
                    {
                        AbstractJavaMailAccount.this.topLevelFolder.remove(mf);
                    }
                }

                /**
                 * @see javax.mail.event.FolderListener#folderRenamed(javax.mail.event.FolderEvent)
                 */
                @Override
                public void folderRenamed(final FolderEvent e)
                {
                    ObservableList<IMailFolder> list = AbstractJavaMailAccount.this.topLevelFolder;

                    if (list == null)
                    {
                        return;
                    }

                    Folder folderOld = e.getFolder();

                    IMailFolder mf = list.stream().filter(f -> f.getFullName().equals(folderOld.getFullName())).findFirst().get();

                    if (mf != null)
                    {
                        list.remove(mf);
                    }

                    Folder folderNew = e.getNewFolder();

                    if (folderNew != null)
                    {
                        list.add(new MailFolder(AbstractJavaMailAccount.this, folderNew));
                    }
                }
            });
        }

        return this.store;
    }
}
