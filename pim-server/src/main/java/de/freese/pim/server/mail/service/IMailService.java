// Created: 20.01.2017
package de.freese.pim.server.mail.service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

import de.freese.pim.server.mail.api.IMailAPI;
import de.freese.pim.server.mail.api.IMailContent;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;

/**
 * Interface für den Service der Mails.<br>
 *
 * @author Thomas Freese
 */
public interface IMailService
{
    /**
     * Erstellt für den Account eine Instanz von Typ {@link IMailAPI} und verbindet stellt die Verbindung her.<br>
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
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link MailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int insertAccount(MailAccount account) throws Exception;

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
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param mail {@link Mail}
     * @param loadMonitor {@link BiConsumer}
     * @return {@link IMailContent}
     * @throws Exception Falls was schief geht.
     */
    public IMailContent loadContent(long accountID, Mail mail, BiConsumer<Long, Long> loadMonitor) throws Exception;

    /**
     * Lädt die Folder des Accounts.
     *
     * @param accountID long
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> loadFolder(long accountID) throws Exception;

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
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     * @return int; affectedRows
     * @throws Exception Falls was schief geht.
     */
    public int updateAccount(MailAccount account) throws Exception;
}
