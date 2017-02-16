// Created: 20.01.2017
package de.freese.pim.server.mail.service;

import java.util.List;
import java.util.concurrent.Future;
import org.springframework.web.context.request.async.DeferredResult;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
import de.freese.pim.server.mail.api.MailAPI;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;

/**
 * Interface für den Service der Mails.<br>
 *
 * @author Thomas Freese
 */
public interface MailService
{
    /**
     * Erstellt für den Account eine Instanz von Typ {@link MailAPI} und verbindet stellt die Verbindung her.<br>
     *
     * @param account {@link MailAccount}
     * @throws Exception Falls was schief geht.
     */
    public void connectAccount(MailAccount account) throws Exception;

    /**
     * Löschen eines {@link MailAccount}.<br>
     *
     * @param accountID long
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int deleteAccount(long accountID) throws Exception;

    /**
     * Schliessen der MailApi-Verbindung aller MailAccounts.
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
    public List<MailAccount> getMailAccounts() throws Exception;

    /**
     * Anlegen eines neuen {@link MailAccount}.<br>
     *
     * @param account {@link MailAccount}
     * @return long; PrimaryKey
     * @throws Exception Falls was schief geht.
     */
    public long insertAccount(MailAccount account) throws Exception;

    /**
     * Anlegen von {@link MailFolder}.<br>
     *
     * @param accountID long
     * @param folders {@link List}
     * @return long[]; PrimaryKeys
     * @throws Exception Falls was schief geht.
     */
    public long[] insertFolder(long accountID, List<MailFolder> folders) throws Exception;

    /**
     * Lädt die Folder des Accounts.
     *
     * @param accountID long
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> loadFolder(long accountID) throws Exception;

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param folderFullName String
     * @param mailUID long
     * @param monitor {@link IOMonitor}, optional
     * @return {@link MailContent}
     * @throws Exception Falls was schief geht.
     */
    public MailContent loadMailContent(long accountID, String folderFullName, long mailUID, IOMonitor monitor) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> loadMails(long accountID, long folderID, String folderFullName) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public Future<List<Mail>> loadMails2(long accountID, long folderID, String folderFullName) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public DeferredResult<List<Mail>> loadMails3(long accountID, long folderID, String folderFullName) throws Exception;

    /**
     * Testet die Verbindung zu einem MailAccount und liefert bei Erfolg dessen Ordner.
     *
     * @param account {@link MailAccount}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> test(MailAccount account) throws Exception;

    /**
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateAccount(MailAccount account) throws Exception;

    /**
     * Ändern von {@link MailFolder}.
     *
     * @param accountID long
     * @param folders {@link List}
     * @return int[]; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int[] updateFolder(long accountID, List<MailFolder> folders) throws Exception;
}
