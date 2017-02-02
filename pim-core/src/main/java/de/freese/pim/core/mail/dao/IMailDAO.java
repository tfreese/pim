/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

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
     * @throws Exception Falls was schief geht.
     */
    public void deleteAccount(long accountID) throws Exception;

    /**
     * Löschen eines {@link MailFolder}.<br>
     *
     * @param folderID long
     * @throws Exception Falls was schief geht.
     */
    public void deleteFolder(long folderID) throws Exception;

    /**
     * Löschen einer {@link Mail}.<br>
     * 
     * @param folderID long
     * @param uid long
     * @throws Exception Falls was schief geht.
     */
    public void deleteMail(long folderID, long uid) throws Exception;

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
     * @throws Exception Falls was schief geht.
     */
    public void insertAccount(MailAccount account) throws Exception;

    /**
     * Anlegen eines neuen {@link MailFolder}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param folder {@link MailFolder}
     * @param accountID long
     * @throws Exception Falls was schief geht.
     */
    public void insertFolder(MailFolder folder, long accountID) throws Exception;

    /**
     * Anlegen einer neuen {@link Mail}.<br>
     * Die Mail hat keinen eigene PrimaryKey.
     *
     * @param mail {@link Mail}
     * @param folderID long
     * @throws Exception Falls was schief geht.
     */
    public void insertMail(Mail mail, long folderID) throws Exception;

    /**
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     * @throws Exception Falls was schief geht.
     */
    public void updateAccount(MailAccount account) throws Exception;

    /**
     * Ändern eines {@link MailFolder}.
     *
     * @param folder {@link MailFolder}
     * @throws Exception Falls was schief geht.
     */
    public void updateFolder(MailFolder folder) throws Exception;

    /**
     * Ändern einer {@link Mail}.<br>
     * Hier wird nur das SEEN-Flag aktualisiert.
     *
     * @param mail {@link Mail}
     * @throws Exception Falls was schief geht.
     */
    public void updateMail(Mail mail) throws Exception;
}
