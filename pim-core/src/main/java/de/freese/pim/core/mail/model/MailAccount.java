/**
 * Created: 26.12.2016
 */

package de.freese.pim.core.mail.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Entity für einen Mail-Account.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("restriction")
// @JsonRootName("mailAccount")
public class MailAccount
{
    /**
    *
    */
    private final StringProperty accountProperty = new SimpleStringProperty(this, "account", null);

    /**
    *
    */
    private final StringProperty passwordProperty = new SimpleStringProperty(this, "password", null);

    /**
     * Erstellt ein neues {@link MailAccount} Object.
     */
    public MailAccount()
    {
        super();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty accountProperty()
    {
        return this.accountProperty;
    }

    /**
     * @return String
     */
    public String getAccount()
    {
        return accountProperty().get();
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return passwordProperty().get();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty passwordProperty()
    {
        return this.passwordProperty;
    }

    /**
     * @param account String
     */
    public void setAccount(final String account)
    {
        accountProperty().set(account);
    }

    /**
     * @param password String
     */
    public void setPassword(final String password)
    {
        passwordProperty().set(password);
    }
}
