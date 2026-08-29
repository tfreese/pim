package de.freese.pim.core.service;

import java.util.List;

import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.mail.api.MailApi;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.utils.io.IOMonitor;

/**
 * Interface für den Service der Mails.<br>
 *
 * @author Thomas Freese
 * @since 20.01.2017
 */
public interface MailService {
    /**
     * Erstellt für den Account eine Instanz von Typ {@link MailApi} und stellt die Verbindung her.<br>
     */
    void connectAccount(MailAccount account);

    /**
     * @return int; affectedRows
     */
    int deleteAccount(long accountID);

    /**
     * Schliessen der MailApi-Verbindung der MailAccounts.
     */
    void disconnectAccounts(long... accountIDs);

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     */
    List<MailAccount> getMailAccounts();

    /**
     * @return long; PrimaryKey
     */
    long insertAccount(MailAccount account);

    /**
     * @return long[]; PrimaryKeys
     */
    long[] insertFolder(long accountID, List<MailFolder> folders);

    /**
     * Lädt die Folder des Accounts.
     */
    List<MailFolder> loadFolder(long accountID);

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param monitor {@link IOMonitor}, optional
     */
    MailContent loadMailContent(long accountID, String folderFullName, long mailUID, IOMonitor monitor);

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     */
    List<Mail> loadMails(long accountID, long folderID, String folderFullName);

    /**
     * Testet die Verbindung zu einem MailAccount und liefert bei Erfolg dessen Ordner.
     */
    List<MailFolder> test(MailAccount account);

    /**
     * @return int; affectedRows
     */
    int updateAccount(MailAccount account);

    /**
     * @return int[]; affectedRows
     */
    int[] updateFolder(long accountID, List<MailFolder> folders);
}
