// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.ArrayUtils;
import de.freese.pim.core.jdbc.tx.Transactional;
import de.freese.pim.core.mail.api.IMailAPI;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.impl.JavaMailAPI;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.service.ISettingsService;

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
    private ExecutorService executorService = null;

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
    private ISettingsService settingsService = null;

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
        Path basePath = this.settingsService.getHome();
        Path accountPath = basePath.resolve(account.getMail());

        IMailAPI mailAPI = new JavaMailAPI(account, accountPath);
        mailAPI.setMailService(this);
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
        List<MailFolder> folder = this.mailDAO.getMailFolder(accountID);
        int affectedRows = 0;

        for (MailFolder mf : folder)
        {
            affectedRows += this.mailDAO.deleteMails(mf.getID());
        }

        affectedRows += this.mailDAO.deleteFolders(accountID);
        affectedRows += this.mailDAO.deleteAccount(accountID);

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#deleteFolder(long)
     */
    @Override
    @Transactional
    public int deleteFolder(final long folderID) throws Exception
    {
        int affectedRows = this.mailDAO.deleteMails(folderID);
        affectedRows += this.mailDAO.deleteFolder(folderID);

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#disconnectAccounts()
     */
    @Override
    public void disconnectAccounts() throws Exception
    {
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
        return this.mailDAO.getMailAccounts();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getMailAPI(long)
     */
    @Override
    public IMailAPI getMailAPI(final long accountID)
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

    /**
     * @see de.freese.pim.core.mail.service.IMailService#getMailFolder(long)
     */
    @Override
    public List<MailFolder> getMailFolder(final long accountID) throws Exception
    {
        return this.mailDAO.getMailFolder(accountID);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMails(long)
     */
    @Override
    @Transactional
    public List<Mail> getMails(final long folderID) throws Exception
    {
        return this.mailDAO.getMails(folderID);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public int insertAccount(final MailAccount account) throws Exception
    {
        return this.mailDAO.insertAccount(account);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertMails(long, java.util.List)
     */
    @Override
    @Transactional
    public int[] insertMails(final long folderID, final List<Mail> mails) throws Exception
    {
        return this.mailDAO.insertMail(folderID, mails);
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
        int[] affectedRows = this.mailDAO.insertFolder(accountID, toInsert);

        // ID != 0 -> update
        List<MailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).collect(Collectors.toList());

        for (MailFolder mf : toUpdate)
        {
            ArrayUtils.add(affectedRows, this.mailDAO.updateFolder(mf));
        }

        return affectedRows;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#loadFolder(long)
     */
    @Override
    @Transactional
    public List<MailFolder> loadFolder(final long accountID) throws Exception
    {
        IMailAPI mailAPI = this.mailApiMap.get(accountID);

        List<MailFolder> folder = getMailDAO().getMailFolder(accountID);

        if ((folder == null) || folder.isEmpty())
        {
            folder = mailAPI.getFolderRemote();

            int[] affectedRows = insertOrUpdateFolder(accountID, folder);

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("new folder saved: number={}", IntStream.of(affectedRows).sum());
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
     * @param executorService {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    /**
     * @param mailDAO {@link IMailDAO}
     */
    public void setMailDAO(final IMailDAO mailDAO)
    {
        this.mailDAO = mailDAO;
    }

    /**
     * @param settingsService {@link ISettingsService}
     */
    public void setSettingsService(final ISettingsService settingsService)
    {
        this.settingsService = settingsService;
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public int updateAccount(final MailAccount account) throws Exception
    {
        return this.mailDAO.updateAccount(account);
    }
}
