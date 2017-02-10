// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.ArrayUtils;

import de.freese.pim.core.jdbc.tx.Transactional;
import de.freese.pim.core.mail.api.IMailAPI;
import de.freese.pim.core.mail.api.IMailContent;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.impl.JavaMailAPI;
import de.freese.pim.core.mail.impl.JavaMailContent;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.core.utils.io.MonitorOutputStream;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
public class DefaultMailService extends AbstractService implements IMailService
{
    /**
     *
     */
    private Map<Long, IMailAPI> mailApiMap = new ConcurrentHashMap<>();

    /**
    *
    */
    private IMailDAO mailDAO = null;

    /**
    *
    */
    private final Semaphore semaphore = new Semaphore(ISettingsService.MAX_ACTIVE_CONNECTIONS.get(), true);

    /**
     * Erzeugt eine neue Instanz von {@link DefaultMailService}
     */
    public DefaultMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#connectAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    public void connectAccount(final MailAccount account) throws Exception
    {
        Path basePath = getSettingsService().getHome();
        Path accountPath = basePath.resolve(account.getMail());

        IMailAPI mailAPI = new JavaMailAPI(account, accountPath);
        mailAPI.setExecutorService(getExecutorService());
        this.mailApiMap.put(account.getID(), mailAPI);

        mailAPI.connect();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#deleteAccount(long)
     */
    @Override
    @Transactional
    public int deleteAccount(final long accountID) throws Exception
    {
        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);
        int affectedRows = 0;

        for (MailFolder mf : folder)
        {
            affectedRows += getMailDAO().deleteMails(mf.getID());
        }

        affectedRows += getMailDAO().deleteFolders(accountID);
        affectedRows += getMailDAO().deleteAccount(accountID);

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#disconnectAccounts()
     */
    @Override
    public void disconnectAccounts() throws Exception
    {
        getLogger().info("Disconnect Accounts");

        for (IMailAPI mailAPI : this.mailApiMap.values())
        {
            getLogger().info("Close " + mailAPI.getAccount().getMail());

            try
            {
                mailAPI.disconnect();
            }
            catch (Exception ex)
            {
                getLogger().warn(ex.getMessage());
            }
        }

        this.mailApiMap.clear();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        return getMailDAO().getMailAccounts();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public int insertAccount(final MailAccount account) throws Exception
    {
        return getMailDAO().insertAccount(account);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    @Transactional
    public int[] insertOrUpdateFolder(final long accountID, final List<MailFolder> folders) throws Exception
    {
        // ID = 0 -> insert
        List<MailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).collect(Collectors.toList());
        int[] affectedRows = getMailDAO().insertFolder(accountID, toInsert);

        // ID != 0 -> update
        List<MailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).collect(Collectors.toList());

        for (MailFolder mf : toUpdate)
        {
            ArrayUtils.add(affectedRows, getMailDAO().updateFolder(mf));
        }

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#loadContent(long, de.freese.pim.core.mail.model.Mail,
     *      java.util.function.BiConsumer)
     */
    @Override
    public IMailContent loadContent(final long accountID, final Mail mail, final BiConsumer<Long, Long> loadMonitor) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("load mail: msgnum={}; uid={}; size={}; subject={}", mail.getMsgNum(), mail.getUID(), mail.getSize(),
                    mail.getSubject());
        }

        IMailAPI mailAPI = getMailAPI(accountID);
        Path folderPath = mailAPI.getBasePath().resolve(mail.getFolderFullName());
        // Path folderPath = mailAPI.getBasePath().resolve(mail.getFolderFullName().replaceAll("/", "__"));
        Path mailPath = folderPath.resolve(Long.toString(mail.getUID())).resolve(mail.getUID() + ".eml");

        JavaMailContent mailContent = null;

