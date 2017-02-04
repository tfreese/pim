/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.util.Collection;
import java.util.List;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;

/**
 * @author Thomas Freese
 */
public interface IMailDAO
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
     * Löschen aller {@link MailFolder} eines Accounts.<br>
     *
     * @param accountID long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteFolders(long accountID) throws Exception;

    /**
     * Löschen einer {@link Mail}.<br>
     *
     * @param folderID long
     * @param uid long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteMail(long folderID, long uid) throws Exception;

    /**
     * Löschen aller {@link Mail}s des Folders.<br>
     *
     * @param folderID long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteMails(long folderID) throws Exception;

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
     * Anlegen von neuen {@link MailFolder}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param accountID long
     * @param folders {@link Collection}
     * @return int[]; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int[] insertFolder(long accountID, Collection<MailFolder> folders) throws Exception;

    /**
     * Anlegen einer neuen {@link Mail}.<br>
     * Die Mail hat keinen eigene PrimaryKey.
     *
     * @param folderID long
     * @param mails {@link Collection}
     * @return int[]; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int[] insertMail(long folderID, Collection<Mail> mails) throws Exception;

    /**
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateAccount(MailAccount account) throws Exception;

    /**
     * Ändern eines {@link MailFolder}.
     *
     * @param folder {@link MailFolder}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateFolder(MailFolder folder) throws Exception;

    /**
     * Ändern einer {@link Mail}.<br>
     * Hier wird nur das SEEN-Flag aktualisiert.
     *
     * @param mail {@link Mail}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateMail(Mail mail) throws Exception;
}
