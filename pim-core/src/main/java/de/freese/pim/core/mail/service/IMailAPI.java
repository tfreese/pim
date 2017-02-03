// Created: 13.01.2017
package de.freese.pim.core.mail.service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.activation.DataSource;

import de.freese.pim.core.mail.model.Mail;
import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;
import de.freese.pim.core.mail.utils.MailContent;
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
     * Liefert alle Folder des Accounts.<br>
     * Wird im Hintergrund geladen.
     *
     * @return {@link Future}
     */
    public ObservableList<MailFolder> getFolder();

    /**
     * Liefert alle abonnierte Folder des Accounts.
     *
     * @return {@link FilteredList}
     */
    public FilteredList<MailFolder> getFolderSubscribed();

    /**
     * Liefert die Anzahl ungelesener Mails des Accounts.
     *
     * @return int
     */
    public int getUnreadMailsCount();

    /**
     * Lädt die Folder des Accounts.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> loadFolder() throws Exception;

    // /**
    // * Lädt die Folder des Accounts und übergibt sie dem {@link Consumer}.
    // *
    // * @param consumer {@link Consumer}
    // * @throws Exception Falls was schief geht.
    // */
    // public void loadFolder(Consumer<MailFolder> consumer) throws Exception;

    /**
     * Lädt die Mails des Folders vom Provider und aus der DB.
     *
     * @param folder {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> loadMails(MailFolder folder) throws Exception;

    /**
     * Holt die neuen Mails des Folders und übergibt sie dem {@link Consumer}.
     *
     * @param folder {@link MailFolder}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<Mail> loadNewMails(MailFolder folder) throws Exception;

    /**
     * Liefert die {@link DataSource} mit dem Text der Mail.<br>
     * Der Monitor dient zur Anzeige des Lade-Fortschritts.
     *
     * @param mail {@link Mail}
     * @param loadMonitor {@link BiConsumer}
     * @return {@link MailContent}
     * @throws Exception Falls was schief geht.
     */
    public MailContent loadTextContent(Mail mail, BiConsumer<Long, Long> loadMonitor) throws Exception;

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
