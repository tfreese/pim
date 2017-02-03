// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public void deleteAccount(final long accountID) throws Exception
    {
        List<MailFolder> folder = this.mailDAO.getMailFolder(accountID);

        for (MailFolder mf : folder)
        {
            this.mailDAO.deleteMails(mf.getID());
        }

        this.mailDAO.deleteFolders(accountID);
        this.mailDAO.deleteAccount(accountID);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#deleteFolder(long)
     */
    @Override
    @Transactional
    public void deleteFolder(final long folderID) throws Exception
    {
        this.mailDAO.deleteMails(folderID);
        this.mailDAO.deleteFolder(folderID);
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
    public void insertAccount(final MailAccount account) throws Exception
    {
        this.mailDAO.insertAccount(account);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertMail(long, de.freese.pim.core.mail.model.Mail)
     */
    @Override
    @Transactional
    public void insertMail(final long folderID, final Mail mail) throws Exception
    {
        this.mailDAO.insertMail(mail, folderID);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertOrUpdateFolder(long, java.util.List)
     */
    @Override
    @Transactional
    public void insertOrUpdateFolder(final long accountID, final List<MailFolder> folders) throws Exception
    {
        // ID = 0 -> insert
        List<MailFolder> toInsert = folders.stream().filter(mf -> mf.getID() == 0).collect(Collectors.toList());

        for (MailFolder mf : toInsert)
        {
            this.mailDAO.insertFolder(mf, accountID);
        }

        // ID != 0 -> update
        List<MailFolder> toUpdate = folders.stream().filter(mf -> mf.getID() > 0).collect(Collectors.toList());

        for (MailFolder mf : toUpdate)
        {
            this.mailDAO.updateFolder(mf);
        }
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public void updateAccount(final MailAccount account) throws Exception
    {
        this.mailDAO.updateAccount(account);
    }
}
