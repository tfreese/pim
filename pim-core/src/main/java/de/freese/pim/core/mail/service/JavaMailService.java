// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import com.sun.mail.imap.IMAPFolder;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.utils.Utils;

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
    private List<MailFolder> rootFolder = null;

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
     *
     * @param account {@link MailAccount}
     * @param basePath {@link Path}
     */
    public JavaMailService(final MailAccount account, final Path basePath)
    {
        super(account, basePath);
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
     * @see de.freese.pim.core.mail.service.IMailService#connect()
     */
    @Override
    public void connect() throws Exception
    {
        this.session = createSession();

        // // Test Connection Empfang.
        // Store s = createStore(this.session);
        // connectStore(s);
        // disconnectStore(s);
        // s = null;
        //
        // // Test Connection Versand.
        // Transport t = createTransport(this.session);
        // connectTransport(t);
        // disconnectTransport(t);
        // t = null;
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
     * @see de.freese.pim.core.mail.service.IMailService#disconnect()
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

        disconnectStore(this.store);
        // disconnectTransport(transport);

        this.store = null;
        // transport = null;

        this.session = null;
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
     * @see de.freese.pim.core.mail.service.IMailService#getChilds(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    public List<MailFolder> getChilds(final MailFolder parent) throws Exception
    {
        Files.createDirectories(parent.getPath());
        Path leafPath = parent.getPath().resolve(".leaf");

        if (Files.exists(leafPath))
        {
            // Folder hat keine Children.
            return Collections.emptyList();
        }

        List<MailFolder> childFolder = null;

        List<Path> folderList = Files.list(parent.getPath()).filter(Utils.PREDICATE_MAIL_FOLDER).collect(Collectors.toList());

        if (folderList.isEmpty())
        {
            // Initiale Füllung
            Folder folder = getStore().getFolder(parent.getFullName());
            checkRead(folder);

            // @formatter:off
            childFolder = Stream.of(folder.list("%"))
                    .map(f -> new MailFolder(this, f.getName(), parent))
                    .collect(Collectors.toList());
            // @formatter:on

            if (childFolder.isEmpty())
            {
                // Folder hat keine Children.
                Files.createFile(leafPath);
            }
            else
            {
                // Folder anlegen
                for (MailFolder mf : childFolder)
                {
                    Files.createDirectories(mf.getPath());
                }
            }

            closeFolder(folder);
        }
        else
        {
            // @formatter:off
            childFolder = folderList.stream()
                 .map(p -> p.getFileName())
                 .map(p -> new MailFolder(this, p.toString(), parent))
                 .collect(Collectors.toList());
             // @formatter:on
        }

        return childFolder;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getNewMails(de.freese.pim.core.mail.model.MailFolder)
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
    public synchronized List<MailFolder> getRootFolder() throws Exception
    {
        if (this.rootFolder == null)
        {
            Path path = getBasePath();
            Files.createDirectories(path);

            List<Path> folderList = Files.list(path).filter(Utils.PREDICATE_MAIL_FOLDER).collect(Collectors.toList());

            if (folderList.isEmpty())
            {
                // Initiale Füllung
                Folder root = getStore().getDefaultFolder();
                // checkRead(root); // Wirft Fehler bei Default-Folder.

                // @formatter:off
                this.rootFolder = Stream.of(root.list("%"))
                        .map(f -> new MailFolder(this, f.getName()))
                        .collect(Collectors.toList());
                // @formatter:on

                // Folder anlegen
                for (MailFolder mf : this.rootFolder)
                {
                    Files.createDirectories(mf.getPath());
                }

                closeFolder(root);
            }
            else
            {
                // @formatter:off
                this.rootFolder = folderList.stream()
                     .map(p -> p.getFileName())
                     .map(p -> new MailFolder(this, p.toString()))
                     .collect(Collectors.toList());
                 // @formatter:on
            }
        }

        return this.rootFolder;
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
     * @see de.freese.pim.core.mail.service.IMailService#getUnreadMailsCount()
     */
    @Override
    public int getUnreadMailsCount()
    {
        if (this.rootFolder == null)
        {
            return 0;
        }

        try
        {
            int sum = getRootFolder().stream().mapToInt(MailFolder::getUnreadMailsCount).sum();

            return sum;
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }
            else
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#loadMails(de.freese.pim.core.mail.model.MailFolder, java.util.function.Consumer)
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

        for (Message message : msgs)
        {
            Mail mail = new Mail(folder);
            populate(mail, message);

            consumer.accept(mail);
        }

        closeFolder(f);
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
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setReceivedDate(receivedDate);
        mail.setSendDate(sendDate);
        mail.setSeen(isSeen);
        mail.setID(id);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#syncChildFolder(de.freese.pim.core.mail.model.MailFolder, java.util.function.Consumer,
     *      java.util.function.Consumer)
     */
    @Override
    public void syncChildFolder(final MailFolder parent, final Consumer<MailFolder> newFolderConsumer, final Consumer<String> removedFolderConsumer)
        throws Exception
    {
        Path leafPath = parent.getPath().resolve(".leaf");

        Folder f = getStore().getFolder(parent.getFullName());
        checkRead(f);

        // @formatter:off
        Map<String, Path> localMap = Files.list(parent.getPath())
                .filter(Utils.PREDICATE_MAIL_FOLDER.and(Utils.PREDICATE_MAIL_FOLDER_LEAF_NOT))
                .collect(Collectors.toMap(p -> p.getFileName().toString(), Function.identity()));
        // @formatter:on

        List<Folder> childList = Stream.of(f.list("%")).collect(Collectors.toList());
        closeFolder(f);

        if (childList.isEmpty())
        {
            // Folder hat keine Children.
            if (!Files.exists(leafPath))
            {
                Files.createFile(leafPath);
            }
        }
        else
        {
            Files.deleteIfExists(leafPath);

            for (Folder child : childList)
            {
                String name = child.getName();
                localMap.remove(name);

                Path childPath = parent.getPath().resolve(name);

                if (!Files.exists(childPath))
                {
                    // Neuer Folder
                    Files.createDirectories(childPath);
                    newFolderConsumer.accept(new MailFolder(this, name, parent));
                }
            }
        }

        // Folder, die jetzt noch in der Map sind, wurden gelöscht.
        for (Entry<String, Path> entry : localMap.entrySet())
        {
            Utils.deleteDirectoryRecursiv(entry.getValue());
            removedFolderConsumer.accept(entry.getKey());
        }

        localMap.clear();
        localMap = null;
    }
}
