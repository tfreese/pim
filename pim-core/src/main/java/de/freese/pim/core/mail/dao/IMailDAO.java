/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.util.List;

import de.freese.pim.core.mail.model.MailAccount;
import de.freese.pim.core.mail.model.MailFolder;

/**
 * @author Thomas Freese
 */
public interface IMailDAO
{
    /**
     * Liefert alle MailAccounts.
     *
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailAccount> getMailAccounts() throws Exception;

    /**
     * Liefert alle Folder des Mail-Accounts.
     *
     * @param accountID long
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<MailFolder> getMailFolder(long accountID) throws Exception;

    /**
     * Anlegen eines neuen {@link MailAccount}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param account {@link MailAccount}
     * @throws Exception Falls was schief geht.
     */
    public void insert(MailAccount account) throws Exception;

    /**
     * Anlegen eines neuen {@link MailFolder}.<br>
     * Die ID wird dabei in die Entity gesetzt.
     *
     * @param folder {@link MailFolder}
     * @param accountID long
     * @throws Exception Falls was schief geht.
     */
    public void insert(MailFolder folder, long accountID) throws Exception;

    /**
     * Änderen eines {@link MailAccount}.
     *
     * @param account {@link MailAccount}
     * @throws Exception Falls was schief geht.
     */
    public void update(MailAccount account) throws Exception;

    /**
     * Änderen eines {@link MailFolder}.
     *
     * @param folder {@link MailFolder}
     * @throws Exception Falls was schief geht.
     */
    public void update(MailFolder folder) throws Exception;
}
