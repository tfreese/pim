// Created: 23.01.2017
package de.freese.pim.core.mail.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import de.freese.pim.core.function.ExceptionalFunction;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.utils.MailUtils;
import de.freese.pim.core.utils.Utils;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.core.utils.io.NestedIOMonitor;
import org.slf4j.LoggerFactory;

/**
 * JavaMail-Implementierung des {@link MailApi}.
 *
 * @author Thomas Freese
 */
public class JavaMailApi extends AbstractMailApi
{
    /**
     * Generisches Callback-Interface für einen Mail-{@link Folder}.
     *
     * @param <T> Konkreter Return-Typ
     *
     * @author Thomas Freese
     */
    @FunctionalInterface
    private interface FolderCallback<T>
    {
        /**
         * @param folder {@link IMAPFolder}
         *
         * @return Object
         *
         * @throws Exception Falls was schiefgeht.
         */
        T doInFolder(IMAPFolder folder) throws Exception;
    }

    /**
     *
     */
    private Session session;
    /**
     *
     */
    private int storeIndex;
    /**
     * Für RoundRobin-Pool.
     */
    private final IMAPStore[] stores = new IMAPStore[Math.max(1, 3)];

    /**
     * Erzeugt eine neue Instanz von {@link JavaMailApi}
     *
     * @param account {@link MailAccount}
     */
    public JavaMailApi(final MailAccount account)
    {
        super(account);
    }

    /**
     * Stellt sicher, das der {@link Folder} zum Lesen geöffnet ist.
     *
     * @param folder {@link Folder}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected void checkRead(final Folder folder) throws MessagingException
    {
        if (!folder.isOpen())
        {
            folder.open(Folder.READ_ONLY);
        }
    }

    /**
     * Stellt sicher, das der {@link Folder} zum Schreiben geöffnet ist.
     *
     * @param folder {@link Folder}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected void checkWrite(final Folder folder) throws MessagingException
    {
        if (!folder.isOpen() || (folder.getMode() == Folder.READ_ONLY))
        {
            folder.open(Folder.READ_WRITE);
        }
    }

    /**
     * Schliesst den {@link Folder} mit close(true).
     *
     * @param folder {@link Folder}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected void closeFolder(final Folder folder) throws MessagingException
    {
        if (folder == null)
        {
            return;
        }

        if (folder.isOpen())
        {
            folder.close(true);
        }
    }

    /**
     * @see MailApi#connect()
     */
    @Override
    public void connect()
    {
        this.session = Utils.executeSafely(this::createSession);
    }

    /**
     * Connecten des {@link Service}.
     *
     * @param service {@link Service}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected void connect(final Service service) throws MessagingException
    {
        String host = service instanceof Store ? getAccount().getImapHost() : getAccount().getSmtpHost();
        int port = service instanceof Store ? getAccount().getImapPort().getPort() : getAccount().getSmtpPort().getPort();

        String mail = getAccount().getMail();
        String password = getAccount().getPassword();

        service.connect(host, port, mail, password);
    }

    /**
     * Erzeugt die Mail-Session.
     *
     * @return {@link Session}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected Session createSession() throws MessagingException
    {
        Authenticator authenticator = null;

        Properties properties = new Properties();

        // if (getLogger().isDebugEnabled())
        if (LoggerFactory.getLogger("javax.mail").isDebugEnabled())
        {
            properties.setProperty("mail.debug", "true");
        }

        // Legitimation für Empfang.
        if (getAccount().isImapLegitimation())
        {
            properties.setProperty("mail.imap.auth", "true");
            properties.setProperty("mail.imap.starttls.enable", "true");
        }

        // Legitimation für Versand.
        if (getAccount().isSmtpLegitimation())
        {
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
        }

        if (getExecutor() != null)
        {
            properties.put("mail.event.executor", getExecutor());
        }

        properties.setProperty("mail.mime.base64.ignoreerrors", "true");

        // properties.setProperty("mail.imap.connectionpoolsize", "10");
        properties.setProperty("mail.imap.fetchsize", "1048576"); // 1MB, Long.toString(1024 * 1024)
        // properties.setProperty("mail.imap.partialfetch", "false");

        // properties.setProperty("mail.imaps.connectionpoolsize", "10");
        properties.setProperty("mail.imaps.fetchsize", "1048576"); // 1MB, Long.toString(1024 * 1024)
        // properties.setProperty("mail.imaps.partialfetch", "false");

        return Session.getInstance(properties, authenticator);
    }

    /**
     * Erzeugt den {@link Store}.
     *
     * @param session {@link Session}
     *
     * @return {@link Store}
     *
     * @throws NoSuchProviderException Falls was schiefgeht.
     */
    protected IMAPStore createStore(final Session session) throws NoSuchProviderException
    {
        return (IMAPStore) this.session.getStore("imaps");
    }

