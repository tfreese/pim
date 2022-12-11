// Created: 13.01.2017
package de.freese.pim.core.mail.api;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import de.freese.pim.core.function.ExceptionalConsumer;
import de.freese.pim.core.function.ExceptionalFunction;
import de.freese.pim.core.mail.MailContent;
import de.freese.pim.core.model.mail.Mail;
import de.freese.pim.core.model.mail.MailAccount;
import de.freese.pim.core.model.mail.MailFolder;
import de.freese.pim.core.utils.io.IOMonitor;

/**
 * Interface für die Mail-API.<br>
 *
 * @author Thomas Freese
 */
public interface MailApi
{
    /**
     * Initialisiert den Service mit der konkreten Mail-API.
     */
    void connect();

    /**
     * Schliessen der Verbindung.
     */
    void disconnect();

    /**
     * Liefert den Account des Services.
     */
    MailAccount getAccount();

    /**
     * Liefert alle Folder des Accounts.
     */
    List<MailFolder> getFolder();

    /**
     * Holt die Mail vom Provider und übergibt sie in dem {@link ExceptionalConsumer}.<br>
     */
    <T> T loadMail(String folderFullName, long uid, ExceptionalFunction<Object, T, Exception> function);

    /**
     * Holt die Mail vom Provider und übergibt sie in dem {@link ExceptionalConsumer}.<br>
     *
     * @param monitor {@link IOMonitor}, optional
     */
    MailContent loadMail(String folderFullName, long uid, IOMonitor monitor);

    /**
     * Lädt die Mails des Folders vom Provider ab der definierten UID.<br>
     * Die Liste ist null, wenn der Folder nicht mehr existent.
     *
     * @param uidFrom long; Startindex der zu Ladenen Mails
     */
    List<Mail> loadMails(String folderFullName, long uidFrom);

    /**
     * Liefert die aktuellen Message-UIDs im Folder.<br>
     */
    Set<Long> loadMessageIDs(String folderFullName);

    /**
     * Optionaler {@link Executor} für die Mail-API.
     */
    void setExecutor(final Executor executor);

    /**
     * Setzt das SEEN-Flag einer Mail.
     */
    void setSeen(Mail mail, boolean seen);

    /**
     * Testet die Verbindung.
     */
    void testConnection();
}
