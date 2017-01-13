// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;

import com.sun.mail.imap.IMAPFolder;

import de.freese.pim.core.mail.model_new.Mail;
import de.freese.pim.core.mail.model_new.MailFolder;
import javafx.collections.ObservableList;

/**
 * JavaMail-Implementierung des {@link IMailService}.
 *
 * @author Thomas Freese
 */
public class JavaMailService extends AbstractMailService
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
     * Erzeugt eine neue Instanz von {@link JavaMailService}
     */
    public JavaMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#connect()
     */
    @Override
    public void connect() throws Exception
    {
        this.session = createSession();

        // Test Connection Empfang.
        Store s = createStore(this.session);
        connectStore(s);
        disconnectStore(s);
        s = null;

        // Test Connection Versand.
        Transport t = createTransport(this.session);
        connectTransport(t);
        disconnectTransport(t);
        t = null;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getMails(de.freese.pim.core.mail.model_new.MailFolder)
     */
    @Override
    public ObservableList<Mail> getMails(final MailFolder folder) throws Exception
    {
        ObservableList<Mail> mails = folder.getMails();

        // TODO Lokaler Cache synchronisieren.
        if (!mails.isEmpty())
        {
            return mails;
        }

        Folder f = getStore().getFolder(folder.getFullName());

        checkRead(f);

        Message[] msgs = f.getMessages();

        // Nur bestimmte Mail-Attribute vorladen.
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(UIDFolder.FetchProfileItem.UID);
        fp.add(IMAPFolder.FetchProfileItem.HEADERS);
        // fp.add(FetchProfile.Item.CONTENT_INFO);

        f.fetch(msgs, fp);

        for (Message message : msgs)
        {
            Mail mail = new Mail(folder);
            populate(mail, message);

            mails.add(mail);
        }

        return mails;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getNewMails(de.freese.pim.core.mail.model_new.MailFolder)
     */
    @Override
    public List<Mail> getNewMails(final MailFolder folder) throws Exception
    {
        return Collections.emptyList();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getRootFolder()
     */
    @Override
    public List<MailFolder> getRootFolder() throws Exception
    {
        // TODO Lokaler Cache synchronisieren.
        // Lokale Folder auslesen.
        // Predicate<Path> isDirectory = Files::isDirectory;
        // Predicate<Path> isHidden = p -> p.getFileName().toString().startsWith(".");
        //
        // Path basePath = getAccount().getPath();
        // Objects.requireNonNull(basePath, "basePath required");
        //
        // Map<String, Path> localMap = Files.list(basePath).filter(isDirectory.negate().and(isHidden.negate()))
        // .collect(Collectors.toMap(p -> p.getFileName().toString(), Function.identity()));

        Folder root = getStore().getDefaultFolder();

        checkRead(root);

        List<MailFolder> parentFolder = Stream.of(root.list("%")).map(f -> new MailFolder(getAccount(), f.getName()))
                .collect(Collectors.toList());

        return parentFolder;
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
     * Connecten des {@link Store}.
     *
     * @param store {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected void connectStore(final Store store) throws MessagingException
    {
        String host = getAccount().getImapHost();
        int port = getAccount().getImapPort();
        String mail = getAccount().getMail();
        String password = getAccount().getPassword();

        store.connect(host, port, mail, password);
    }

    /**
     * Connecten des {@link Transport}.
     *
     * @param transport {@link Transport}
     * @throws MessagingException Falls was schief geht.
     */
    protected void connectTransport(final Transport transport) throws MessagingException
    {
        String host = getAccount().getSmtpHost();
        int port = getAccount().getSmtpPort();
        String mail = getAccount().getMail();
        String password = getAccount().getPassword();

        transport.connect(host, port, mail, password);
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

        if (getLogger().isDebugEnabled())
        {
            properties.put("mail.debug", "true");
        }

        // Legitimation für Empfang.
        if (getAccount().isImapLegitimation())
        {
            properties.put("mail.imap.auth", "true");
            properties.put("mail.imap.starttls.enable", "true");
        }

        // Legitimation für Versand.
        if (getAccount().isSmtpLegitimation())
        {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
        }

        if (getAccount().getExecutor() != null)
        {
            properties.put("mail.event.executor", getAccount().getExecutor());
        }

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
    protected Store createStore(final Session session) throws MessagingException
    {
        return this.session.getStore("imaps");
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
     * @see de.freese.pim.core.mail.service.AbstractMailService#getChildFolder(de.freese.pim.core.mail.model_new.MailFolder)
     */
    @Override
    protected List<MailFolder> getChildFolder(final MailFolder parent) throws Exception
    {
        Folder folder = getStore().getFolder(parent.getFullName());
        checkRead(folder);

        List<MailFolder> childFolder = Stream.of(folder.list("%")).map(f -> new MailFolder(getAccount(), f.getName(), parent))
                .collect(Collectors.toList());

        return childFolder;
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
            connectStore(this.store);
        }

        if (this.store == null)
        {
            this.store = createStore(getSession());
            connectStore(this.store);
        }

        return this.store;
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
        String subject = message.getSubject();
        Date receivedDate = message.getReceivedDate();
        Date sendDate = message.getSentDate();
        boolean isSeen = message.isSet(Flags.Flag.SEEN);

        String id = null;

        if (message.getFolder() instanceof IMAPFolder)
        {
            id = Long.toString(((IMAPFolder) message.getFolder()).getUID(message));
        }

        if (id == null)
        {
            id = Optional.ofNullable(message.getHeader("Message-ID")).map(h -> h[0]).orElse(null);
            id = message.getHeader("Message-ID")[0];
        }

        mail.setFrom(from);
        mail.setSubject(subject);
        mail.setReceivedDate(receivedDate);
        mail.setSendDate(sendDate);
        mail.setSeen(isSeen);
        mail.setID(id);
    }
}