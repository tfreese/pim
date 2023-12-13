// Created: 14.01.2017
package de.freese.pim.core.dao;

import java.util.Collection;
import java.util.List;

import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;

/**
 * @author Thomas Freese
 */
public interface MailDao {
    /**
     * @return int; affectedRows
     */
    int deleteAccount(long accountID);

    /**
     * @return int; affectedRows
     */
    int deleteFolder(long folderID);

    /**
     * @return int; affectedRows
     */
    int deleteFolders(long accountID);

    /**
     * @return int; affectedRows
     */
    int deleteMail(long folderID, long uid);

    /**
     * @return int; affectedRows
     */
    int deleteMails(long folderID);

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     */
    List<MailAccount> getMailAccounts();

    /**
     * Liefert alle Folder des Mail-Accounts, sortiert nach FULLNAME.
     */
    List<MailFolder> getMailFolder(long accountID);

    /**
     * Liefert alle Mails des Folders.
     */
    List<Mail> getMails(long folderID);

    /**
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @return int; affectedRows
     */
    int insertAccount(MailAccount account);

    /**
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @return int[]; affectedRows
     */
    int[] insertFolder(long accountID, Collection<MailFolder> folders);

    /**
     * Die Mail hat keinen eigenen PrimaryKey.
     *
     * @return int[]; affectedRows
     */
    int[] insertMail(long folderID, Collection<Mail> mails);

    /**
     * @return int; affectedRows
     */
    int updateAccount(MailAccount account);

    /**
     * @return int; affectedRows
     */
    int updateFolder(MailFolder folder);

    /**
     * Hier wird nur das SEEN-Flag aktualisiert.
     *
     * @return int; affectedRows
     */
    int updateMail(long folderID, Mail mail);
}