        if (!Files.exists(mailPath))
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("download mail: msgnum={}; uid={}", mail.getMsgNum(), mail.getUID());
            }

            // Mail download.
            Files.createDirectories(mailPath.getParent());

            try (OutputStream os = Files.newOutputStream(mailPath);
                 GZIPOutputStream gos = new GZIPOutputStream(os);
                 BufferedOutputStream bos = new BufferedOutputStream(gos);
                 MonitorOutputStream mos = new MonitorOutputStream(bos, mail.getSize(), loadMonitor))
            {
                mailAPI.loadMail(mail.getFolderFullName(), mail.getUID(), mos);
            }
            catch (Exception ex)
            {
                Files.deleteIfExists(mailPath);
                Files.deleteIfExists(mailPath.getParent());
                throw ex;
            }
        }

        // Lokal gespeicherte Mail laden.
        try (InputStream is = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(mailPath))))
        {
            MimeMessage message = new MimeMessage(null, is);
            mailContent = new JavaMailContent(message);
        }

        return mailContent;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#loadFolder(long)
     */
    @Override
    @Transactional
    public List<MailFolder> loadFolder(final long accountID) throws Exception
    {
        IMailAPI mailAPI = getMailAPI(accountID);

        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);

        if ((folder == null) || folder.isEmpty())
        {
            folder = mailAPI.getFolder();

            int[] affectedRows = insertOrUpdateFolder(accountID, folder);

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("new folder saved: affected rows={}", IntStream.of(affectedRows).sum());
            }
        }

        // Hierarchie aufbauen basierend auf Namen.
        for (MailFolder mailFolder : folder)
        {
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

    /**
     * @see de.freese.pim.core.mail.service.IMailService#loadMails(long, long, java.lang.String)
     */
    @Override
    @Transactional
    public List<Mail> loadMails(final long accountID, final long folderID, final String folderFullName) throws Exception
    {
        this.semaphore.acquire();

        try
        {
            IMailAPI mailAPI = getMailAPI(accountID);

            Map<Long, Mail> mailMap = getMailDAO().getMails(folderID).stream().collect(Collectors.toMap(Mail::getUID, Function.identity()));

            // Höchste UID finden.
            long uidFrom = mailMap.values().parallelStream().mapToLong(Mail::getUID).max().orElse(1);

            // Alle Mails lokal löschen, die zwischenzeitlich auch Remote gelöscht worden sind.
            Set<Long> currentUIDs = mailAPI.loadCurrentMessageIDs(folderFullName);

            Set<Long> remoteDeletedUIDs = new HashSet<>();
            remoteDeletedUIDs.addAll(mailMap.keySet());
            remoteDeletedUIDs.removeAll(currentUIDs);

            for (Long uid : remoteDeletedUIDs)
            {
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("delete mail: uid={}", uid);
                }

                getMailDAO().deleteMail(folderID, uid);
                mailMap.remove(uid);
            }

            if (uidFrom > 1)
            {
                // Neue Mails holen, ausser der aktuellsten geladenen.
                uidFrom += 1;
            }

            List<Mail> newMails = mailAPI.loadMails(folderFullName, uidFrom);

            if (newMails == null)
            {
                int affectedRows = getMailDAO().deleteMails(folderID);
                affectedRows += getMailDAO().deleteFolder(folderID);

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("folder deleted: affected rows={}", affectedRows);
                }

                return Collections.emptyList();
            }

            if (newMails.size() > 0)
            {
                int[] affectedRows = getMailDAO().insertMail(folderID, newMails);

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("new mails saved: affected rows={}", IntStream.of(affectedRows).sum());
                }
            }

            List<Mail> mails = new ArrayList<>();
            mails.addAll(mailMap.values());
            mails.addAll(newMails);

            mails.stream().forEach(m -> m.setFolderFullName(folderFullName));

            return mails;
        }
        finally
        {
            this.semaphore.release();
        }
    }

    /**
     * @param mailDAO {@link IMailDAO}
     */
    public void setMailDAO(final IMailDAO mailDAO)
    {
        this.mailDAO = mailDAO;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public int updateAccount(final MailAccount account) throws Exception
    {
        return getMailDAO().updateAccount(account);
    }

    /**
     * @param accountID long
     * @return {@link IMailAPI}
     */
    protected IMailAPI getMailAPI(final long accountID)
    {
        return this.mailApiMap.get(accountID);
    }

    /**
     * @return {@link IMailDAO}
     */
    protected IMailDAO getMailDAO()
    {
        return this.mailDAO;
    }
}
