// Created: 20.01.2017
package de.freese.pim.core.mail.service;

import java.util.List;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;

/**
 * Interface für den Service der Mails.<br>
 *
 * @author Thomas Freese
 */
public interface IMailService
{
    /**
     * Löschen eines {@link MailAccount}.<br>
     *
     * @param accountID long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteAccount(long accountID) throws Exception;

    /**
     * Löschen eines {@link MailFolder}.<br>
     *
     * @param folderID long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteFolder(long folderID) throws Exception;

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailAccount> getMailAccounts() throws Exception;

    /**
     * Liefert alle Folder des Mail-Accounts, sortiert nach FULLNAME.
     *
     * @param accountID long
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getMailFolder(long accountID) throws Exception;

    /**
     * Liefert alle Mails des Folders.
     *
     * @param folderID long
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> getMails(long folderID) throws Exception;

    /**
     * Anlegen eines neuen {@link MailAccount}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link MailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int insertAccount(MailAccount account) throws Exception;

    /**
     * Anlegen von neuen {@link Mail}.<br>
     * Die Mail hat keinen eigene PrimaryKey.
     *
     * @param folderID long
     * @param mails {@link List}
     * @return int[]; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int[] insertMails(long folderID, List<Mail> mails) throws Exception;

    /**
     * Anlegen oder ändern von {@link MailFolder}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param accountID long
     * @param folders {@link List}
     * @return int[]; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int[] insertOrUpdateFolder(long accountID, List<MailFolder> folders) throws Exception;

    /**
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateAccount(MailAccount account) throws Exception;
}
