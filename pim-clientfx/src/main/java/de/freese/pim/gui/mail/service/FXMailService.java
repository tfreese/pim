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
    public void connectAccount(FXMailAccount account);

    /**
     * Löschen eines MailAccounts.<br>
     *
     * @param accountID long
     * @return int; affectedRows
     * @throws PIMException Falls was schief geht.
     */
    public int deleteAccount(long accountID);

    /**
     * Schliessen der MailApi-Verbindungen der MailAccounts.
     *
     * @param accountIDs long[]
     * @throws PIMException Falls was schief geht.
     */
    public void disconnectAccounts(long...accountIDs);

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     *
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMailAccount> getMailAccounts();

    /**
     * Anlegen eines neuen MailAccounts.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link FXMailAccount}
     * @throws PIMException Falls was schief geht.
     */
    public void insertAccount(FXMailAccount account);

    /**
     * Anlegen oder ändern von MailFoldern.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param accountID long
     * @param folders {@link List}
     * @return int; affectedRows
     * @throws PIMException Falls was schief geht.
     */
    public int insertOrUpdateFolder(long accountID, List<FXMailFolder> folders);

    /**
     * Lädt die Folder des Accounts.
     *
     * @param accountID long
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMailFolder> loadFolder(long accountID);

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param account {@link FXMailAccount}
     * @param mail {@link FXMail}
     * @param monitor {@link IOMonitor}
     * @return {@link MailContent}
     * @throws PIMException Falls was schief geht.
     */
    public MailContent loadMailContent(FXMailAccount account, FXMail mail, IOMonitor monitor);

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param account {@link FXMailAccount}
     * @param folder {@link FXMailFolder}
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMail> loadMails(FXMailAccount account, FXMailFolder folder);

    /**
     * Testet die Verbindung zu einem MailAccount und liefert bei Erfolg dessen Ordner.
     *
     * @param account {@link FXMailAccount}
     * @return {@link List}
     * @throws PIMException Falls was schief geht.
     */
    public List<FXMailFolder> test(FXMailAccount account);

    /**
     * Ändern eines MailAccounts.
     *
     * @param account {@link FXMailAccount}
     * @return int; affectedRows
     * @throws PIMException Falls was schief geht.
     */
    public int updateAccount(FXMailAccount account);
}