    /**
     * Erzeugt den {@link Transport}.
     *
     * @param session {@link Session}
     *
     * @return {@link Transport}
     *
     * @throws NoSuchProviderException Falls was schiefgeht.
     */
    protected Transport createTransport(final Session session) throws NoSuchProviderException
    {
        return this.session.getTransport("smtp");
    }

    /**
     * @see MailApi#disconnect()
     */
    @Override
    public void disconnect()
    {
        // Folder schliessen.
        // if (this.topLevelFolder != null)
        // {
        // for (IMailFolder folder : this.topLevelFolder)
        // {
        // folder.close();
        // }
        // }
        // if (getFolder().isOpen())
        // {
        // getFolder().close(true);
        // }

        // disconnect(this.store);
        // disconnect(transport);
        for (int i = 0; i < this.stores.length; i++)
        {
            Store store = this.stores[i];

            try
            {
                disconnect(store);
            }
            catch (Exception ex)
            {
                getLogger().error(ex.getMessage(), ex);
            }

            this.stores[i] = null;
        }

        // this.store = null;
        // transport = null;
        this.session = null;
    }

    /**
     * Schliessen des {@link Service}.
     *
     * @param service {@link Service}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected void disconnect(final Service service) throws MessagingException
    {
        if ((service != null) && service.isConnected())
        {
            service.close();
        }
    }

    /**
     * Führt die Action in dem Folder aus.
     *
     * @param <T> Konkreter Return-Typ
     * @param folderFullName String
     * @param action {@link FolderCallback}
     *
     * @return Object
     */
    protected <T> T executeInFolder(final String folderFullName, final FolderCallback<T> action)
    {
        return Utils.executeSafely(() ->
        {
            IMAPFolder folder = (IMAPFolder) getStore().getFolder(folderFullName);

            if (folder == null)
            {
                getLogger().warn("Folder {} not exist", folderFullName);

                return null;
            }

            if ((folder.getType() & Folder.HOLDS_MESSAGES) == 0)
            {
                getLogger().warn("Folder {} can not contain messges", folderFullName);

                return null;
            }

            try
            {
                checkRead(folder);

                return action.doInFolder(folder);
            }
            finally
            {
                closeFolder(folder);
            }
        });
    }

    /**
     * @see MailApi#getFolder()
     */
    @Override
    public List<MailFolder> getFolder()
    {
        return Utils.executeSafely(() ->
        {
            Folder root = getStore().getDefaultFolder();

            // @formatter:off
            return Stream.of(root.list("*"))
                    .map(f ->
                    {
                        MailFolder mf = new MailFolder();
                        mf.setFullName(f.getFullName());
                        mf.setName(f.getName());
                        mf.setAbonniert(true);
                        return mf;
                    })
                    .toList();
            // @formatter:on

            // closeFolder(root);
        });
    }

    /**
     * @return {@link Session}
     */
    protected Session getSession()
    {
        return this.session;
    }

