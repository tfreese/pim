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
     *
     * @throws Exception Falls was schief geht.
     */
    public void close() throws Exception;

    /**
     * Liefert die Child-Folder.
     *
     * @return {@link ObservableList}
     * @throws Exception Falls was schief geht.
     */
    public ObservableList<IMailFolder> getChildren() throws Exception;

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
     * @throws Exception Falls was schief geht.
     */
    public ObservableList<IMail> getMessages() throws Exception;

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
     * @throws Exception Falls was schief geht.
     */
    public int getUnreadMessageCount() throws Exception;

    /**
     * Liefert die nicht gelesenen Mails.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<IMail> getUnreadMessages() throws Exception;

    // /**
    // * Synchronisiert das Lokale Verzeichnis mit dem Remote-Folder.
    // *
    // * @throws Exception Falls was schief geht.
    // */
    // public void syncLocal() throws Exception;
}
