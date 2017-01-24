// Created: 23.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
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

import com.sun.mail.imap.IMAPFolder;

import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * JavaMail-Implementierung des {@link IMailAPI}.
 *
 * @author Thomas Freese
 */
public class JavaMailAPI extends AbstractMailAPI
{
    /**
     *
     */
    private final FilteredList<MailFolder> abonnierteFolder;

    /**
    *
    */
    private Session session = null;

    /**
    *
    */
    private Store store = null;

    /**
     * Erzeugt eine neue Instanz von {@link JavaMailAPI}
     *
     * @param account {@link MailAccount}
     * @param basePath {@link Path}
     */
    public JavaMailAPI(final MailAccount account, final Path basePath)
    {
        super(account, basePath);

        this.abonnierteFolder = new FilteredList<>(account.getFolder(), MailFolder::isAbonniert);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#connect()
     */
    @Override
    public void connect() throws Exception
    {
        this.session = createSession();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#disconnect()
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

        disconnect(this.store);
        // disconnect(transport);

        this.store = null;
        // transport = null;

        this.session = null;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#getAbonnierteFolder()
     */
    @Override
    public FilteredList<MailFolder> getAbonnierteFolder() throws Exception
    {
        if (this.abonnierteFolder.isEmpty())
        {
            getFolder();
        }

        return this.abonnierteFolder;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#getFolder()
     */
    @Override
    public ObservableList<MailFolder> getFolder() throws Exception
    {
        ObservableList<MailFolder> folder = getAccount().getFolder();

        if (folder.isEmpty())
        {
            Callable<Void> callable = () ->
            {
                // Folder aus DB laden.
                // @formatter:off
                getMailService().getMailFolder(getAccount().getID()).stream()
                    .peek(mf -> mf.setMailAPI(this))
                    .forEach(folder::add);
                // @formatter:on

                if (folder.isEmpty())
                {
                    // Noch keine Folder-Abonnenten gepflegt.
                    Folder root = getStore().getDefaultFolder();

                    // @formatter:off
                    Stream.of(root.list("*"))
                        .map(f -> {
                            MailFolder mf = new MailFolder();
                            mf.setMailAPI(this);
                            mf.setFullName(f.getFullName());
                            mf.setName(f.getName());
                            mf.setAbonniert(true);

                            return mf;
                        })
                        .forEach(folder::add);
                    // @formatter:on
                }

                updateUnreadMailsCount(this.abonnierteFolder);

                return null;
            };

            getExecutor().submit(callable);
        }

        return folder;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#getNewMails(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    public List<Mail> getNewMails(final MailFolder folder) throws Exception
    {
        return Collections.emptyList();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#getUnreadMailsCount()
     */
    @Override
    public int getUnreadMailsCount()
    {
        ObservableList<MailFolder> folder = getAccount().getFolder();

        if (folder.isEmpty())
        {
            return 0;
        }

        int sum = 0;

        // Reverse
        for (int i = folder.size() - 1; i >= 0; i--)
        {
            sum += folder.get(i).getUnreadMailsCount();
        }

        // int sum = folder.stream().mapToInt(MailFolder::getUnreadMailsCount).sum();

        return sum;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadMails(de.freese.pim.core.mail.model.MailFolder, java.util.function.Consumer)
     */
    @Override
    public void loadMails(final MailFolder folder, final Consumer<Mail> consumer) throws Exception
    {
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

        // List<Message> messages = Arrays.asList(msgs);
        //
        // if (!folder.isSendFolder())
        // {
        // Collections.sort(messages, Comparator.comparing(Message::getReceivedDate).reversed());
        // }
        // else
        // {
        // Collections.sort(messages, Comparator.comparing(Message::getSentDate).reversed());
        // }

        for (Message message : msgs)
        {
            Mail mail = new Mail(folder);
            populate(mail, message);

            consumer.accept(mail);
        }

        closeFolder(f);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#testConnection()
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
        String host = getAccount().getImapHost();
        int port = getAccount().getImapPort().getPort();
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

        if (getExecutor() != null)
        {
            properties.put("mail.event.executor", getExecutor());
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
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected Store getStore() throws MessagingException
    {
        if ((this.store != null) && !this.store.isConnected())
        {
            connect(this.store);
        }

        if (this.store == null)
        {
            this.store = createStore(getSession());
            connect(this.store);
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
        InternetAddress to = Optional.ofNullable(message.getRecipients(RecipientType.TO)).map(t -> (InternetAddress) t[0]).orElse(null);
        String subject = message.getSubject();
        Date receivedDate = message.getReceivedDate();
        Date sendDate = message.getSentDate();
        boolean isSeen = message.isSet(Flags.Flag.SEEN);
        int msgNum = message.getMessageNumber();

        String uid = null;

        if (message.getFolder() instanceof IMAPFolder)
        {
            uid = Long.toString(((IMAPFolder) message.getFolder()).getUID(message));
        }

        if (uid == null)
        {
            uid = Optional.ofNullable(message.getHeader("Message-ID")).map(h -> h[0]).orElse(null);
            // uid = message.getHeader("Message-ID")[0];
        }

        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setReceivedDate(receivedDate);
        mail.setSendDate(sendDate);
        mail.setSeen(isSeen);
        mail.setUID(uid);
        mail.setMsgNum(msgNum);
    }

    /**
     * Aktualisiert den Zähler nicht gelsener Mails.
     *
     * @param folders {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected void updateUnreadMailsCount(final List<MailFolder> folders) throws Exception
    {
        if (folders.isEmpty())
        {
            return;
        }

        for (MailFolder mf : folders)
        {
            Folder f = getStore().getFolder(mf.getFullName());
            checkRead(f);

            mf.setUnreadMailsCount(f.getUnreadMessageCount());

            closeFolder(f);
        }
    }
}