    /**
     * Liefert einen {@link Store} mit dem Round-Robin Verfahren.
     *
     * @return {@link Store}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected synchronized IMAPStore getStore() throws MessagingException
    {
        IMAPStore store = this.stores[this.storeIndex++];

        if ((store != null) && !store.isConnected())
        {
            connect(store);
        }

        if (store == null)
        {
            store = createStore(getSession());
            connect(store);
            this.stores[this.storeIndex - 1] = store;
        }

        if (this.storeIndex == this.stores.length)
        {
            this.storeIndex = 0;
        }

        return store;
    }

    /**
     * @see MailApi#loadMail(java.lang.String, long, de.freese.pim.core.function.ExceptionalFunction)
     */
    @Override
    public <T> T loadMail(final String folderFullName, final long uid, final ExceptionalFunction<Object, T, Exception> function)
    {
        return executeInFolder(folderFullName, folder ->
        {
            MimeMessage message = (MimeMessage) folder.getMessageByUID(uid);
            preFetch(folder, message);

            return function.apply(message);
        });
    }

    /**
     * @see MailApi#loadMail(java.lang.String, long, de.freese.pim.core.utils.io.IOMonitor)
     */
    @Override
    public MailContent loadMail(final String folderFullName, final long uid, final IOMonitor monitor)
    {
        return executeInFolder(folderFullName, folder ->
        {
            MimeMessage mimeMessage = (MimeMessage) folder.getMessageByUID(uid);
            preFetch(folder, mimeMessage);

            MailContent mailContent = null;

            if (monitor == null)
            {
                mailContent = new JavaMailContent(mimeMessage);
            }
            else
            {
                int sizeMessage = mimeMessage.getSize();
                long sizeAllParts = MailUtils.getSizeOfAllParts(mimeMessage);
                long size = Math.max(sizeMessage, sizeAllParts);

                NestedIOMonitor nestendIOMonitor = new NestedIOMonitor(monitor, size);

                mailContent = new JavaMailContent(mimeMessage, nestendIOMonitor);

                // try (FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1024))
                // {
                // // OutputStream gos = new GZIPOutputStream(baos);
                // try (OutputStream mos = new MonitorOutputStream(baos, size, monitor))
                // {
                // mimeMessage.writeTo(mos);
                // }
                //
                // // baos.close();
                //
                // // byte[] data = baos.toByteArray();
                //
                // // try (InputStream is = new ByteArrayInputStream(data);
                // // InputStream gis = new GZIPInputStream(is)
                // try (InputStream is = baos.getInputStream();)
                // {
                // message = new MimeMessage(null, is);
                // mailContent = new JavaMailContent(mimeMessage);
                // }
                // }
            }

            return mailContent;
        });
    }

    /**
     * @see MailApi#loadMails(java.lang.String, long)
     */
    @Override
    public List<Mail> loadMails(final String folderFullName, final long uidFrom)
    {
        List<Mail> mails = executeInFolder(folderFullName, folder ->
        {
            long uidTo = folder.getUIDNext();
            // SearchTerm searchTerm = new SearchTerm()
            // {
            // /**
            // * @see javax.mail.search.SearchTerm#match(javax.mail.Message)
            // */
            // @Override
            // public boolean match(final Message msg)
            // {
            // if (msg.getMessageNumber() > maxMsgNum)
            // {
            // return true;
            // }
            //
            // return false;
            // }
            // };

            // Message[] msgs = folder.search(searchTerm);
            Message[] msgs = folder.getMessagesByUID(uidFrom, uidTo);
            preFetch(folder, msgs);

            List<Mail> newMails = new ArrayList<>();

            for (Message message : msgs)
            {
                Mail mail = new Mail();
                populate(mail, message);

                newMails.add(mail);
            }

            return newMails;
        });

        return mails != null ? mails : Collections.emptyList();
    }

    /**
     * @see MailApi#loadMessageIDs(java.lang.String)
     */
    @Override
    public Set<Long> loadMessageIDs(final String folderFullName)
    {
        Set<Long> ids = executeInFolder(folderFullName, folder ->
        {
            Message[] msgs = folder.getMessages();

            FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            folder.fetch(msgs, fp);

            Set<Long> uids = new HashSet<>();

            for (Message msg : msgs)
            {
                uids.add(folder.getUID(msg));
            }

            return uids;
        });

        return ids != null ? ids : Collections.emptySet();
    }

