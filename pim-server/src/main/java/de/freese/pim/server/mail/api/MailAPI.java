// Created: 13.01.2017
package de.freese.pim.server.mail.api;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import de.freese.pim.common.function.ExceptionalConsumer;
import de.freese.pim.common.function.ExceptionalFunction;
import de.freese.pim.common.model.mail.MailContent;
import de.freese.pim.common.utils.io.IOMonitor;
import de.freese.pim.server.mail.model.Mail;
import de.freese.pim.server.mail.model.MailAccount;
import de.freese.pim.server.mail.model.MailFolder;

/**
 * Interface für die Mail-API.<br>
 *
 * @author Thomas Freese
 */
public interface MailAPI
{
    /**
     * Initialisiert den Service mit der konkreten Mail-API.
     */
    public void connect();

    /**
     * Schliessen der Verbindung.
     */
    public void disconnect();

    /**
     * Liefert den Account des Services.
     *
     * @return {@link MailAccount}
     */
    public MailAccount getAccount();

    /**
     * Liefert alle Folder des Accounts.
     *
     * @return {@link List}
     */
    public List<MailFolder> getFolder();

    /**
     * Liefert die aktuellen Message-UIDs im Folder.<br>
     *
     * @param folderFullName String
     * @return {@link Set}
     */
    public Set<Long> loadCurrentMessageIDs(String folderFullName);

    /**
     * Holt die Mail vom Provider und übergibt sie in dem {@link ExceptionalConsumer}.<br>
     *
     * @param <T> Konkreter Return-Typ
     * @param folderFullName String
     * @param uid long
     * @param function {@link ExceptionalFunction}
     * @return Object
     */
    public <T> T loadMail(String folderFullName, long uid, ExceptionalFunction<Object, T, Exception> function);

    /**
     * Holt die Mail vom Provider und übergibt sie in dem {@link ExceptionalConsumer}.<br>
     *
     * @param folderFullName String
     * @param uid long
     * @param monitor {@link IOMonitor}, optional
     * @return {@link MailContent}
     */
    public MailContent loadMail(String folderFullName, long uid, IOMonitor monitor);

    /**
     * Holt die Mail vom Provider und schreibt sie in den {@link OutputStream}.<br>
     *
     * @param folderFullName String
     * @param uid long
     * @param outputStream {@link OutputStream}
     */
    public void loadMail(String folderFullName, long uid, OutputStream outputStream);

    /**
     * Lädt die Mails des Folders vom Provider ab der definierten UID.<br>
     * Ist die Liste null ist der Folder nicht mehr existent.
     *
     * @param folderFullName {@link String}
     * @param uidFrom long; Startindex der zu ladenen Mails
     * @return {@link List}
     */
    public List<Mail> loadMails(String folderFullName, long uidFrom);

    /**
     * Optionaler {@link Executor} für die Mail-API.
     *
     * @param executor {@link Executor}
     */
    public void setExecutor(final Executor executor);

    /**
     * Setzt das SEEN-Flag einer Mail.
     *
     * @param mail {@link Mail}
     * @param seen boolean
     */
    public void setSeen(Mail mail, boolean seen);

    /**
     * Testet die Verbindung.
     */
    public void testConnection();
}
