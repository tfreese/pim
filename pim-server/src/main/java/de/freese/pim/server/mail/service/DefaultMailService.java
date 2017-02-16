// Created: 20.01.2017
package de.freese.pim.server.mail.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.MonitorOutputStream;
import de.freese.pim.server.mail.api.JavaMailAPI;
import de.freese.pim.server.mail.api.MailAPI;
import de.freese.pim.server.mail.dao.MailDAO;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;
import de.freese.pim.server.service.AbstractService;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
// @Service
public class DefaultMailService extends AbstractService implements MailService
{
    /**
     *
     */
    private static final Map<Long, MailAPI> MAIL_API_MAP = new ConcurrentHashMap<>();

    /**
    *
    */
    private MailDAO mailDAO = null;

    // /**
    // *
    // */
    // private final Semaphore semaphore = new Semaphore(ISettingsService.MAX_ACTIVE_CONNECTIONS.get(), true);

    /**
     * Erzeugt eine neue Instanz von {@link DefaultMailService}
     */
    public DefaultMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#connectAccount(de.freese.pim.server.mail.model.MailAccount)
     */
    @Override
    public void connectAccount(final MailAccount account) throws Exception
    {
        // BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SomeClass.class);
        // builder.addPropertyReference("propertyName", "someBean"); // add dependency to other bean
        // builder.addPropertyValue("propertyName", someValue); // set property value
        // DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getBeanFactory();
        // factory.registerBeanDefinition("beanName", builder.getBeanDefinition());

        // BeanDefinitionBuilder.genericBeanDefinition(String.class).addConstructorArgValue("test").getBeanDefinition()
        // BeanDefinitionBuilder.genericBeanDefinition(TestBean.class).addConstructorArgReference("myTestStringBean").getBeanDefinition()

        // GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        // beanDefinition.setBeanClassName(className);
        // beanDefinition.setFactoryMethodName("getService");
        // beanDefinition.setLazyInit(true);
        // factory.registerBeanDefinition("beanName", beanDefinition);

        // Is possible also to register a singleton bean instance (already configured) with
        //
        // context.getBeanFactory().registerSingleton(beanName, singletonObject)

        MailAPI mailAPI = new JavaMailAPI(account);
        mailAPI.setExecutorService(getExecutorService());
        MAIL_API_MAP.put(account.getID(), mailAPI);

        mailAPI.connect();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#deleteAccount(long)
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
     * @see de.freese.pim.server.mail.service.MailService#disconnectAccounts()
     */
    @Override
    public void disconnectAccounts() throws Exception
    {
        getLogger().info("Disconnect Accounts");

        List<Long> accountsIDs = new ArrayList<>(MAIL_API_MAP.keySet());

        for (Long accountsID : accountsIDs)
        {
            MailAPI mailAPI = MAIL_API_MAP.get(accountsID);

            getLogger().info("Close " + mailAPI.getAccount().getMail());

            try
            {
                mailAPI.disconnect();
            }
            catch (Exception ex)
            {
                getLogger().warn(ex.getMessage());
            }

            MAIL_API_MAP.remove(accountsID);
        }

        MAIL_API_MAP.clear();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#getMailAccounts()
     */
    @Override
    @Transactional(readOnly = true)
    public List<MailAccount> getMailAccounts() throws Exception
    {
        return getMailDAO().getMailAccounts();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#insertAccount(de.freese.pim.server.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public long insertAccount(final MailAccount account) throws Exception
    {
        getMailDAO().insertAccount(account);

        return account.getID();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#insertFolder(long, java.util.List)
     */
    @Override
    @Transactional
    public long[] insertFolder(final long accountID, final List<MailFolder> folders) throws Exception
    {
        getMailDAO().insertFolder(accountID, folders);

        return folders.stream().mapToLong(MailFolder::getID).toArray();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadFolder(long)
     */
    @Override
    @Transactional
    public List<MailFolder> loadFolder(final long accountID) throws Exception
    {
        MailAPI mailAPI = getMailAPI(accountID);

        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);

        if ((folder == null) || folder.isEmpty())
        {
            folder = mailAPI.getFolder();

            long[] primaryKeys = insertFolder(accountID, folder);

            for (int i = 0; i < primaryKeys.length; i++)
            {
                folder.get(i).setID(primaryKeys[i]);
            }

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("new folder saved: affected rows={}", primaryKeys.length);
            }
        }

        return folder;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadMailContent(long, java.lang.String, long, java.util.function.BiConsumer, int)
     */
    @Override
    public byte[] loadMailContent(final long accountID, final String folderFullName, final long mailUID,
            final BiConsumer<Long, Long> loadMonitor, final int size) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("download mail: uid={}; size={}", mailUID, size);
        }

        MailAPI mailAPI = getMailAPI(accountID);
        byte[] rawData = null;

        // FastByteArrayOutputStream baos = new FastByteArrayOutputStream(4*1024);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);

        // BufferedOutputStream bos = new BufferedOutputStream(gos);
        try (GZIPOutputStream gos = new GZIPOutputStream(baos);
             MonitorOutputStream mos = new MonitorOutputStream(gos, size, loadMonitor))
        {
            mailAPI.loadMail(folderFullName, mailUID, mos);
        }

        baos.close();

        rawData = baos.toByteArray();

        return rawData;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadMailContent2(long, java.lang.String, long, java.util.function.BiConsumer, int)
     */
    @Override
    public MailContent loadMailContent2(final long accountID, final String folderFullName, final long mailUID,
            final BiConsumer<Long, Long> loadMonitor, final int size) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("download mail: uid={}; size={}", mailUID, size);
        }

        MailAPI mailAPI = getMailAPI(accountID);

        MailContent mailContent = mailAPI.loadMail(folderFullName, mailUID, loadMonitor, size);

        return mailContent;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadMails(long, long, java.lang.String)
     */
    @Override
    @Transactional
    public List<Mail> loadMails(final long accountID, final long folderID, final String folderFullName) throws Exception
    {
        // this.semaphore.acquire();

        // try
        // {
        getLogger().info("Load Mails: account={}, folder={}", accountID, folderFullName);

        MailAPI mailAPI = getMailAPI(accountID);

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
        // }
        // finally
        // {
        // this.semaphore.release();
        // }
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadMails2(long, long, java.lang.String)
     */
    @Override
    @Async // ("executorService")
    @Transactional
    public Future<List<Mail>> loadMails2(final long accountID, final long folderID, final String folderFullName) throws Exception
    {
        List<Mail> mails = loadMails(accountID, folderID, folderFullName);

        return new AsyncResult<>(mails);
    }

    /**
     * {@link DeferredResult} entkoppelt den Server Thread von der Ausführung.
     *
     * @see de.freese.pim.server.mail.service.MailService#loadMails3(long, long, java.lang.String)
     */
    @Override
    @Transactional
    public DeferredResult<List<Mail>> loadMails3(final long accountID, final long folderID, final String folderFullName) throws Exception
    {
        DeferredResult<List<Mail>> deferredResult = new DeferredResult<>();

        CompletableFuture.supplyAsync(() ->
        {
            try
            {
                return loadMails(accountID, folderID, folderFullName);
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }, getExecutorService()).whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));

        return null;
    }

    /**
     * @param mailDAO {@link MailDAO}
     */
    public void setMailDAO(final MailDAO mailDAO)
    {
        this.mailDAO = mailDAO;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#test(de.freese.pim.server.mail.model.MailAccount)
     */
    @Override
    public List<MailFolder> test(final MailAccount account) throws Exception
    {
        List<MailFolder> folder = null;

        MailAPI mailAPI = new JavaMailAPI(account);

        try
        {
            mailAPI.connect();
            mailAPI.testConnection();

            folder = mailAPI.getFolder();
        }
        finally
        {
            try
            {
                mailAPI.disconnect();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        return folder;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#updateAccount(de.freese.pim.server.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public int updateAccount(final MailAccount account) throws Exception
    {
        return getMailDAO().updateAccount(account);
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#updateFolder(long, java.util.List)
     */
    @Override
    @Transactional
    public int[] updateFolder(final long accountID, final List<MailFolder> folders) throws Exception
    {
        int[] affectedRows = new int[folders.size()];

        for (int i = 0; i < folders.size(); i++)
        {
            MailFolder mf = folders.get(i);

            affectedRows[i] = getMailDAO().updateFolder(mf);
        }

        return affectedRows;
    }

    /**
     * @param accountID long
     * @return {@link MailAPI}
     */
    protected MailAPI getMailAPI(final long accountID)
    {
        return MAIL_API_MAP.get(accountID);
    }

    /**
     * @return {@link MailDAO}
     */
    protected MailDAO getMailDAO()
    {
        return this.mailDAO;
    }
}
