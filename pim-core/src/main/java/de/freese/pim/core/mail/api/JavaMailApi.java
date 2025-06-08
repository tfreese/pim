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

import jakarta.mail.Authenticator;
import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Service;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.eclipse.angus.mail.imap.IMAPFolder;
import org.eclipse.angus.mail.imap.IMAPStore;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.function.ExceptionalFunction;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.utils.MailUtils;
import de.freese.pim.core.utils.Utils;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.core.utils.io.NestedIOMonitor;

/**
 * JavaMail-Implementierung des {@link MailApi}.
 *
 * @author Thomas Freese
 */
public class JavaMailApi extends AbstractMailApi {
    @FunctionalInterface
    protected interface FolderCallback<T> {
        T doInFolder(IMAPFolder folder) throws Exception;
    }

    /**
     * Für RoundRobin-Pool.
     */
    private final IMAPStore[] stores = new IMAPStore[Math.max(1, 3)];
    private Session session;
    private int storeIndex;

    public JavaMailApi(final MailAccount account) {
        super(account);
    }

    @Override
    public void connect() {
        session = Utils.executeSafely(this::createSession);
    }

    @Override
    public void disconnect() {
        // Folder schliessen.
        // if (topLevelFolder != null) {
        // for (IMailFolder folder : topLevelFolder) {
        // folder.close();
        // }
        // }
        // if (getFolder().isOpen()) {
        // getFolder().close(true);
        // }

        // disconnect(store);
        // disconnect(transport);
        for (int i = 0; i < stores.length; i++) {
            final Store store = stores[i];

            try {
                disconnect(store);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }

            stores[i] = null;
        }

        // store = null;
        // transport = null;
        session = null;
    }

    @Override
    public List<MailFolder> getFolder() {
        return Utils.executeSafely(() -> {
            final Folder root = getStore().getDefaultFolder();

            return Stream.of(root.list("*"))
                    .map(f -> {
                        final MailFolder mf = new MailFolder();
                        mf.setFullName(f.getFullName());
                        mf.setName(f.getName());
                        mf.setAbonniert(true);
                        return mf;
                    })
                    .toList();

            // closeFolder(root);
        });
    }

    @Override
    public <T> T loadMail(final String folderFullName, final long uid, final ExceptionalFunction<Object, T, Exception> function) {
        return executeInFolder(folderFullName, folder -> {
            final MimeMessage message = (MimeMessage) folder.getMessageByUID(uid);
            preFetch(folder, message);

            return function.apply(message);
        });
    }

    @Override
    public MailContent loadMail(final String folderFullName, final long uid, final IOMonitor monitor) {
        return executeInFolder(folderFullName, folder -> {
            final MimeMessage mimeMessage = (MimeMessage) folder.getMessageByUID(uid);
            preFetch(folder, mimeMessage);

            MailContent mailContent = null;

            if (monitor == null) {
                mailContent = new JavaMailContent(mimeMessage);
            }
            else {
                final int sizeMessage = mimeMessage.getSize();
                final long sizeAllParts = MailUtils.getSizeOfAllParts(mimeMessage);
                final long size = Math.max(sizeMessage, sizeAllParts);

                final NestedIOMonitor nestendIOMonitor = new NestedIOMonitor(monitor, size);

                mailContent = new JavaMailContent(mimeMessage, nestendIOMonitor);

                // try (FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1024)) {
                // // OutputStream gos = new GZIPOutputStream(baos);
                // try (OutputStream mos = new MonitorOutputStream(baos, size, monitor)) {
                // mimeMessage.writeTo(mos);
                // }
                //
                // // baos.close();
                //
                // // byte[] data = baos.toByteArray();
                //
                // // try (InputStream is = new ByteArrayInputStream(data);
                // // InputStream gis = new GZIPInputStream(is)
                // try (InputStream is = baos.getInputStream();) {
                // message = new MimeMessage(null, is);
                // mailContent = new JavaMailContent(mimeMessage);
                // }
                // }
            }

            return mailContent;
        });
    }

    @Override
    public List<Mail> loadMails(final String folderFullName, final long uidFrom) {
        final List<Mail> mails = executeInFolder(folderFullName, folder -> {
            final long uidTo = folder.getUIDNext();
            // SearchTerm searchTerm = new SearchTerm() {
            // @Override
            // public boolean match(final Message msg) {
            // if (msg.getMessageNumber() > maxMsgNum) {
            // return true;
            // }
            //
            // return false;
            // }
            // };

            // Message[] msgs = folder.search(searchTerm);
            final Message[] msgs = folder.getMessagesByUID(uidFrom, uidTo);
            preFetch(folder, msgs);

            final List<Mail> newMails = new ArrayList<>();

            for (Message message : msgs) {
                final Mail mail = new Mail();
                populate(mail, message);

                newMails.add(mail);
            }

            return newMails;
        });

        return mails != null ? mails : Collections.emptyList();
    }

    @Override
    public Set<Long> loadMessageIDs(final String folderFullName) {
        final Set<Long> ids = executeInFolder(folderFullName, folder -> {
            final Message[] msgs = folder.getMessages();

            final FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            folder.fetch(msgs, fp);

            final Set<Long> uids = new HashSet<>();

            for (Message msg : msgs) {
                uids.add(folder.getUID(msg));
            }

            return uids;
        });

        return ids != null ? ids : Collections.emptySet();
    }

