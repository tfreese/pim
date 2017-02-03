// Created: 23.01.2017
package de.freese.pim.core.mail.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
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
import com.sun.mail.imap.IMAPStore;
import de.freese.pim.core.mail.JavaMailBuilder;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.model.SumUnreadMailsInChildFolderBinding;
import de.freese.pim.core.mail.utils.MailContent;
import de.freese.pim.core.mail.utils.MailUtils;
import de.freese.pim.core.utils.io.MonitorOutputStream;
import javafx.beans.value.ObservableIntegerValue;
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
    private final FilteredList<MailFolder> rootFolder;

    /**
     *
     */
    private final Semaphore semaphore = new Semaphore(10, true);

    // /**
    // *
    // */
    // private Store store = null;

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
    private final IMAPStore[] stores = new IMAPStore[5];

    /**
     *
     */
    private ObservableIntegerValue unreadMailsCount = null;

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
        this.rootFolder = new FilteredList<>(this.abonnierteFolder, MailFolder::isParent);

        // Zähler mit der Folder-List verbinden.
        this.unreadMailsCount = new SumUnreadMailsInChildFolderBinding(this.rootFolder);
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
     * @see de.freese.pim.core.mail.service.IMailAPI#connect()
     */
    @Override
    public void connect() throws Exception
    {
        this.session = createSession();
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
     * @see de.freese.pim.core.mail.service.IMailAPI#getUnreadMailsCount()
     */
    @Override
    public int getUnreadMailsCount()
    {
        return this.unreadMailsCount.intValue();
        // ObservableList<MailFolder> folder = getFolderSubscribed();
        //
        // if (folder.isEmpty())
        // {
        // return 0;
        // }
        //
        // // int sum = 0;
        // //
        // // // Reverse
        // // for (int i = folder.size() - 1; i >= 0; i--)
        // // {
        // // sum += folder.get(i).getUnreadMailsCount();
        // // }
        //
        // int sum = folder.stream().mapToInt(MailFolder::getUnreadMailsCountTotal).sum();
        //
        // return sum;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadFolder()
     */
    @Override
    public List<MailFolder> loadFolder() throws Exception
    {
        this.semaphore.acquire();

        try
        {
            List<MailFolder> folder = null;

            // Folder aus DB laden.
            if (getMailService() != null)
            {
                folder = getMailService().getMailFolder(getAccount().getID());
            }

            if ((folder == null) || folder.isEmpty())
            {
                // Noch keine Folder-Abonnenten gepflegt -> Folder von Provider laden.
                Folder root = getStore().getDefaultFolder();

                // @formatter:off
                folder = Stream.of(root.list("*"))
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

                closeFolder(root);

                if (getMailService() != null)
                {
                    getMailService().insertOrUpdateFolder(getAccount().getID(), folder);
                }
            }

            // Hierarchie aufbauen basierend auf Namen.
            for (MailFolder mailFolder : folder)
            {
                mailFolder.setMailAPI(this);

                // @formatter:off
                Optional<MailFolder> parent = folder.stream()
                        .filter(mf -> !Objects.equals(mf, mailFolder))
                        .filter(mf -> mailFolder.getFullName().startsWith(mf.getFullName()))
                        .findFirst();
                // @formatter:on

                parent.ifPresent(p -> p.addChild(mailFolder));
            }

            return folder;
        }
        finally
        {
            this.semaphore.release();
        }
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadMails(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    public List<Mail> loadMails(final MailFolder folder) throws Exception
    {
        this.semaphore.acquire();

        try
        {
            List<Mail> mails = getMailService().getMails(folder.getID());
            // Map<Integer, Mail> map = mails.stream().collect(Collectors.toMap(Mail::getMsgNum, Function.identity()));

            // Höchste UID finden.
            long maxUID = mails.parallelStream().mapToLong(Mail::getUID).max().orElse(1);

            if (maxUID > 1)
            {
                // Neue Mails holen, ausser der aktuellsten geladenen.
                maxUID += 1;
            }

            // Mails von Provider laden, die eine höhere MsgNum/UID als die bereits vorhanden haben.
            IMAPFolder f = (IMAPFolder) getStore().getFolder(folder.getFullName());

            if (f == null)
            {
                getLogger().warn("Folder {} not exist", folder.getFullName());

                getMailService().deleteFolder(folder.getID());

                return Collections.emptyList();
            }

            checkRead(f);

            long nextUID = f.getUIDNext();
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
            Message[] msgs = f.getMessagesByUID(maxUID, nextUID);

            // Nur bestimmte Mail-Attribute vorladen.
            FetchProfile fp = createDefaultFetchProfile();
            f.fetch(msgs, fp);

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("mails: number={}", msgs.length);
            }

            for (Message message : msgs)
            {
                Mail mail = new Mail();
                mail.setFolder(folder);
                populate(mail, message);

                getMailService().insertMail(folder.getID(), mail);
                mails.add(mail);
            }

            return mails;
        }
        finally
        {
            this.semaphore.release();
        }
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadNewMails(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    public List<Mail> loadNewMails(final MailFolder folder) throws Exception
    {
        Folder f = getStore().getFolder(folder.getFullName());
        checkRead(f);

        SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message[] msgs = f.search(searchTerm);

        // Nur bestimmte Mail-Attribute vorladen.
        FetchProfile fp = createDefaultFetchProfile();
        f.fetch(msgs, fp);

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("new mails: number={}", msgs.length);
        }

        List<Mail> mails = new ArrayList<>();

        for (Message message : msgs)
        {
            Mail mail = new Mail();
            mail.setFolder(folder);
            populate(mail, message);

            mails.add(mail);
        }

        closeFolder(f);

        return mails;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailAPI#loadTextContent(de.freese.pim.core.mail.model.Mail, java.util.function.BiConsumer)
     */
    @Override
    public MailContent loadTextContent(final Mail mail, final BiConsumer<Long, Long> loadMonitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(), mail.getSubject());
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

            IMAPFolder f = (IMAPFolder) getStore().getFolder(mail.getFolder().getFullName());
            checkRead(f);

            message = f.getMessageByUID(mail.getUID());

            // if (f instanceof IMAPFolder)
            // {
            // message = ((IMAPFolder) f).getMessageByUID(mail.getUID());
            // }
            // else
            // {
            // throw new MessagingException("IMAPFolder required");
            // // message = f.getMessage(mail.getMsgNum());
            // }

            // FetchProfile fp = createDefaultFetchProfile();
            // f.fetch(new Message[]
            // {
            // message
            // }, fp);
            // https://www.ibm.com/developerworks/community/blogs/a9ba1efe-b731-4317-9724-a181d6155e3a/entry/reducing_imap_attachment_prefetch_latency_in_the_email_listener?lang=en
            // http://stackoverflow.com/questions/8322836/javamail-imap-over-ssl-quite-slow-bulk-fetching-multiple-messages

            try (OutputStream os = Files.newOutputStream(path);
                 GZIPOutputStream gos = new GZIPOutputStream(os);
                 BufferedOutputStream bos = new BufferedOutputStream(gos);
                 MonitorOutputStream mos = new MonitorOutputStream(bos, mail.getSize(), loadMonitor))

            {
                message.writeTo(mos);
            }

            closeFolder(f);
        }

        // Lokale Mail laden.
        try (InputStream is = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(path))))
        {
            message = new MimeMessage(null, is);
        }

        // Mail-Inhalt lesen.
        List<DataSource> dataSources = MailUtils.getTextDataSources(message);

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("dataSources: count={}", dataSources.size());

            for (DataSource dataSource : dataSources)
            {
                getLogger().debug("dataSource: name={}; contentType={}", dataSource.getName(), dataSource.getContentType());
            }
        }

        Optional<DataSource> dataSource = dataSources.stream().filter(ds -> ds.getContentType().startsWith("text/html")).findFirst();

        if (!dataSource.isPresent())
        {
            // Kein HTML gefunden -> nach Plain-Text suchen.
            dataSource = dataSources.stream().filter(ds -> ds.getContentType().startsWith("text/plain")).findFirst();
        }

        if (!dataSource.isPresent())
        {
            return null;
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("use dataSource: {}", dataSource.get().getContentType());
        }

        MailContent mailContent = new MailContent(dataSource.get());
        Path contentPath = mailContent.isHTML() ? htmlContentPath : textContentPath;
        mailContent.setUrl(contentPath.toUri().toURL());

        // Mail-Inhalt speichern.
        try (InputStream is = dataSource.get().getInputStream())
        {
            Files.copy(is, contentPath, StandardCopyOption.REPLACE_EXISTING);
        }

        if (mailContent.isText())
        {
            return mailContent;
        }

        // Bei HTML die Inlines bearbeiten.
        // http://stackoverflow.com/questions/26363573/registering-and-using-a-custom-java-net-url-protocol
        if (mailContent.isHTML())
        {
            String html = mailContent.getContent();

            List<MimeBodyPart> inlines = MailUtils.getInlines(message);

            for (MimeBodyPart inline : inlines)
            {
                String contentID = Optional.ofNullable(inline.getHeader(JavaMailBuilder.HEADER_CONTENT_ID)).map(h -> h[0]).orElse(null);

                if (contentID == null)
                {
                    continue;
                }

                contentID = contentID.replace("<", "");
                contentID = contentID.replace(">", "");

                // spring-context-support: org/springframework/mail/javamail/mime.types
                // image/gif
                // image/jpeg
                Path inlinePath = path.getParent().resolve(mail.getUID() + "_" + contentID + ".inline");

                try (InputStream is = inline.getInputStream())
                {
                    Files.copy(is, inlinePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // src=\"cid:image1\">
                html = html.replaceAll("cid:" + contentID, inlinePath.toUri().toString());
            }

            // Geändertes HTML spreichern.
            try (PrintWriter pw = new PrintWriter(Files.newOutputStream(contentPath)))
            {
                pw.append(html);
            }
        }

        mailContent = new MailContent(contentPath);

        return mailContent;
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

        IMAPFolder f = (IMAPFolder) message.getFolder();
        long uid = f.getUID(message);

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

    // /**
    // * Aktualisiert den Zähler nicht gelesener Mails.
    // *
    // * @param folders {@link List}
    // * @throws Exception Falls was schief geht.
    // */
    // protected void updateUnreadMailsCount(final List<MailFolder> folders) throws Exception
    // {
    // if (folders.isEmpty())
    // {
    // return;
    // }
    //
    // for (MailFolder mf : folders)
    // {
    // Folder f = getStore().getFolder(mf.getFullName());
    // // checkRead(f);
    //
    // mf.setUnreadMailsCount(f.getUnreadMessageCount());
    //
    // closeFolder(f);
    // }
    // }
}
