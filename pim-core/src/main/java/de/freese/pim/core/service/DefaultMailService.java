// Created: 20.01.2017
package de.freese.pim.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.core.dao.MailDAO;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.mail.api.JavaMailApi;
import de.freese.pim.core.mail.api.MailApi;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.utils.io.IOMonitor;

/**
 * Service für die Mail-API.
 *
 * @author Thomas Freese
 */
@Service("mailService")
@Profile("!ClientREST")
public class DefaultMailService extends AbstractService implements MailService, BeanFactoryAware {
    private BeanFactory beanFactory;

    private MailDAO mailDAO;

    /**
     * @see de.freese.pim.core.service.MailService#connectAccount(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    public void connectAccount(final MailAccount account) {
        getLogger().info("connect {}", account.getMail());

        // BeanFactory per Definition:
        // BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SomeClass.class);
        // builder.addPropertyReference("propertyName", "someBean"); // add dependency to other bean
        // builder.addPropertyValue("propertyName", someValue); // set property value
        // DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getBeanFactory();
        // BeanDefinitionRegistry#registerBeanDefinition("beanName", builder.getBeanDefinition());

        // BeanDefinitionBuilder.genericBeanDefinition(String.class).addConstructorArgValue("test").getBeanDefinition()
        // BeanDefinitionBuilder.genericBeanDefinition(TestBean.class).addConstructorArgReference("myTestStringBean").getBeanDefinition()

        // GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        // beanDefinition.setBeanClassName(className);
        // beanDefinition.setFactoryMethodName("getService");
        // beanDefinition.setLazyInit(true);
        // BeanDefinitionRegistry#registerBeanDefinition("beanName", beanDefinition);

        // BeanFactory per Programming:
        // GenericApplicationContext#registerBean(Foo.class);
        // GenericApplicationContext#registerBean(Bar.class, () -> new Bar(ctx.getBean(Foo.class));

        String beanName = getAccountBeanName(account.getID());

        if (getApplicationContext().containsBean(beanName)) {
            // Bean ist bereits registriert.
            return;
        }

        MailApi mailAPI = new JavaMailApi(account);
        mailAPI.setExecutor(getTaskExecutor());

        ConfigurableListableBeanFactory bf = (ConfigurableListableBeanFactory) getBeanFactory();
        bf.registerSingleton(beanName, mailAPI);

        mailAPI.connect();
    }

    /**
     * @see de.freese.pim.core.service.MailService#deleteAccount(long)
     */
    @Override
    @Transactional
    public int deleteAccount(final long accountID) {
        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);
        int affectedRows = 0;

        for (MailFolder mf : folder) {
            affectedRows += getMailDAO().deleteMails(mf.getID());
        }

        affectedRows += getMailDAO().deleteFolders(accountID);
        affectedRows += getMailDAO().deleteAccount(accountID);

        disconnectMailAPI(accountID);

