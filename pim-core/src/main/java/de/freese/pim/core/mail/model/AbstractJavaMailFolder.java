// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.UIDFolder;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import com.sun.mail.imap.IMAPFolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Basis-Implementierung eines JavaMail {@link IMailFolder}.
 *
 * @author Thomas Freese
 * @param <A> Konkreter MailAccount
 */
@SuppressWarnings("restriction")
public abstract class AbstractJavaMailFolder<A extends AbstractJavaMailAccount> extends AbstractMailFolder<A>
{
    /**
     *
     */
    private ObservableList<IMailFolder> children = null;

    /**
     *
     */
    private final Folder folder;

    /**
     *
     */
    private ObservableList<IMail> messages = null;

    /**
     *
     */
    private int unreadMessageCount = -1;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJavaMailFolder}
     *
     * @param mailAccount {@link IMailAccount}
     * @param folder {@link Folder}
     */
    public AbstractJavaMailFolder(final A mailAccount, final Folder folder)
    {
        super(mailAccount);

        Objects.requireNonNull(folder, "folder required");

        this.folder = folder;
        this.folder.addMessageChangedListener((mce) -> this.unreadMessageCount = -1);
        this.folder.addMessageCountListener(new MessageCountListener()
        {
            /**
             * @see javax.mail.event.MessageCountListener#messagesAdded(javax.mail.event.MessageCountEvent)
             */
            @Override
            public void messagesAdded(final MessageCountEvent e)
            {
                ObservableList<IMail> list = AbstractJavaMailFolder.this.messages;

                if (list == null)
                {
                    return;
                }

                // @formatter:off
                List<IMail> newMails = Stream.of(e.getMessages())
                        .map(m -> new JavaMail(AbstractJavaMailFolder.this, m))
                        .sorted(Comparator.comparing(IMail::getReceivedDate).reversed())
                        .collect(Collectors.toList());
                // @formatter:on

                list.addAll(0, newMails);
            }

            /**
             * @see javax.mail.event.MessageCountListener#messagesRemoved(javax.mail.event.MessageCountEvent)
             */
            @Override
            public void messagesRemoved(final MessageCountEvent e)
            {
                ObservableList<IMail> list = AbstractJavaMailFolder.this.messages;

                if (list == null)
                {
                    return;
                }

                for (Message message : e.getMessages())
                {
                    IMail rm = new JavaMail(AbstractJavaMailFolder.this, message);

                    IMail m = list.stream().filter(mail -> mail.getID().equals(rm.getID())).findFirst().get();

                    if (m != null)
                    {
                        list.remove(m);
                    }
                }
            }
        });
    }

    /**
     * Stellt sicher, das der {@link Folder} zum Lesen geöffnet ist.
     *
     * @throws Exception Falls was schief geht.
     */
    protected void checkRead() throws Exception
    {
        if (!getFolder().isOpen())
        {
            getFolder().open(Folder.READ_ONLY);
        }
    }

    /**
     * Stellt sicher, das der {@link Folder} zum Schreiben geöffnet ist.
     *
     * @throws Exception Falls was schief geht.
     */
    protected void checkWrite() throws Exception
    {
        if (!getFolder().isOpen() || (getFolder().getMode() == Folder.READ_ONLY))
        {
            getFolder().open(Folder.READ_WRITE);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#close()
     */
    @Override
    public void close()
    {
        try
        {
            if (getFolder().isOpen())
            {
                getFolder().close(true);
            }

            if (this.children != null)
            {
                for (IMailFolder child : this.children)
                {
                    child.close();
                }
            }

            this.children.clear();
            this.children = null;

            if (this.messages != null)
            {
                this.messages.clear();
                this.messages = null;
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getChildren()
     */
    @Override
    public ObservableList<IMailFolder> getChildren()
    {
        try
        {
            if (this.children == null)
            {
                // @formatter:off
                this.children = Stream.of(getFolder().list("%"))
                        .map(f -> new MailFolder(getMailAccount(), f))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                // @formatter:on

                // Verzeichnisse anlegen
                // for (IMailFolder mailFolder : children)
                // {
                // Files.createDirectories(mailFolder.getPath());
                // }
            }

            return this.children;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return {@link Folder}
     */
    protected Folder getFolder()
    {
        return this.folder;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getFullName()
     */
    @Override
    public String getFullName()
    {
        return getFolder().getFullName();
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getMessages()
     */
    @Override
    public ObservableList<IMail> getMessages()
    {
        try
        {
            if (this.messages == null)
            {
                checkRead();

                Message[] msgs = getFolder().getMessages();

                // Nur bestimmte Mail-Attribute vorladen.
                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                fp.add(UIDFolder.FetchProfileItem.UID);
                fp.add(IMAPFolder.FetchProfileItem.HEADERS);
                // fp.add(FetchProfile.Item.CONTENT_INFO);

                getFolder().fetch(msgs, fp);

                // @formatter:off
                this.messages = Stream.of(msgs)
                        .map(m -> new JavaMail(this, m))
                        .sorted(Comparator.comparing(IMail::getReceivedDate).reversed())
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                // @formatter:off
            }

            return this.messages;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    // /**
    // * @see de.freese.pim.core.mail.model.IMailFolder#syncLocal()
    // */
    // @Override
    // public void syncLocal()
    // {
    // try
    // {
    // checkRead();
    // // SearchTerm searchTerm = new MessageIDTerm(messageId);
    // // Message[] messages = imapFolder.search(searchTerm);
    //
    // // Lokales Folder auslesen.
    // Predicate<Path> isDirectory = Files::isDirectory;
    // Predicate<Path> isHidden = (p) -> p.getFileName().toString().startsWith(".");
    //
    // Map<String, Path> localMap = Files.list(getPath()).filter(isDirectory.negate().and(isHidden.negate()))
    // .collect(Collectors.toMap(p -> p.getFileName().toString(), Function.identity()));
    //
    // // Remote-Folder auslesen.
    // Message[] messages = getFolder().getMessages();
    //
    // // Nur bestimmte Mail-Attribute vorladen.
    // FetchProfile fp = new FetchProfile();
    // // fp.add(FetchProfile.Item.ENVELOPE);
    // fp.add(UIDFolder.FetchProfileItem.UID);
    // fp.add(IMAPFolder.FetchProfileItem.HEADERS);
    //
    // getFolder().fetch(messages, fp);
    //
    // for (Message message : messages)
    // {
    // String messageID = message.getHeader("Message-ID")[0];
    //
    // // Message-ID = Dateiname
    // Path path = localMap.remove(messageID);
    //
    // if (path == null)
    // {
    // Date receivedDate = message.getReceivedDate();
    //
    // try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(getPath().resolve(messageID))))
    // {
    // // ReceivedDate merken, da nicht im HEADER vorkommt und IMAPMessage read-only ist.
    // byte[] bytes = ASCIIUtility.getBytes("RECEIVED-DATE: " + receivedDate.toInstant().toString() + "\r\n");
    // os.write(bytes);
    //
    // message.writeTo(os);
    // }
    // }
    // }
    //
    // // Was am Ende noch in der lokalen Map enthalten ist kann gelöscht werden.
    // localMap.values().forEach(p ->
    // {
    // try
    // {
    // Files.delete(p);
    // }
    // catch (Exception ex)
    // {
    // // Ignore
    // }
    // });
    // }
    // catch (Exception ex)
    // {
    // throw new RuntimeException(ex);
    // }
    // }
    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getName()
     */
    @Override
    public String getName()
    {
        return getFolder().getName();
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getUnreadMessageCount()
     */
    @Override
    public int getUnreadMessageCount()
    {
        try
        {
            if (this.unreadMessageCount < 0)
            {
                this.unreadMessageCount = getChildren().stream().mapToInt(IMailFolder::getUnreadMessageCount).sum();

                this.unreadMessageCount += getFolder().getUnreadMessageCount();
            }

            return this.unreadMessageCount;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getUnreadMessages()
     */
    @Override
    public List<IMail> getUnreadMessages()
    {
        try
        {
            checkRead();

            // Nur ungelesene Mails holen.
            SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = getFolder().search(searchTerm);

            // Nur bestimmte Mail-Attribute vorladen.
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(UIDFolder.FetchProfileItem.UID);
            fp.add(IMAPFolder.FetchProfileItem.HEADERS);
//            fp.add(FetchProfile.Item.CONTENT_INFO);

            getFolder().fetch(messages, fp);

            return Stream.of(messages).map(m -> new JavaMail(this, m)).collect(Collectors.toList());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