    @Override
    public void setSeen(final Mail mail, final boolean seen) {
        executeInFolder(mail.getFolderFullName(), folder -> {
            // Bulk-Operation auf Server.
            folder.setFlags(new int[]{mail.getMsgNum()}, new Flags(Flags.Flag.SEEN), seen);

            // Einzel-Operation auf Server.
            // Message message = f.getMessage(mail.getMsgNum());
            // message.setFlag(Flag.SEEN, seen);

            return null;
        });
    }

    @Override
    public void testConnection() {
        Utils.executeSafely(() -> {
            // Test Connection Empfang.
            Store s = createStore(session);
            connect(s);
            disconnect(s);
            s = null;

            // Test Connection Versand.
            Transport t = createTransport(session);
            connect(t);
            disconnect(t);
            t = null;
        });
    }

    protected void checkRead(final Folder folder) throws MessagingException {
        if (!folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }
    }

    protected void checkWrite(final Folder folder) throws MessagingException {
        if (!folder.isOpen() || folder.getMode() == Folder.READ_ONLY) {
            folder.open(Folder.READ_WRITE);
        }
    }

    protected void closeFolder(final Folder folder) throws MessagingException {
        if (folder == null) {
            return;
        }

        if (folder.isOpen()) {
            folder.close(true);
        }
    }

    protected void connect(final Service service) throws MessagingException {
        final String host = service instanceof Store ? getAccount().getImapHost() : getAccount().getSmtpHost();
        final int port = service instanceof Store ? getAccount().getImapPort().getPort() : getAccount().getSmtpPort().getPort();

        final String mail = getAccount().getMail();
        final String password = getAccount().getPassword();

        service.connect(host, port, mail, password);
    }

    protected Session createSession() {
        final Authenticator authenticator = null;

        final Properties properties = new Properties();

        // if (getLogger().isDebugEnabled())
        if (LoggerFactory.getLogger("jakarta.mail").isDebugEnabled()) {
            properties.setProperty("mail.debug", "true");
        }

        // Legitimation für Empfang.
        if (getAccount().isImapLegitimation()) {
            properties.setProperty("mail.imap.auth", "true");
            properties.setProperty("mail.imap.starttls.enable", "true");
        }

        // Legitimation für Versand.
        if (getAccount().isSmtpLegitimation()) {
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
        }

        if (getExecutor() != null) {
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

    protected IMAPStore createStore(final Session session) throws NoSuchProviderException {
        return (IMAPStore) session.getStore("imaps");
    }

    protected Transport createTransport(final Session session) throws NoSuchProviderException {
        return session.getTransport("smtp");
    }

    protected void disconnect(final Service service) throws MessagingException {
        if (service != null && service.isConnected()) {
            service.close();
        }
    }

    protected <T> T executeInFolder(final String folderFullName, final FolderCallback<T> action) {
        return Utils.executeSafely(() -> {
            final IMAPFolder folder = (IMAPFolder) getStore().getFolder(folderFullName);

            if (folder == null) {
                getLogger().warn("Folder {} not exist", folderFullName);

                return null;
            }

            if ((folder.getType() & Folder.HOLDS_MESSAGES) == 0) {
                getLogger().warn("Folder {} can not contain messages", folderFullName);

                return null;
            }

            try {
                checkRead(folder);

                return action.doInFolder(folder);
            }
            finally {
                closeFolder(folder);
            }
        });
    }

    protected Session getSession() {
        return session;
    }

    protected synchronized IMAPStore getStore() throws MessagingException {
        IMAPStore store = stores[storeIndex++];

        if (store != null && !store.isConnected()) {
            connect(store);
        }

        if (store == null) {
            store = createStore(getSession());
            connect(store);
            stores[storeIndex - 1] = store;
        }

        if (storeIndex == stores.length) {
            storeIndex = 0;
        }

        return store;
    }

    protected void populate(final Mail mail, final Message message) throws MessagingException {
        final InternetAddress from = Optional.ofNullable(message.getFrom()).map(f -> (InternetAddress) f[0]).orElse(null);
        final InternetAddress[] to = (InternetAddress[]) message.getRecipients(RecipientType.TO);
        final InternetAddress[] cc = (InternetAddress[]) message.getRecipients(RecipientType.CC);
        final InternetAddress[] bcc = (InternetAddress[]) message.getRecipients(RecipientType.BCC);

        final String subject = message.getSubject();
        final Date receivedDate = message.getReceivedDate();
        final Date sendDate = message.getSentDate();
        final boolean isSeen = message.isSet(Flag.SEEN);
        final int msgNum = message.getMessageNumber();
        final int size = message.getSize();

        final IMAPFolder f = (IMAPFolder) message.getFolder();
        final long uid = f.getUID(message);

        // if (uid == 3066 || uid == 576 || uid == 60) {
        // // Debug Problemfälle.
        // System.out.println();
        // }
        // if (message.getFolder() instanceof IMAPFolder) {
        // uid = ((IMAPFolder) message.getFolder()).getUID(message);
        // }
        // else {
        // throw new MessagingException("IMAPFolder required");
        // }
        // if (uid == null) {
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

    private void preFetch(final Folder folder, final Message... messages) throws MessagingException {
        final FetchProfile fp = new FetchProfile();
        fp.add(IMAPFolder.FetchProfileItem.HEADERS);
        fp.add(UIDFolder.FetchProfileItem.UID);
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.SIZE);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        // fp.add("X-Mailer");

        folder.fetch(messages, fp);
    }
}
