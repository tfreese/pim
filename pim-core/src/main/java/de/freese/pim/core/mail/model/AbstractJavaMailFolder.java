// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Arrays;
import java.util.Collections;
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

                List<Message> l = Arrays.asList(e.getMessages());
                Collections.sort(l, (m1, m2) ->
                {
                    try
                    {
                        return m1.getReceivedDate().compareTo(m2.getReceivedDate());
                    }
                    catch (Exception ex)
                    {
                        // Ignore
                    }

                    return 0;
                });

                for (Message message : l)
                {
                    list.add(0, new JavaMail(AbstractJavaMailFolder.this, message));
                }
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
                    IMail m = list.stream().filter(mail ->
                    {
                        try
                        {
                            if (mail.getMessageID().equals(message.getHeader("Message-ID")[0]))
                            {
                                return true;
                            }
                        }
                        catch (Exception ex)
                        {
                            // Ignore
                        }

                        return false;
                    }).findFirst().get();

                    if (m != null)
                    {
                        list.remove(m);
                    }
                }
            }
        });
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#close()
     */
    @Override
    public void close() throws Exception
    {
        getFolder().close(true);

        for (IMailFolder child : getChildren())
        {
            child.close();
        }

        this.children.clear();
        this.children = null;

        if (this.messages != null)
        {
            this.messages.clear();
            this.messages = null;
        }
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getChildren()
     */
    @Override
    public ObservableList<IMailFolder> getChildren() throws Exception
    {
        if (this.children == null)
        {
            // @formatter:off
            this.children = Stream.of(getFolder().list("%"))
                //.peek(f -> f.open(Folder.READ_ONLY))
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
    public ObservableList<IMail> getMessages() throws Exception
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

            getFolder().fetch(msgs, fp);

            this.messages = Stream.of(msgs).map(m -> new JavaMail(this, m))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        return this.messages;
    }

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
    public int getUnreadMessageCount() throws Exception
    {
        if (this.unreadMessageCount < 0)
        {
            this.unreadMessageCount = getChildren().stream().mapToInt(c ->
            {
                try
                {
                    return c.getUnreadMessageCount();
                }
                catch (Exception ex)
                {
                    // Ignore
                }

                return 0;
            }).sum();

            this.unreadMessageCount += getFolder().getUnreadMessageCount();
        }

        return this.unreadMessageCount;
    }

    /**
     * @see de.freese.pim.core.mail.model.IMailFolder#getUnreadMessages()
     */
    @Override
    public List<IMail> getUnreadMessages() throws Exception
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

        getFolder().fetch(messages, fp);

        return Stream.of(messages).map(m -> new JavaMail(this, m)).collect(Collectors.toList());
    }

    // /**
    // * @see de.freese.pim.core.mail.model.IMailFolder#syncLocal()
    // */
    // @Override
    // public void syncLocal() throws Exception
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
     * @return {@link Folder}
     */
    protected Folder getFolder()
    {
        return this.folder;
    }
}
