// Created: 14.02.2017
package de.freese.pim.gui.mail.service;

import java.util.List;

import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.gui.mail.model.FxMail;
import de.freese.pim.gui.mail.model.FxMailAccount;
import de.freese.pim.gui.mail.model.FxMailFolder;

/**
 * Interface für einen JavaFX-MailService.
 *
 * @author Thomas Freese
 */
public interface FxMailService {
    /**
     * Erstellt für den Account eine Instanz von Typ der MailApi und stellt die Verbindung her.<br>
     */
    void connectAccount(FxMailAccount account);

    /**
     * @return int; affectedRows
     */
    int deleteAccount(long accountID);

    /**
     * Schliessen der MailApi-Verbindungen der MailAccounts.
     */
    void disconnectAccounts(long... accountIDs);

    /**
     * Liefert alle MailAccounts, sortiert nach MAIL.
     */
    List<FxMailAccount> getMailAccounts();

    /**
     * Die ID wird dabei in die Entity gesetzt.
     */
    void insertAccount(FxMailAccount account);

    /**
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @return int; affectedRows
     */
    int insertOrUpdateFolder(long accountID, List<FxMailFolder> folders);

    List<FxMailFolder> loadFolder(long accountID);

    /**
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     */
    MailContent loadMailContent(FxMailAccount account, FxMail mail, IOMonitor monitor);

    List<FxMail> loadMails(FxMailAccount account, FxMailFolder folder);

    List<FxMailFolder> test(FxMailAccount account);

    /**
     * @return int; affectedRows
     */
    int updateAccount(FxMailAccount account);
}
