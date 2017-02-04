// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.persistence.Transactional;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
public class DefaultMailService implements IMailService
{
    /**
    *
    */
    private final IMailDAO mailDAO;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultMailService}
     *
     * @param mailDAO {@link IMailDAO}
     */
    public DefaultMailService(final IMailDAO mailDAO)
    {
        super();

        Objects.requireNonNull(mailDAO, "mailDAO required");

        this.mailDAO = mailDAO;
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
     * @see de.freese.pim.core.mail.service.IMailService#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        return this.mailDAO.getMailAccounts();
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
     * @see de.freese.pim.core.mail.service.IMailService#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public int updateAccount(final MailAccount account) throws Exception
    {
        return this.mailDAO.updateAccount(account);
    }
}
