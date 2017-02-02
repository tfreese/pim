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
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteAccount(long)
     */
    @Override
    @Transactional
    public void deleteAccount(final long accountID) throws Exception
    {
        this.mailDAO.deleteAccount(accountID);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteFolder(long)
     */
    @Override
    public void deleteFolder(final long folderID) throws Exception
    {
        this.mailDAO.deleteFolder(folderID);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#deleteMail(long, long)
     */
    @Override
    @Transactional
    public void deleteMail(final long folderID, final long uid) throws Exception
    {
        this.mailDAO.deleteMail(folderID, uid);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        return this.mailDAO.getMailAccounts();
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailFolder(long)
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
    public List<Mail> getMails(final long folderID) throws Exception
    {
        return this.mailDAO.getMails(folderID);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public void insertAccount(final MailAccount account) throws Exception
    {
        this.mailDAO.insertAccount(account);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertFolder(de.freese.pim.core.mail.model.MailFolder, long)
     */
    @Override
    @Transactional
    public void insertFolder(final MailFolder folder, final long accountID) throws Exception
    {
        this.mailDAO.insertFolder(folder, accountID);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insertMail(de.freese.pim.core.mail.model.Mail, long)
     */
    @Override
    @Transactional
    public void insertMail(final Mail mail, final long folderID) throws Exception
    {
        this.mailDAO.insertMail(mail, folderID);
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#insertOrUpdate(java.util.List, long)
     */
    @Override
    @Transactional
    public void insertOrUpdate(final List<MailFolder> folders, final long accountID) throws Exception
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
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateAccount(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public void updateAccount(final MailAccount account) throws Exception
    {
        this.mailDAO.updateAccount(account);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateFolder(de.freese.pim.core.mail.model.MailFolder)
     */
    @Override
    @Transactional
    public void updateFolder(final MailFolder folder) throws Exception
    {
        this.mailDAO.updateFolder(folder);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#updateMail(de.freese.pim.core.mail.model.Mail)
     */
    @Override
    @Transactional
    public void updateMail(final Mail mail) throws Exception
    {
        this.mailDAO.updateMail(mail);
    }
}
