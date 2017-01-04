// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.List;

/**
 * Interface für einen MailAccount.
 *
 * @author Thomas Freese
 */
public interface IMailAccount
{
    /**
     * Liefert den Namen.
     *
     * @return String
     */
    public String getName();

    /**
     * Liefert die direkten Folder.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<IMailFolder> getTopLevelFolder() throws Exception;

    /**
     * Initialisierunge-Methode des {@link IMailAccount}.
     *
     * @param mailConfig {@link MailConfig}
     * @throws Exception Falls was schief geht.
     */
    public void init(MailConfig mailConfig) throws Exception;
}
