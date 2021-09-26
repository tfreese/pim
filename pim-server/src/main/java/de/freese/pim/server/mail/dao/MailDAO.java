// Created: 14.01.2017
package de.freese.pim.server.mail.dao;

import java.util.Collection;
import java.util.List;

import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;

/**
 * @author Thomas Freese
 */
public interface MailDAO
{
    /**
     * Löschen eines {@link MailAccount}.<br>
     *
     * @param accountID long
     *
     * @return int; affectedRows
     */
    int deleteAccount(long accountID);

    /**
     * Löschen eines {@link MailFolder}.<br>
     *
     * @param folderID long
     *
     * @return int; affectedRows
     */
    int deleteFolder(long folderID);

    /**
     * Löschen aller {@link MailFolder} eines Accounts.<br>
     *
     * @param accountID long
     *
     * @return int; affectedRows
     */
    int deleteFolders(long accountID);

    /**
     * Löschen einer {@link Mail}.<br>
     *
     * @param folderID long
     * @param uid long
     *
     * @return int; affectedRows
     */
    int deleteMail(long folderID, long uid);

    /**
     * Löschen aller {@link Mail}s des Folders.<br>
     *
     * @param folderID long
     *
     * @return int; affectedRows
     */
    int deleteMails(long folderID);

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     *
     * @return {@link List}
     */
    List<MailAccount> getMailAccounts();

    /**
     * Liefert alle Folder des Mail-Accounts, sortiert nach FULLNAME.
     *
     * @param accountID long
     *
     * @return {@link List}
     */
    List<MailFolder> getMailFolder(long accountID);

    /**
     * Liefert alle Mails des Folders.
     *
     * @param folderID long
     *
     * @return {@link List}
     */
    List<Mail> getMails(long folderID);

    /**
     * Anlegen eines neuen {@link MailAccount}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link MailAccount}
     *
     * @return int; affectedRows
     */
    int insertAccount(MailAccount account);

    /**
     * Anlegen von neuen {@link MailFolder}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param accountID long
     * @param folders {@link Collection}
     *
     * @return int[]; affectedRows
     */
    int[] insertFolder(long accountID, Collection<MailFolder> folders);

    /**
     * Anlegen einer neuen {@link Mail}.<br>
     * Die Mail hat keinen eigene PrimaryKey.
     *
     * @param folderID long
     * @param mails {@link Collection}
     *
     * @return int[]; affectedRows
     */
    int[] insertMail(long folderID, Collection<Mail> mails);

    /**
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     *
     * @return int; affectedRows
     */
    int updateAccount(MailAccount account);

    /**
     * Ändern eines {@link MailFolder}.
     *
     * @param folder {@link MailFolder}
     *
     * @return int; affectedRows
     */
    int updateFolder(MailFolder folder);

    /**
     * Ändern einer {@link Mail}.<br>
     * Hier wird nur das SEEN-Flag aktualisiert.
     *
     * @param folderID long
     * @param mail {@link Mail}
     *
     * @return int; affectedRows
     */
    int updateMail(long folderID, Mail mail);
}
