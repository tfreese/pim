// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

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
     * Liefert alle abonnierte Folder des Accounts.
     *
     * @return {@link FilteredList}
     * @throws Exception Falls was schief geht.
     */
    public FilteredList<MailFolder> getAbonnierteFolder() throws Exception;

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
     * @return {@link ObservableList}
     * @throws Exception Falls was schief geht.
     */
    public ObservableList<MailFolder> getFolder() throws Exception;

    /**
     * Liefert die neuen Mails des Folders.
     *
     * @param folder {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> getNewMails(MailFolder folder) throws Exception;

    /**
     * Liefert die Anzahl ungelesener Mails des Accounts.
     *
     * @return int
     */
    public int getUnreadMailsCount();

    /**
     * Holt Mails des Folders und übergibt sie dem {@link Consumer}.
     *
     * @param folder {@link MailFolder}
     * @param consumer {@link Consumer}
     * @throws Exception Falls was schief geht.
     */
    public void loadMails(MailFolder folder, Consumer<Mail> consumer) throws Exception;

    /**
     * Optionaler {@link ExecutorService} für die Mail-API.
     *
     * @param executor {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executor);

    /**
     * Testet die Verbindung.
     *
     * @throws Exception Falls was schief geht.
     */
    public void testConnection() throws Exception;
}