    /**
     * Befüllt die {@link Mail} mit den Inhalten der {@link Message}.
     *
     * @param mail {@link Mail}
     * @param message {@link Message}
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    protected void populate(final Mail mail, final Message message) throws MessagingException
    {
        InternetAddress from = Optional.ofNullable(message.getFrom()).map(f -> (InternetAddress) f[0]).orElse(null);
        InternetAddress[] to = (InternetAddress[]) message.getRecipients(RecipientType.TO);
        InternetAddress[] cc = (InternetAddress[]) message.getRecipients(RecipientType.CC);
        InternetAddress[] bcc = (InternetAddress[]) message.getRecipients(RecipientType.BCC);

        String subject = message.getSubject();
        Date receivedDate = message.getReceivedDate();
        Date sendDate = message.getSentDate();
        boolean isSeen = message.isSet(Flag.SEEN);
        int msgNum = message.getMessageNumber();
        int size = message.getSize();

        IMAPFolder f = (IMAPFolder) message.getFolder();
        long uid = f.getUID(message);

        // if ((uid == 3066) || (uid == 576) || (uid == 60))
        // {
        // // Debug Problemfälle.
        // System.out.println();
        // }
        // if (message.getFolder() instanceof IMAPFolder)
        // {
        // uid = ((IMAPFolder) message.getFolder()).getUID(message);
        // }
        // else
        // {
        // throw new MessagingException("IMAPFolder required");
        // }
        // if (uid == null)
        // {
        // uid = Optional.ofNullable(message.getHeader("Message-ID")).map(h -> h[0]).orElse(null);
        // // uid = message.getHeader("Message-ID")[0];
        // }
        mail.setFrom(MailUtils.map(from));
        mail.setTo(MailUtils.map(to));
        mail.setCc(MailUtils.map(cc));
        mail.setBcc(MailUtils.map(bcc));
        mail.setSubject(subject);
        mail.setReceivedDate(receivedDate);
        mail.setSendDate(sendDate);
        mail.setSeen(isSeen);
        mail.setUID(uid);
        mail.setMsgNum(msgNum);
        mail.setSize(size);
    }

    /**
     * Bestimmte Mail-Attribute vorladen.
     *
     * @param folder {@link Folder}
     * @param messages {@link Message}[]
     *
     * @throws MessagingException Falls was schiefgeht.
     */
    private void preFetch(final Folder folder, final Message... messages) throws MessagingException
    {
        FetchProfile fp = new FetchProfile();
        fp.add(IMAPFolder.FetchProfileItem.HEADERS);
        fp.add(UIDFolder.FetchProfileItem.UID);
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.SIZE);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        // fp.add("X-Mailer");

        folder.fetch(messages, fp);
    }

    /**
     * @see MailApi#setSeen(de.freese.pim.core.model.mail.Mail, boolean)
     */
    @Override
    public void setSeen(final Mail mail, final boolean seen)
    {
        executeInFolder(mail.getFolderFullName(), folder ->
        {
            // Bulk-Operation auf Server.
            folder.setFlags(new int[]
                    {
                            mail.getMsgNum()
                    }, new Flags(Flags.Flag.SEEN), seen);

            // Einzel-Operation auf Server.
            // Message message = f.getMessage(mail.getMsgNum());
            // message.setFlag(Flag.SEEN, seen);

            return null;
        });
    }

    /**
     * @see MailApi#testConnection()
     */
    @Override
    public void testConnection()
    {
        Utils.executeSafely(() ->
        {
            // Test Connection Empfang.
            Store s = createStore(this.session);
            connect(s);
            disconnect(s);
            s = null;

            // Test Connection Versand.
            Transport t = createTransport(this.session);
            connect(t);
            disconnect(t);
            t = null;
        });
    }
}
