// Created: 13.01.2017
package de.freese.pim.core.mail.api;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;

import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.service.IMailService;

/**
 * Interface für die Mail-API.<br>
 *
 * @author Thomas Freese
 */
public interface IMailAPI
{
    /**
     * Initialisiert den Service mit der konkreten Mail-API.
     *
     * @throws Exception Falls was schief geht.
     */
    public void connect() throws Exception;

    /**
     * Schliessen der Verbindung.
     *
     * @throws Exception Falls was schief geht.
     */
    public void disconnect() throws Exception;

    /**
     * Liefert den Account des Services.
     *
     * @return {@link MailAccount}
     */
    public MailAccount getAccount();

    /**
     * Liefert den lokalen Temp-{@link Path} des Accounts.
     *
     * @return {@link Path}
     */
    public Path getBasePath();

    /**
     * Liefert alle Folder des Accounts.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getFolder() throws Exception;

    /**
     * Holt die Mailvom Provider und schreibt sie in den {@link OutputStream}.<br>
     *
     * @param folderFullName String
     * @param uid long
     * @param outputStream {@link OutputStream}
     * @throws Exception Falls was schief geht.
     */
    public void loadMail(String folderFullName, long uid, OutputStream outputStream) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider.
     *
     * @param folderFullName {@link String}
     * @param uidFrom long; Startindex der zu ladenen Mails
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> loadMails(String folderFullName, long uidFrom) throws Exception;

    /**
     * Optionaler {@link ExecutorService} für die Mail-API.
     *
     * @param executor {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executor);

    /**
     * Setzt den {@link IMailService}.
     *
     * @param mailService {@link IMailService}
     */
    public void setMailService(final IMailService mailService);

    /**
     * Setzt das SEEN-Flag einer Mail.
     *
     * @param mail {@link Mail}
     * @param seen boolean
     * @throws Exception Falls was schief geht.
     */
    public void setSeen(Mail mail, boolean seen) throws Exception;

    /**
     * Testet die Verbindung.
     *
     * @throws Exception Falls was schief geht.
     */
    public void testConnection() throws Exception;
}
