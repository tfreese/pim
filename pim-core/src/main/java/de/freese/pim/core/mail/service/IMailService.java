// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;

import de.freese.pim.core.mail.model_new.Mail;
import de.freese.pim.core.mail.model_new.MailAccount;
import de.freese.pim.core.mail.model_new.MailFolder;
import javafx.collections.ObservableList;

/**
 * Interface für den Service des Mail-Clients<br>
 *
 * @author Thomas Freese
 */
public interface IMailService
{
    /**
     * Initialisiert den Service mit der konkreten Mail-API.
     *
     * @throws Exception Falls was schief geht.
     */
    public void connect() throws Exception;

    /**
     * Liefert die Mails des Folders.
     *
     * @param folder {@link MailFolder}
     * @return {@link ObservableList}
     * @throws Exception Falls was schief geht.
     */
    public ObservableList<Mail> getMails(MailFolder folder) throws Exception;

    /**
     * Liefert die neuen Mails des Folders.
     *
     * @param folder {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> getNewMails(MailFolder folder) throws Exception;

    /**
     * Liefert die Root-/Top-Level Folder des Accounts und synchronisiert den lokalen Cache.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getRootFolder() throws Exception;

    /**
     * Setzt den Account des Services.
     *
     * @param account {@link MailAccount}
     */
    public void setAccount(MailAccount account);
}
