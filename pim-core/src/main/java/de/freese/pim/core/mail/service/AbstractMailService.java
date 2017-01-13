// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.model_new.MailAccount;
import de.freese.pim.core.mail.model_new.MailFolder;

/**
 * Basis-Implementierung des {@link IMailService}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailService implements IMailService
{
    /**
     *
     */
    private MailAccount account = null;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMailService}
     */
    public AbstractMailService()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.service.IMailService#setAccount(de.freese.pim.core.mail.model_new.MailAccount)
     */
    @Override
    public void setAccount(final MailAccount account)
    {
        this.account = account;
    }

    /**
     * @return {@link MailAccount}
     */
    protected MailAccount getAccount()
    {
        return this.account;
    }

    /**
     * Liefert die Children des Folders.
     *
     * @param parent {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected abstract List<MailFolder> getChildFolder(MailFolder parent) throws Exception;

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
