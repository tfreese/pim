// Created: 20.01.2017
package de.freese.pim.server.mail.service;

import java.util.List;

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
     */
    void connectAccount(MailAccount account);

    /**
     * Löschen eines {@link MailAccount}.<br>
     *
     * @param accountID long
     *
     * @return int; affectedRows
     */
    int deleteAccount(long accountID);

    /**
     * Schliessen der MailAPI-Verbindung der MailAccounts.
     *
     * @param accountIDs long[]
     */
    void disconnectAccounts(long...accountIDs);

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     *
     * @return {@link List}
     */
    List<MailAccount> getMailAccounts();

    /**
     * Anlegen eines neuen {@link MailAccount}.<br>
     *
     * @param account {@link MailAccount}
     *
     * @return long; PrimaryKey
     */
    long insertAccount(MailAccount account);

    /**
     * Anlegen von {@link MailFolder}.<br>
     *
     * @param accountID long
     * @param folders {@link List}
     *
     * @return long[]; PrimaryKeys
     */
    long[] insertFolder(long accountID, List<MailFolder> folders);

    /**
     * Lädt die Folder des Accounts.
     *
     * @param accountID long
     *
     * @return {@link List}
     */
    List<MailFolder> loadFolder(long accountID);

    /**
     * Liefert den Inhalt der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param accountID long
     * @param folderFullName String
     * @param mailUID long
     * @param monitor {@link IOMonitor}, optional
     *
     * @return {@link MailContent}
     */
    MailContent loadMailContent(long accountID, String folderFullName, long mailUID, IOMonitor monitor);

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param accountID long
     * @param folderID long
     * @param folderFullName String
     *
     * @return {@link List}
     */
    List<Mail> loadMails(long accountID, long folderID, String folderFullName);

    /**
     * Testet die Verbindung zu einem MailAccount und liefert bei Erfolg dessen Ordner.
     *
     * @param account {@link MailAccount}
     *
     * @return {@link List}
     */
    List<MailFolder> test(MailAccount account);

    /**
     * Ändern eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     *
     * @return int; affectedRows
     */
    int updateAccount(MailAccount account);

    /**
     * Ändern von {@link MailFolder}.
     *
     * @param accountID long
     * @param folders {@link List}
     *
     * @return int[]; affectedRows
     */
    int[] updateFolder(long accountID, List<MailFolder> folders);
}
