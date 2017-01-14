// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;

/**
 * Interface für den Service des Mail-Clients<br>
 *
 * @author Thomas Freese
 */
public interface IMailService
{
    /**
     * Initialisiert den Service mit der konkreten Mail-API und testet die Verbindung.
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
     * Liefert die Children des Folders.
     *
     * @param parent {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getChilds(MailFolder parent) throws Exception;

    /**
     * Liefert die neuen Mails des Folders.
     *
     * @param folder {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> getNewMails(MailFolder folder) throws Exception;

    /**
     * Liefert die Root-/Top-Level Folder des Accounts.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getRootFolder() throws Exception;

    /**
     * Holt Mails des Folders und übergibt sie dem {@link Consumer}.
     *
     * @param folder {@link MailFolder}
     * @param consumer {@link Consumer}
     * @throws Exception Falls was schief geht.
     */
    public void loadMails(MailFolder folder, Consumer<Mail> consumer) throws Exception;

    /**
     * Optionaler {@link Executor} für die Mail-API.
     *
     * @param executor {@link Executor}
     */
    public void setExecutor(final Executor executor);

    /**
     * Synchronisiert den lokalen Cache der Mail-Folder.
     *
     * @throws Exception Falls was schief geht.
     */
    public void syncFolders() throws Exception;
}
