// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.List;

/**
 * Interface für einen MailFolder.
 *
 * @author Thomas Freese
 */
public interface IMailFolder
{
    /**
     * Liefert die Child-Folder.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<IMailFolder> getChildren() throws Exception;

    /**
     * Liefert den Namen.
     *
     * @return String
     */
    public String getName();
}
