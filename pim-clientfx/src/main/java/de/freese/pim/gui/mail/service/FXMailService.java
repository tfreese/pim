/**
 * Created: 14.02.2017
 */

package de.freese.pim.gui.mail.service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import de.freese.pim.gui.mail.model.FXMail;
import de.freese.pim.gui.mail.model.FXMailAccount;
import de.freese.pim.gui.mail.model.FXMailFolder;
import de.freese.pim.server.mail.api.MailAPI;
import de.freese.pim.server.mail.api.MailContent;

/**
 * Interface für einen JavaFX-MailService.
 *
 * @author Thomas Freese
 */
public interface FXMailService
{
    /**
     * Erstellt für den Account eine Instanz von Typ {@link MailAPI} und verbindet stellt die Verbindung her.<br>
     *
     * @param account {@link FXMailAccount}
     * @throws Exception Falls was schief geht.
     */
    public void connectAccount(FXMailAccount account) throws Exception;

    /**
     * Löschen eines MailAccounts.<br>
     *
     * @param accountID long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteAccount(long accountID) throws Exception;

    /**
     * Schliessen der MailApi-Verbindungen aller MailAccounts.
     *
     * @throws Exception Falls was schief geht.
     */
    public void disconnectAccounts() throws Exception;

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<FXMailAccount> getMailAccounts() throws Exception;

    /**
     * Anlegen eines neuen MailAccounts.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link FXMailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int insertAccount(FXMailAccount account) throws Exception;

    /**
     * Anlegen oder ändern von MailFoldern.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param accountID long
     * @param folders {@link List}
     * @return int[]; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int[] insertOrUpdateFolder(long accountID, List<FXMailFolder> folders) throws Exception;

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param mail {@link FXMail}
     * @param loadMonitor {@link BiConsumer}
     * @return {@link MailContent}
     * @throws Exception Falls was schief geht.
     */
    public MailContent loadContent(long accountID, FXMail mail, BiConsumer<Long, Long> loadMonitor) throws Exception;

    /**
     * Lädt die Folder des Accounts.
     *
     * @param accountID long
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<FXMailFolder> loadFolder(long accountID) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<FXMail> loadMails(long accountID, long folderID, String folderFullName) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public Future<List<FXMail>> loadMails2(long accountID, long folderID, String folderFullName) throws Exception;

    /**
     * Ändern eines MailAccounts.
     *
     * @param account {@link FXMailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateAccount(FXMailAccount account) throws Exception;
}
