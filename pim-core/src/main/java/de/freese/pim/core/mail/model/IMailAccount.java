// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.List;

import javafx.collections.ObservableList;

/**
 * Interface für einen MailAccount.
 *
 * @author Thomas Freese
 */
public interface IMailAccount
{
    /**
     * Initialisierunge-Methode des {@link IMailAccount}.
     *
     * @param mailConfig {@link MailConfig}
     * @throws Exception Falls was schief geht.
     */
    public void connect(MailConfig mailConfig) throws Exception;

    /**
     * Schliessen der Verbindung.
     *
     * @throws Exception Falls was schief geht.
     */
    public void disconnect() throws Exception;

    /**
     * Liefert die {@link MailConfig}.
     *
     * @return {@link MailConfig}
     */
    public MailConfig getMailConfig();

    /**
     * Liefert den Namen.
     *
     * @return String
     */
    public String getName();

    /**
     * Liefert den lokalen Temp-{@link Path} des Accounts.
     *
     * @return {@link Path}
     */
    public Path getPath();

    /**
     * Liefert die direkten Folder.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public ObservableList<IMailFolder> getTopLevelFolder() throws Exception;

    /**
     * Liefert die Anzahl nicht gelesener Mails.
     *
     * @return int
     * @throws Exception Falls was schief geht.
     */
    public int getUnreadMessageCount() throws Exception;
}
