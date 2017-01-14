/**
 * Created: 14.01.2017
 */

package de.freese.pim.core.mail.dao;

import java.util.List;
import de.freese.pim.core.mail.model.MailAccount;

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
}
