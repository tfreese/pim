/**
 * Created: 14.02.2017
 */

package de.freese.pim.gui.mail.service;

import java.util.List;

import de.freese.pim.common.PIMException;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;

/**
 * Interface für einen JavaFX-MailService.
 *
 * @author Thomas Freese
 */
public interface FXMailService
{
    /**
     * Erstellt für den Account eine Instanz von Typ der MailAPI und stellt die Verbindung her.<br>
     *
     * @param account {@link FXMailAccount}
     * @throws PIMException Falls was schief geht.
     */
    public void connectAccount(FXMailAccount account) throws PIMException;

    /**
     * Löschen eines MailAccounts.<br>
     *
     * @param accountID long
     * @return int; affectedRows
     * @throws PIMException Falls was schief geht.
     */
    public int deleteAccount(long accountID) throws PIMException;

    /**
     * Schliessen der MailApi-Verbindungen der MailAccounts.
     *
     * @param accountIDs long[]
     * @throws PIMException Falls was schief geht.
     */
    public void disconnectAccounts(long... accountIDs) throws PIMException;

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     *
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMailAccount> getMailAccounts() throws PIMException;

    /**
     * Anlegen eines neuen MailAccounts.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link FXMailAccount}
     * @throws PIMException Falls was schief geht.
     */
    public void insertAccount(FXMailAccount account) throws PIMException;

    /**
     * Anlegen oder ändern von MailFoldern.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param accountID long
     * @param folders {@link List}
     * @return int; affectedRows
     * @throws PIMException Falls was schief geht.
     */
    public int insertOrUpdateFolder(long accountID, List<FXMailFolder> folders) throws PIMException;

    /**
     * Lädt die Folder des Accounts.
     *
     * @param accountID long
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMailFolder> loadFolder(long accountID) throws PIMException;

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param mail {@link FXMail}
     * @param monitor {@link IOMonitor}
     * @return {@link MailContent}
     * @throws PIMException Falls was schief geht.
     */
    public MailContent loadMailContent(long accountID, FXMail mail, IOMonitor monitor) throws PIMException;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMail> loadMails(long accountID, long folderID, String folderFullName) throws PIMException;

    // /**
    // * Lädt die Mails des Folders vom Provider und aus der DB.
    // *
    // * @param accountID long
    // * @param folderID long
    // * @param folderFullName String
    // * @return {@link List}
    // * @throws Exception Falls was schief geht.
    // */
    // public Future<List<FXMail>> loadMails2(long accountID, long folderID, String folderFullName) throws Exception;

    /**
     * Testet die Verbindung zu einem MailAccount und liefert bei Erfolg dessen Ordner.
     *
     * @param account {@link FXMailAccount}
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMailFolder> test(FXMailAccount account) throws PIMException;

    /**
     * Ändern eines MailAccounts.
     *
     * @param account {@link FXMailAccount}
     * @return int; affectedRows
     * @throws PIMException Falls was schief geht.
     */
    public int updateAccount(FXMailAccount account) throws PIMException;
}
