// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;
import java.util.Objects;

import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.MailAccount;
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
     * @see de.freese.pim.core.mail.dao.IMailDAO#getMailAccounts()
     */
    @Override
    public List<MailAccount> getMailAccounts() throws Exception
    {
        return this.mailDAO.getMailAccounts();
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#insert(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public void insert(final MailAccount account) throws Exception
    {
        this.mailDAO.insert(account);
    }

    /**
     * @see de.freese.pim.core.mail.dao.IMailDAO#update(de.freese.pim.core.mail.model.MailAccount)
     */
    @Override
    @Transactional
    public void update(final MailAccount account) throws Exception
    {
        this.mailDAO.update(account);
    }
}
