// Created: 23.01.2017
package de.freese.pim.server.mail.api;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import de.freese.pim.common.function.ExceptionalFunction;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
import de.freese.pim.common.utils.io.NestendIOMonitor;
import de.freese.pim.server.mail.MailUtils;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;

/**
 * JavaMail-Implementierung des {@link MailAPI}.
 *
 * @author Thomas Freese
 */
public class JavaMailAPI extends AbstractMailAPI
{
    /**
     *
     */
    private Session session = null;

    /**
     *
     */
    private int storeIndex = 0;

    /**
     * Für RoundRobin-Pool.
     */
    private final IMAPStore[] stores = new IMAPStore[Math.max(1, 3)];

    /**
     * Erzeugt eine neue Instanz von {@link JavaMailAPI}
     *
     * @param account {@link MailAccount}
     */
    public JavaMailAPI(final MailAccount account)
    {
        super(account);
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#connect()
     */
    @Override
    public void connect() throws Exception
    {
        this.session = createSession();
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#disconnect()
     */
    @Override
    public void disconnect() throws Exception
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
            disconnect(store);

            this.stores[i] = null;
        }

        // this.store = null;
        // transport = null;
        this.session = null;
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#getFolder()
     */
    @Override
    public List<MailFolder> getFolder() throws Exception
    {
        Folder root = getStore().getDefaultFolder();

        // @formatter:off
        List<MailFolder> folder = Stream.of(root.list("*"))
                .map(f ->
                {
                    MailFolder mf = new MailFolder();
                    mf.setFullName(f.getFullName());
                    mf.setName(f.getName());
                    mf.setAbonniert(true);
                    return mf;
                })
                .collect(Collectors.toList());
        // @formatter:on

        // closeFolder(root);

        return folder;
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#loadCurrentMessageIDs(java.lang.String)
     */
    @Override
    public Set<Long> loadCurrentMessageIDs(final String folderFullName) throws Exception
    {
        IMAPFolder f = (IMAPFolder) getStore().getFolder(folderFullName);
        checkRead(f);

        if (f == null)
        {
            getLogger().warn("Folder {} not exist", folderFullName);

            return Collections.emptySet();
        }

        try
        {
            Message[] msgs = f.getMessages();

            FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            f.fetch(msgs, fp);

            Set<Long> uids = new HashSet<>();

            for (Message msg : msgs)
            {
                uids.add(f.getUID(msg));
            }

            return uids;
        }
        finally
        {
            closeFolder(f);
        }
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#loadMail(java.lang.String, long, de.freese.pim.common.function.ExceptionalFunction)
     */
    @Override
    public <T> T loadMail(final String folderFullName, final long uid, final ExceptionalFunction<Object, T, Exception> function)
            throws Exception
    {
        IMAPFolder f = (IMAPFolder) getStore().getFolder(folderFullName);
        checkRead(f);

        try
        {
            MimeMessage message = (MimeMessage) f.getMessageByUID(uid);
            preFetch(f, message);

            return function.apply(message);
        }
        finally
        {
            closeFolder(f);
        }
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#loadMail(java.lang.String, long, de.freese.pim.common.utils.io.IOMonitor)
     */
    @Override
    public MailContent loadMail(final String folderFullName, final long uid, final IOMonitor monitor) throws Exception
    {
        return loadMail(folderFullName, uid, message ->
        {
            MimeMessage mimeMessage = (MimeMessage) message;
            MailContent mailContent = null;

            if (monitor == null)
            {
                mailContent = new JavaMailContent(mimeMessage);
            }
            else
            {
                int size = mimeMessage.getSize();
                NestendIOMonitor nestendIOMonitor = new NestendIOMonitor(monitor, size);

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
     * @see de.freese.pim.server.mail.api.MailAPI#loadMail(java.lang.String, long, java.io.OutputStream)
     */
    @Override
    public void loadMail(final String folderFullName, final long uid, final OutputStream outputStream) throws Exception
    {
        loadMail(folderFullName, uid, message ->
        {
            ((MimeMessage) message).writeTo(outputStream);
            return null;
        });
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#loadMails(java.lang.String, long)
     */
    @Override
    public List<Mail> loadMails(final String folderFullName, final long uidFrom) throws Exception
    {
        // Mails von Provider laden, die eine höhere MsgNum/UID als die bereits vorhanden haben.
        IMAPFolder f = (IMAPFolder) getStore().getFolder(folderFullName);

        if (f == null)
        {
            getLogger().warn("Folder {} not exist", folderFullName);

            return null;
        }

        checkRead(f);

        try
        {
            long uidTo = f.getUIDNext();
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

            // Message[] msgs = f.search(searchTerm);
            Message[] msgs = f.getMessagesByUID(uidFrom, uidTo);
            preFetch(f, msgs);

            List<Mail> newMails = new ArrayList<>();

            for (Message message : msgs)
            {
                Mail mail = new Mail();
                populate(mail, message);

                newMails.add(mail);
            }

            return newMails;
        }
        finally
        {
            closeFolder(f);
        }
    }

    // /**
    // * @see de.freese.pim.core.mail.api.IMailAPI#loadNewMails(de.freese.pim.core.mail.model.MailFolder)
    // */
    // @Override
    // public List<Mail> loadNewMails(final MailFolder folder) throws Exception
    // {
    // this.semaphore.acquire();
    //
    // try
    // {
    // Folder f = getStore().getFolder(folder.getFullName());
    // checkRead(f);
    //
    // SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
    // Message[] msgs = f.search(searchTerm);
    // preFetch(f, msgs);
    //
    // if (getLogger().isDebugEnabled())
    // {
    // getLogger().debug("new mails: number={}", msgs.length);
    // }
    //
    // List<Mail> mails = new ArrayList<>();
    //
    // for (Message message : msgs)
    // {
    // Mail mail = new Mail();
    // populate(mail, message);
    //
    // mails.add(mail);
    // }
    //
    // closeFolder(f);
    //
    // return mails;
    // }
    // finally
    // {
    // this.semaphore.release();
    // }
    // }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#setSeen(de.freese.pim.server.mail.model.Mail, boolean)
     */
    @Override
    public void setSeen(final Mail mail, final boolean seen) throws Exception
    {
        Folder f = getStore().getFolder(mail.getFolderFullName());
        checkRead(f);

        try
        {
            // Bulk-Operation auf Server.
            f.setFlags(new int[]
            {
                    mail.getMsgNum()
            }, new Flags(Flags.Flag.SEEN), seen);

            // Einzel-Operation auf Server.
            // Message message = f.getMessage(mail.getMsgNum());
            // message.setFlag(Flag.SEEN, seen);
        }
        finally
        {
            closeFolder(f);
        }
    }

    /**
     * @see de.freese.pim.server.mail.api.MailAPI#testConnection()
     */
    @Override
    public void testConnection() throws Exception
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
    }

    /**
     * Bestimmte Mail-Attribute vorladen.
     *
     * @param folder {@link Folder}
     * @param messages {@link Message}[]
     * @throws MessagingException Falls was schief geht.
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
     * Stellt sicher, das der {@link Folder} zum Lesen geöffnet ist.
     *
     * @param folder {@link Folder}
     * @throws Exception Falls was schief geht.
     */
    protected void checkRead(final Folder folder) throws Exception
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
     * @throws Exception Falls was schief geht.
     */
    protected void checkWrite(final Folder folder) throws Exception
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
     * @throws MessagingException Falls was schief geht.
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
     * Connecten des {@link Service}.
     *
     * @param service {@link Service}
     * @throws MessagingException Falls was schief geht.
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
     * @throws MessagingException Falls was schief geht.
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

        Session session = Session.getInstance(properties, authenticator);

        return session;
    }

    /**
     * Erzeugt den {@link Store}.
     *
     * @param session {@link Session}
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected IMAPStore createStore(final Session session) throws MessagingException
    {
        return (IMAPStore) this.session.getStore("imaps");
    }

    /**
     * Erzeugt den {@link Transport}.
     *
     * @param session {@link Session}
     * @return {@link Transport}
     * @throws MessagingException Falls was schief geht.
     */
    protected Transport createTransport(final Session session) throws MessagingException
    {
        return this.session.getTransport("smtp");
    }

    /**
     * Schliessen des {@link Service}.
     *
     * @param service {@link Service}
     * @throws MessagingException Falls was schief geht.
     */
    protected void disconnect(final Service service) throws MessagingException
    {
        if ((service != null) && service.isConnected())
        {
            service.close();
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
     * Liefert einen {@link Store} mit dem Round-Robin Verfahren.
     *
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
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
     * Befüllt die {@link Mail} mit den Inhalten der {@link Message}.
     *
     * @param mail {@link Mail}
     * @param message {@link Message}
     * @throws MessagingException Falls was schief geht.
     */
    protected void populate(final Mail mail, final Message message) throws MessagingException
    {
        InternetAddress from = Optional.ofNullable(message.getFrom()).map(f -> (InternetAddress) f[0]).orElse(null);
        InternetAddress[] to = (InternetAddress[]) Optional.ofNullable(message.getRecipients(RecipientType.TO)).orElse(null);
        InternetAddress[] cc = (InternetAddress[]) Optional.ofNullable(message.getRecipients(RecipientType.CC)).orElse(null);
        InternetAddress[] bcc = (InternetAddress[]) Optional.ofNullable(message.getRecipients(RecipientType.BCC)).orElse(null);

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
}
