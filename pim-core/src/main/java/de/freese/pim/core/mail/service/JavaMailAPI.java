// Created: 23.01.2017
package de.freese.pim.core.mail.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.activation.DataSource;
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
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;

import de.freese.pim.core.mail.JavaMailBuilder;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.utils.MailContent;
import de.freese.pim.core.mail.utils.MailUtils;
import de.freese.pim.core.utils.io.MonitorOutputStream;
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
     * @see de.freese.pim.core.mail.service.IMailAPI#getFolder()
     */
    @Override
    public ObservableList<MailFolder> getFolder()
    {
        return getAccount().getFolder();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#getFolderSubscribed()
     */
    @Override
    public FilteredList<MailFolder> getFolderSubscribed()
    {
        return this.abonnierteFolder;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#getUnreadMailsCount()
     */
    @Override
    public int getUnreadMailsCount()
    {
        ObservableList<MailFolder> folder = getFolder();

        if (folder.isEmpty())
        {
            return 0;
        }

        // int sum = 0;
        //
        // // Reverse
        // for (int i = folder.size() - 1; i >= 0; i--)
        // {
        // sum += folder.get(i).getUnreadMailsCount();
        // }

        int sum = folder.stream().mapToInt(MailFolder::getUnreadMailsCount).sum();

        return sum;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadFolder(java.util.function.Consumer)
     */
    @Override
    public void loadFolder(final Consumer<MailFolder> consumer) throws Exception
    {
        List<MailFolder> folder = null;

        // Folder aus DB laden.
        if (getMailService() != null)
        {
            folder = getMailService().getMailFolder(getAccount().getID());
        }

        if ((folder == null) || folder.isEmpty())
        {
            // Noch keine Folder-Abonnenten gepflegt.
            Folder root = getStore().getDefaultFolder();

            // @formatter:off
            folder = Stream.of(root.list("*"))
                .map(f -> {
                    MailFolder mf = new MailFolder();
                    mf.setFullName(f.getFullName());
                    mf.setName(f.getName());
                    mf.setAbonniert(true);
                    return mf;
                })
                .collect(Collectors.toList());
            // @formatter:on

            closeFolder(root);
        }

        // Aktualisiert den Zähler nicht gelesener Mails.
        for (Iterator<MailFolder> iterator = folder.iterator(); iterator.hasNext();)
        {
            MailFolder mf = iterator.next();
            mf.setMailAPI(this);

            Folder f = getStore().getFolder(mf.getFullName());

            if (f == null)
            {
                getLogger().warn("Folder {} not exist", mf.getFullName());
                iterator.remove();
                continue;
            }

            // checkRead(f);
            mf.setUnreadMailsCount(f.getUnreadMessageCount());
            closeFolder(f);

            consumer.accept(mf);
        }
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
        FetchProfile fp = createDefaultFetchProfile();
        f.fetch(msgs, fp);

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("loaded mails: number={}", msgs.length);
        }

        for (Message message : msgs)
        {
            Mail mail = new Mail(folder);
            populate(mail, message);

            consumer.accept(mail);
        }

        closeFolder(f);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadNewMails(de.freese.pim.core.mail.model.MailFolder, java.util.function.Consumer)
     */
    @Override
    public void loadNewMails(final MailFolder folder, final Consumer<Mail> consumer) throws Exception
    {
        Folder f = getStore().getFolder(folder.getFullName());
        checkRead(f);

        SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message[] msgs = f.search(searchTerm);

        // Nur bestimmte Mail-Attribute vorladen.
        FetchProfile fp = createDefaultFetchProfile();
        f.fetch(msgs, fp);

        for (Message message : msgs)
        {
            Mail mail = new Mail(folder);
            populate(mail, message);

            consumer.accept(mail);
        }

        closeFolder(f);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadTextContent(de.freese.pim.core.mail.model.Mail, java.util.function.BiConsumer)
     */
    @Override
    public MailContent loadTextContent(final Mail mail, final BiConsumer<Long, Long> loadMonitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(),
                    mail.getSubject());
        }

        Path path = mail.getPath();
        Path htmlContentPath = path.getParent().resolve(mail.getUID() + ".html");
        Path textContentPath = path.getParent().resolve(mail.getUID() + ".txt");

        // Nach bereits erzeugter Content-Datei suchen: *.html oder *.txt
        if (Files.exists(htmlContentPath))
        {
            return new MailContent(htmlContentPath);
        }
        else if (Files.exists(textContentPath))
        {
            return new MailContent(textContentPath);
        }

        Message message = null;

        if (!Files.exists(path))
        {
            // Mail download.
            Files.createDirectories(path.getParent());

            Folder f = getStore().getFolder(mail.getFolder().getFullName());
            checkRead(f);

            if (f instanceof IMAPFolder)
            {
                message = ((IMAPFolder) f).getMessageByUID(Long.parseLong(mail.getUID()));
            }
            else
            {
                message = f.getMessage(mail.getMsgNum());
            }

            // FetchProfile fp = createDefaultFetchProfile();
            // f.fetch(new Message[]
            // {
            // message
            // }, fp);

            // http://stackoverflow.com/questions/8322836/javamail-imap-over-ssl-quite-slow-bulk-fetching-multiple-messages
            // securerandom.source=file:/dev/urandom
            // -Djava.security.egd=file:/dev/urandom
            // Laden der kompletten Mail auf der Client.
            // MimeMessage msg = new MimeMessage((MimeMessage) message);

            // try (OutputStream os = new BufferedOutputStream(new MonitorOutputStream(Files.newOutputStream(path), mail.getSize(),
            // loadMonitor)))
            try (OutputStream os = new MonitorOutputStream(new BufferedOutputStream(Files.newOutputStream(path)), mail.getSize(),
                    loadMonitor))
            // try (OutputStream os = new MonitorOutputStream(Files.newOutputStream(path), mail.getSize(), loadMonitor))
            {
                message.writeTo(os);
            }

            closeFolder(f);
        }

        // Lokale Mail laden.
        try (InputStream is = new BufferedInputStream(Files.newInputStream(path)))
        {
            message = new MimeMessage(null, is);
        }

        // Mail-Inhalt lesen.
        List<DataSource> dataSources = MailUtils.getTextDataSources(message);

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("dataSources: count={}", dataSources.size());
        }

        Optional<DataSource> dataSource = dataSources.stream().filter(ds -> ds.getContentType().equals("text/html")).findFirst();

        if (!dataSource.isPresent())
        {
            // Kein HTML gefunden -> nach Plain-Text suchen.
            dataSource = dataSources.stream().filter(ds -> ds.getContentType().equals("text/plain")).findFirst();
        }

        if (!dataSource.isPresent())
        {
            return null;
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("use dataSource: {}", dataSource.get().getContentType());
        }

        Path contentPath = dataSource.get().getContentType().equals("text/html") ? htmlContentPath : textContentPath;
        Files.copy(dataSource.get().getInputStream(), contentPath);

        MailContent mailContent = new MailContent(dataSource.get());
        mailContent.setUrl(contentPath.toUri().toURL());

        // return mailContent;

        // Bei HTML die Inlines bearbeiten.
        // http://stackoverflow.com/questions/26363573/registering-and-using-a-custom-java-net-url-protocol
        if (mailContent.isHTML())
        {
            String content = mailContent.getContent();

            List<MimeBodyPart> inlines = MailUtils.getInlines(message);

            for (MimeBodyPart inline : inlines)
            {
                String contentID = Optional.ofNullable(inline.getHeader(JavaMailBuilder.HEADER_CONTENT_ID)).map(h -> h[0]).orElse(null);

                if (contentID == null)
                {
                    continue;
                }

                // spring-context-support: org/springframework/mail/javamail/mime.types
                // image/gif
                // image/jpeg

                Path inlinePath = path.getParent().resolve(mail.getUID() + "_" + contentID + ".inline");
                Files.copy(inline.getInputStream(), inlinePath);

                contentID = contentID.replace("<", "");
                contentID = contentID.replace(">", "");

                // src=\"cid:image1\">
                content.replaceAll("cid:" + contentID, inlinePath.getFileName().toString());
            }

            try (PrintWriter pw = new PrintWriter(Files.newOutputStream(contentPath)))
            {
                pw.append(content);
            }
        }

        mailContent = new MailContent(contentPath);

        return mailContent;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#setSeen(de.freese.pim.core.mail.model.Mail, boolean)
     */
    @Override
    public void setSeen(final Mail mail, final boolean seen) throws Exception
    {
        Folder f = getStore().getFolder(mail.getFolder().getFullName());
        checkRead(f);

        // Bulk-Operation auf Server.
        f.setFlags(new int[]
        {
                mail.getMsgNum()
        }, new Flags(Flags.Flag.SEEN), seen);

        // Einzel-Operation auf Server.
        // Message message = f.getMessage(mail.getMsgNum());
        // message.setFlag(Flag.SEEN, seen);

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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaMailAPI [").append(getAccount()).append("]");

        return builder.toString();
    }

    /**
     * @return {@link FetchProfile}
     */
    private FetchProfile createDefaultFetchProfile()
    {
        FetchProfile fp = new FetchProfile();
        fp.add(IMAPFolder.FetchProfileItem.HEADERS);
        fp.add(UIDFolder.FetchProfileItem.UID);
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.SIZE);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        // fp.add("X-Mailer");

        return fp;
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
        boolean isSeen = message.isSet(Flag.SEEN);
        int msgNum = message.getMessageNumber();
        int size = message.getSize();

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
        mail.setSize(size);
    }

    /**
     * Aktualisiert den Zähler nicht gelesener Mails.
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
            // checkRead(f);

            mf.setUnreadMailsCount(f.getUnreadMessageCount());

            closeFolder(f);
        }
    }
}
