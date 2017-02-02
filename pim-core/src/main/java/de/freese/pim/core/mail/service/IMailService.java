// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;

import de.freese.pim.core.mail.dao.IMailDAO;
import de.freese.pim.core.mail.model.MailFolder;

/**
 * Interface für den Service der Mails.<br>
 *
 * @author Thomas Freese
 */
public interface IMailService extends IMailDAO
{
    /**
     * Anlegen oder ändern von {@link MailFolder}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param folders {@link List}
     * @param accountID long
     * @throws Exception Falls was schief geht.
     */
    public void insertOrUpdate(List<MailFolder> folders, long accountID) throws Exception;
}
