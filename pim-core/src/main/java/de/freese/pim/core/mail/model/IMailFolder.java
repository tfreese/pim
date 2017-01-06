// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.List;

import javafx.collections.ObservableList;

/**
 * Interface für einen MailFolder.
 *
 * @author Thomas Freese
 */
public interface IMailFolder
{
    /**
     * Schliesst den Folder und seine Children.
     */
    public void close();

    /**
     * Liefert die Child-Folder.
     *
     * @return {@link ObservableList}
     */
    public ObservableList<IMailFolder> getChildren();

    /**
     * Liefert den vollen Hierarchie-Namen.
     *
     * @return String
     */
    public String getFullName();

    /**
     * Liefert alle Mails.
     *
     * @return {@link ObservableList}
     */
    public ObservableList<IMail> getMessages();

    /**
     * Liefert den Namen.
     *
     * @return String
     */
    public String getName();

    /**
     * Liefert den lokalen Temp-{@link Path} des Folders.
     *
     * @return {@link Path}
     */
    public Path getPath();

    /**
     * Liefert die Anzahl nicht gelesener Mails, inklusive der Child-Folder.
     *
     * @return int
     */
    public int getUnreadMessageCount();

    /**
     * Liefert die nicht gelesenen Mails.
     *
     * @return {@link List}
     */
    public List<IMail> getUnreadMessages();

    // /**
    // * Synchronisiert das Lokale Verzeichnis mit dem Remote-Folder.
    // *
    // * @throws Exception Falls was schief geht.
    // */
    // public void syncLocal() throws Exception;
}
