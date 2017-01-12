// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;

/**
 * Basis-Implementierung eines JavaMail {@link IMailAccount}.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
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
    public void connect(final MailConfig mailConfig)
    {
        super.connect(mailConfig);

        try
        {
            this.session = createSession();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Connecten des {@link Store}.
     *
     * @param store {@link Store}
     *
     * @throws MessagingException Falls was schief geht.
     */
    protected void connectStore(final Store store) throws MessagingException
    {
        store.connect(getMailConfig().getImapHost(), getMailConfig().getImapPort(), getMailConfig().getMail(), getMailConfig().getPassword());
    }

    /**
     * Connecten des {@link Transport}.
     *
     * @param transport {@link Transport}
     *
     * @throws MessagingException Falls was schief geht.
     */
    protected void connectTransport(final Transport transport) throws MessagingException
    {
        transport.connect(getMailConfig().getSmtpHost(), getMailConfig().getSmtpPort(), getMailConfig().getMail(), getMailConfig().getPassword());
    }

    /**
     * Erzeugt die Mail-Session.
     *
     * @return {@link Session}
     *
     * @throws MessagingException Falls was schief geht.
     */
    private Session createSession() throws MessagingException
    {
        Authenticator authenticator = null;

        Properties properties = new Properties();
        properties.put("mail.debug", DEBUG.toString());

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
        Store s = createStore(session);
        connectStore(s);
        disconnectStore(s);
        s = null;

        // Test Connection Versand.
        Transport t = createTransport(session);
        connectTransport(t);
        disconnectTransport(t);
        t = null;

        return session;
    }

    /**
     * Erzeugt den {@link Store}.
     *
     * @param session {@link Session}
     *
     * @return {@link Store}
     *
     * @throws MessagingException Falls was schief geht.
     */
    protected abstract Store createStore(Session session) throws MessagingException;

    /**
     * Erzeugt den {@link Transport}.
     *
     * @param session {@link Session}
     *
     * @return {@link Transport}
     *
     * @throws MessagingException Falls was schief geht.
     */
    protected abstract Transport createTransport(Session session) throws MessagingException;

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#disconnect()
     */
    @Override
    public void disconnect()
    {
        try
        {
            // Folder schliessen.
            if (this.topLevelFolder != null)
            {
                for (IMailFolder folder : this.topLevelFolder)
                {
                    folder.close();
                }
            }

            disconnectStore(this.store);
            // disconnectTransport(transport);

            this.store = null;
            // transport = null;

            this.topLevelFolder.clear();
            this.topLevelFolder = null;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Schliessen des {@link Store}.
     *
     * @param store {@link Store}
     *
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
     *
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
     *
     * @throws MessagingException Falls was schief geht.
     */
    protected Store getStore() throws MessagingException
    {
        if ((this.store != null) && !this.store.isConnected())
        {
            connectStore(this.store);
        }

        if (this.store == null)
        {
            this.store = createStore(getSession());
            connectStore(this.store);

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

                    list.add(new JavaMailFolder(AbstractJavaMailAccount.this, e.getFolder()));
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
                        list.add(new JavaMailFolder(AbstractJavaMailAccount.this, folderNew));
                    }
                }
            });
        }

        return this.store;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getTopLevelFolder()
     */
    @Override
    public ObservableList<IMailFolder> getTopLevelFolder()
    {
        try
        {
            if (this.topLevelFolder == null)
            {
                Folder root = getStore().getDefaultFolder();

                // @formatter:off
                this.topLevelFolder = Stream.of(root.list("%"))
                        .map(f -> new JavaMailFolder(this, f))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                // @formatter:on
            }

            return this.topLevelFolder;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailAccount#getUnreadMessageCount()
     */
    @Override
    public int getUnreadMessageCount()
    {
        if (this.store == null)
        {
            return 0;
        }

        try
        {
            int count = getTopLevelFolder().stream().mapToInt(IMailFolder::getUnreadMessageCount).sum();

            return count;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}