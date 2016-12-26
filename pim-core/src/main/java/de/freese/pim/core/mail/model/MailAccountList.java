/**
 * Created: 26.12.2016
 */

package de.freese.pim.core.mail.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ObservableList für die Mail-Accounts.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
// @JsonRootName("mailAccounts")
// @JsonIgnoreProperties(
// {
// "empty"
// })
public class MailAccountList
{
    /**
     *
     */
    // @JsonUnwrapped
    private final ObservableList<MailAccount> list = FXCollections.observableArrayList();

    /**
     * Erstellt ein neues {@link MailAccountList} Object.
     */
    public MailAccountList()
    {
        super();
    }

    /**
     * @param account {@link MailAccount}
     */
    public void add(final MailAccount account)
    {
        getObservableList().add(account);
    }

    /**
     * @param index int
     * @return {@link MailAccount}
     */
    public MailAccount get(final int index)
    {
        return getObservableList().get(index);
    }

    /**
     * @return {@link ObservableList}
     */
    @JsonGetter("mailAccounts")
    // @JsonIgnore
    public ObservableList<MailAccount> getObservableList()
    {
        return this.list;
    }

    /**
     * @return boolean
     */
    @JsonIgnore
    public boolean isEmpty()
    {
        return getObservableList().isEmpty();
    }

    /**
     * @param accounts {@link List}
     */
    @JsonSetter("mailAccounts")
    public void setList(final List<MailAccount> accounts)
    {
        getObservableList().addAll(accounts);
    }

    /**
     * @return int
     */
    public int size()
    {
        return getObservableList().size();
    }
}
