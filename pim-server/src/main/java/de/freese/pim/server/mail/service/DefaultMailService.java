// Created: 20.01.2017
package de.freese.pim.server.mail.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
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
@RestController("mailService")
@RequestMapping(path = "/mail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DefaultMailService extends AbstractService implements MailService, BeanFactoryAware
{
    /**
     *
     */
    private BeanFactory beanFactory = null;

    /**
    *
    */
    private MailDAO mailDAO = null;

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
    @PostMapping("/connect")
    public void connectAccount(@RequestBody final MailAccount account) throws Exception
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

        MailAPI mailAPI = new JavaMailAPI(account);
        mailAPI.setExecutorService(getExecutorService());

        ConfigurableListableBeanFactory bf = (ConfigurableListableBeanFactory) getBeanFactory();
        bf.registerSingleton("mailAPI-" + account.getID(), mailAPI);

        mailAPI.connect();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#deleteAccount(long)
     */
    @Override
    @Transactional
    @DeleteMapping("/account/{id}")
    public int deleteAccount(@PathVariable("id") final long accountID) throws Exception
    {
        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);
        int affectedRows = 0;

        for (MailFolder mf : folder)
        {
            affectedRows += getMailDAO().deleteMails(mf.getID());
        }

        affectedRows += getMailDAO().deleteFolders(accountID);
        affectedRows += getMailDAO().deleteAccount(accountID);

        disconnectMailAPI(accountID);

        return affectedRows;
    }

    /**
     * Schliessen der MailAPI-Verbindung aller MailAccounts.
     *
     * @throws Exception Falls was schief geht.
     */
    @PreDestroy
    public void disconnectAccounts() throws Exception
    {
        disconnectAccounts(new long[0]);
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#disconnectAccounts(long[])
     */
    @Override
    @PostMapping("/account/disconnect")
    public void disconnectAccounts(@RequestParam("accountIDs") final long...accountIDs) throws Exception
    {
        getLogger().info("Disconnect Accounts");

        long[] ids = accountIDs;

        if (ids.length == 0)
        {
            // Alle schliessen.
            String[] mailAPINames = getApplicationContext().getBeanNamesForType(MailAPI.class);

            for (String mailAPIName : mailAPINames)
            {
                long accountID = Long.parseLong(mailAPIName.split("[-]")[1]);

                ids = ArrayUtils.add(ids, accountID);
            }
        }

        for (long id : ids)
        {
            disconnectMailAPI(id);
        }
    }

    /**
     * Schliessen der MailAPI-Verbindung des MailAccounts
     *
     * @param accountID long
     */
    protected void disconnectMailAPI(final long accountID)
    {
        String beanName = "mailAPI-" + accountID;

        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) getBeanFactory();
        MailAPI mailAPI = bf.getBean(beanName, MailAPI.class);

        getLogger().info("Close " + mailAPI.getAccount().getMail());

        try
        {
            mailAPI.disconnect();
        }
        catch (Exception ex)
        {
            getLogger().warn(ex.getMessage());
        }

        bf.destroySingleton(beanName);
    }

    /**
     * @return {@link BeanFactory}
     */
    protected BeanFactory getBeanFactory()
    {
        return this.beanFactory;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#getMailAccounts()
     */
    @Override
    @Transactional(readOnly = true)
    @GetMapping("/accounts")
    public List<MailAccount> getMailAccounts() throws Exception
    {
        return getMailDAO().getMailAccounts();
    }

    /**
     * @param accountID long
     * @return {@link MailAPI}
     */
    protected MailAPI getMailAPI(final long accountID)
    {
        MailAPI mailAPI = getApplicationContext().getBean("mailAPI-" + accountID, MailAPI.class);

        return mailAPI;
    }

    /**
     * @return {@link MailDAO}
     */
    protected MailDAO getMailDAO()
    {
        return this.mailDAO;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#insertAccount(de.freese.pim.server.mail.model.MailAccount)
     */
    @Override
    @Transactional
    @PutMapping("/account/")
    public long insertAccount(@RequestBody final MailAccount account) throws Exception
    {
        getMailDAO().insertAccount(account);

        return account.getID();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#insertFolder(long, java.util.List)
     */
    @Override
    @Transactional
    @PutMapping("/folder/{accountID}")
    public long[] insertFolder(@PathVariable("accountID") final long accountID, @RequestBody final List<MailFolder> folders) throws Exception
    {
        getMailDAO().insertFolder(accountID, folders);

        return folders.stream().mapToLong(MailFolder::getID).toArray();
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadFolder(long)
     */
    @Override
    @Transactional
    @GetMapping("/folder/{accountID}")
    public List<MailFolder> loadFolder(@PathVariable("accountID") final long accountID) throws Exception
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
     * @see de.freese.pim.server.mail.service.MailService#loadMailContent(long, java.lang.String, long, de.freese.pim.common.utils.io.IOMonitor)
     */
    @Override
    @GetMapping("/content/{accountID}/{folderFullName}/{mailUID}")
    public MailContent loadMailContent(@PathVariable("accountID") final long accountID, @PathVariable("folderFullName") final String folderFullName,
                                       @PathVariable("mailUID") final long mailUID, final @RequestBody(required = false) IOMonitor monitor)
        throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("download mail: accountID={}, folderFullName={}, uid={}", accountID, folderFullName, mailUID);
        }

        MailAPI mailAPI = getMailAPI(accountID);

        MailContent mailContent = mailAPI.loadMail(folderFullName, mailUID, monitor);

        return mailContent;
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#loadMails(long, long, java.lang.String)
     */
    @Override
    @Transactional
    @GetMapping("/mails/{accountID}/{folderID}/{folderFullName}")
    public List<Mail> loadMails(@PathVariable("accountID") final long accountID, @PathVariable("folderID") final long folderID,
                                @PathVariable("folderFullName") final String folderFullName)
        throws Exception
    {
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
    }

    /**
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    /**
     * @param mailDAO {@link MailDAO}
     */
    @Resource
    public void setMailDAO(final MailDAO mailDAO)
    {
        this.mailDAO = mailDAO;
    }

    // /**
    // * {@link DeferredResult} entkoppelt den Server Thread von der Ausführung.
    // *
    // * @see de.freese.pim.server.mail.service.MailService#loadMails3(long, long, java.lang.String)
    // */
    // @Override
    // @Transactional
    // public DeferredResult<List<Mail>> loadMails3(final long accountID, final long folderID, final String folderFullName) throws Exception
    // {
    // DeferredResult<List<Mail>> deferredResult = new DeferredResult<>();
    //
    // CompletableFuture.supplyAsync(() -> {
    // try
    // {
    // return loadMails(accountID, folderID, folderFullName);
    // }
    // catch (Exception ex)
    // {
    // throw new RuntimeException(ex);
    // }
    // }, getExecutorService()).whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
    //
    // return null;
    // }

    /**
     * @see de.freese.pim.server.mail.service.MailService#test(de.freese.pim.server.mail.model.MailAccount)
     */
    @Override
    @GetMapping("/test")
    public List<MailFolder> test(@RequestBody final MailAccount account) throws Exception
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
    @PostMapping("/account")
    public int updateAccount(@RequestBody final MailAccount account) throws Exception
    {
        return getMailDAO().updateAccount(account);
    }

    /**
     * @see de.freese.pim.server.mail.service.MailService#updateFolder(long, java.util.List)
     */
    @Override
    @Transactional
    @PostMapping("/folder/{accountID}")
    public int[] updateFolder(final long accountID, @RequestBody final List<MailFolder> folders) throws Exception
    {
        int[] affectedRows = new int[folders.size()];

        for (int i = 0; i < folders.size(); i++)
        {
            MailFolder mf = folders.get(i);

            affectedRows[i] = getMailDAO().updateFolder(mf);
        }

        return affectedRows;
    }
}