        return affectedRows;
    }

    @PreDestroy
    public void disconnectAccounts() {
        disconnectAccounts(new long[0]);
    }

    /**
     * @see de.freese.pim.core.service.MailService#disconnectAccounts(long[])
     */
    @Override
    public void disconnectAccounts(final long... accountIDs) {
        getLogger().info("Disconnect Accounts");

        List<Long> ids = new ArrayList<>();

        Arrays.stream(accountIDs).forEach(ids::add);

        if (ids.isEmpty()) {
            // Alle schliessen.
            String[] mailAPINames = getApplicationContext().getBeanNamesForType(MailApi.class);

            for (String mailAPIName : mailAPINames) {
                long accountID = Long.parseLong(mailAPIName.split("-")[1]);

                ids.add(accountID);
            }
        }

        for (long id : ids) {
            disconnectMailAPI(id);
        }
    }

    /**
     * @see de.freese.pim.core.service.MailService#getMailAccounts()
     */
    @Override
    @Transactional(readOnly = true)
    public List<MailAccount> getMailAccounts() {
        getLogger().info("load accounts");

        return getMailDAO().getMailAccounts();
    }

    /**
     * @see de.freese.pim.core.service.MailService#insertAccount(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    @Transactional
    public long insertAccount(final MailAccount account) {
        getMailDAO().insertAccount(account);

        return account.getID();
    }

    /**
     * @see de.freese.pim.core.service.MailService#insertFolder(long, java.util.List)
     */
    @Override
    @Transactional
    public long[] insertFolder(final long accountID, final List<MailFolder> folders) {
        getMailDAO().insertFolder(accountID, folders);

        return folders.stream().mapToLong(MailFolder::getID).toArray();
    }

    /**
     * @see de.freese.pim.core.service.MailService#loadFolder(long)
     */
    @Override
    @Transactional
    public List<MailFolder> loadFolder(final long accountID) {
        MailApi mailAPI = getMailAPI(accountID);

        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);

        if ((folder == null) || folder.isEmpty()) {
            folder = mailAPI.getFolder();

            long[] primaryKeys = insertFolder(accountID, folder);

            for (int i = 0; i < primaryKeys.length; i++) {
                folder.get(i).setID(primaryKeys[i]);
            }

            getLogger().debug("new folder saved: affected rows={}", primaryKeys.length);
        }

        return folder;
    }

    /**
     * @see de.freese.pim.core.service.MailService#loadMailContent(long, java.lang.String, long, de.freese.pim.core.utils.io.IOMonitor)
     */
    @Override
    public MailContent loadMailContent(final long accountID, final String folderFullName, final long mailUID, final IOMonitor monitor) {
        getLogger().debug("download mail: accountID={}, folderFullName={}, uid={}", accountID, folderFullName, mailUID);

        MailApi mailAPI = getMailAPI(accountID);

        return mailAPI.loadMail(folderFullName, mailUID, monitor);
    }

    /**
     * @see de.freese.pim.core.service.MailService#loadMails(long, long, java.lang.String)
     */
    @Override
    @Transactional
    public List<Mail> loadMails(final long accountID, final long folderID, final String folderFullName) {
        getLogger().info("Load Mails: account={}, folder={}", accountID, folderFullName);

        MailApi mailAPI = getMailAPI(accountID);

        Map<Long, Mail> mailMap = getMailDAO().getMails(folderID).stream().collect(Collectors.toMap(Mail::getUID, Function.identity()));

        // Höchste UID finden.
        long uidFrom = mailMap.values().parallelStream().mapToLong(Mail::getUID).max().orElse(1);

        // Alle Mails lokal löschen, die zwischenzeitlich auch Remote gelöscht worden sind.
        Set<Long> currentUIDs = mailAPI.loadMessageIDs(folderFullName);

        Set<Long> remoteDeletedUIDs = new HashSet<>();
        remoteDeletedUIDs.addAll(mailMap.keySet());
        remoteDeletedUIDs.removeAll(currentUIDs);

        for (Long uid : remoteDeletedUIDs) {
            getLogger().debug("delete mail: uid={}", uid);

            getMailDAO().deleteMail(folderID, uid);
            mailMap.remove(uid);
        }

        if (uidFrom > 1) {
            // Neue Mails holen, ausser der aktuellsten geladenen.
            uidFrom += 1;
        }

        List<Mail> newMails = mailAPI.loadMails(folderFullName, uidFrom);

        if (newMails == null) {
            int affectedRows = getMailDAO().deleteMails(folderID);
            affectedRows += getMailDAO().deleteFolder(folderID);
            final int rows = affectedRows;

            getLogger().debug("folder deleted: affected rows={}", rows);

            return Collections.emptyList();
        }

        if (!newMails.isEmpty()) {
            int[] affectedRows = getMailDAO().insertMail(folderID, newMails);

            getLogger().debug("new mails saved: affected rows={}", IntStream.of(affectedRows).sum());
        }

        List<Mail> mails = new ArrayList<>();
        mails.addAll(mailMap.values());
        mails.addAll(newMails);

        mails.forEach(m -> m.setFolderFullName(folderFullName));

        return mails;
    }

    /**
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Resource
    public void setMailDAO(final MailDAO mailDAO) {
        this.mailDAO = mailDAO;
    }

    /**
     * @see de.freese.pim.core.service.MailService#test(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    public List<MailFolder> test(final MailAccount account) {
        List<MailFolder> folder = null;

        MailApi mailAPI = new JavaMailApi(account);

        try {
            mailAPI.connect();
            mailAPI.testConnection();

            folder = mailAPI.getFolder();
        }
        finally {
            try {
                mailAPI.disconnect();
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage());
            }
        }

        return folder;
    }

    /**
     * @see de.freese.pim.core.service.MailService#updateAccount(de.freese.pim.core.model.mail.MailAccount)
     */
    @Override
    @Transactional
    public int updateAccount(final MailAccount account) {
        return getMailDAO().updateAccount(account);
    }

    /**
     * @see de.freese.pim.core.service.MailService#updateFolder(long, java.util.List)
     */
    @Override
    @Transactional
    public int[] updateFolder(final long accountID, final List<MailFolder> folders) {
        int[] affectedRows = new int[folders.size()];

        for (int i = 0; i < folders.size(); i++) {
            MailFolder mf = folders.get(i);

            affectedRows[i] = getMailDAO().updateFolder(mf);
        }

        return affectedRows;
    }

    protected void disconnectMailAPI(final long accountID) {
        String beanName = getAccountBeanName(accountID);

        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) getBeanFactory();
        MailApi mailAPI = bf.getBean(beanName, MailApi.class);

        getLogger().info("Close {}", mailAPI.getAccount().getMail());

        try {
            mailAPI.disconnect();
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage());
        }

        bf.destroySingleton(beanName);
    }

    protected BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    protected MailApi getMailAPI(final long accountID) {
        String beanName = getAccountBeanName(accountID);

        return getApplicationContext().getBean(beanName, MailApi.class);
    }

    protected MailDAO getMailDAO() {
        return this.mailDAO;
    }

    private String getAccountBeanName(final long accountID) {
        return "mailAPI-" + accountID;
    }
}
