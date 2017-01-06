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
     */
    public void connect(MailConfig mailConfig);

    /**
     * Schliessen der Verbindung.
     */
    public void disconnect();

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
     */
    public ObservableList<IMailFolder> getTopLevelFolder();

    /**
     * Liefert die Anzahl nicht gelesener Mails.
     *
     * @return int
     */
    public int getUnreadMessageCount();
}
