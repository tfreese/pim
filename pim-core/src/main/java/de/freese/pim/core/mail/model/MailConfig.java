/**
 * Created: 26.12.2016
 */

package de.freese.pim.core.mail.model;

import java.util.concurrent.Executor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Entity für einen Mail-Account.
 *
 * @author Thomas Freese
 */
// @SuppressWarnings("restriction")
// @JsonRootName("mailAccount")
public class MailConfig
{
    /**
     *
     */
    @JsonIgnore
    private Executor executor = null;

    /**
    *
    */
    private final StringProperty imapHostProperty = new SimpleStringProperty(this, "imapHost", null);

    /**
    *
    */
    private final BooleanProperty imapLegitimationProperty = new SimpleBooleanProperty(this, "imapLegitimation", true);

    /**
    *
    */
    private final IntegerProperty imapPortProperty = new SimpleIntegerProperty(this, "imapPort", 993);

    /**
    *
    */
    private final StringProperty mailProperty = new SimpleStringProperty(this, "mail", null);

    /**
    *
    */
    private final StringProperty passwordProperty = new SimpleStringProperty(this, "password", null);

    /**
    *
    */
    private final StringProperty smtpHostProperty = new SimpleStringProperty(this, "smtpHost", null);

    /**
    *
    */
    private final BooleanProperty smtpLegitimationProperty = new SimpleBooleanProperty(this, "smtpLegitimation", true);

    /**
    *
    */
    private final IntegerProperty smtpPortProperty = new SimpleIntegerProperty(this, "smtpPort", 587);

    /**
     * Erstellt ein neues {@link MailConfig} Object.
     */
    public MailConfig()
    {
        super();
    }

    /**
     * {@link Executor} für die MAIL-API, ist optional.
     *
     * @return {@link Executor}
     */
    public Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @return String
     */
    public String getImapHost()
    {
        return imapHostProperty().get();
    }

    /**
     * @return int
     */
    public int getImapPort()
    {
        return imapPortProperty().get();
    }

    /**
     * @return String
     */
    public String getMail()
    {
        return mailProperty().get();
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return passwordProperty().get();
    }

    /**
     * @return String
     */
    public String getSmtpHost()
    {
        return smtpHostProperty().get();
    }

    /**
     * @return int
     */
    public int getSmtpPort()
    {
        return smtpPortProperty().get();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty imapHostProperty()
    {
        return this.imapHostProperty;
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty imapLegitimationProperty()
    {
        return this.imapLegitimationProperty;
    }

    /**
     * @return {@link IntegerProperty}
     */
    public IntegerProperty imapPortProperty()
    {
        return this.imapPortProperty;
    }

    /**
     * @return boolean
     */
    public boolean isImapLegitimation()
    {
        return imapLegitimationProperty().get();
    }

    /**
     * @return boolean
     */
    public boolean isSmtpLegitimation()
    {
        return smtpLegitimationProperty().get();
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty mailProperty()
    {
        return this.mailProperty;
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty passwordProperty()
    {
        return this.passwordProperty;
    }

    /**
     * {@link Executor} für die MAIL-API, ist optional.
     *
     * @param executor {@link Executor}
     */
    public void setExecutor(final Executor executor)
    {
        this.executor = executor;
    }

    /**
     * @param host String
     */
    public void setImapHost(final String host)
    {
        imapHostProperty().set(host);
    }

    /**
     * @param legitimation boolean
     */
    public void setImapLegitimation(final boolean legitimation)
    {
        imapLegitimationProperty().set(legitimation);
    }

    /**
     * @param port int
     */
    public void setImapPort(final int port)
    {
        imapPortProperty().set(port);
    }

    /**
     * @param mail String
     */
    public void setMail(final String mail)
    {
        mailProperty().set(mail);
    }

    /**
     * @param password String
     */
    public void setPassword(final String password)
    {
        passwordProperty().set(password);
    }

    /**
     * @param host String
     */
    public void setSmtpHost(final String host)
    {
        smtpHostProperty().set(host);
    }

    /**
     * @param legitimation boolean
     */
    public void setSmtpLegitimation(final boolean legitimation)
    {
        smtpLegitimationProperty().set(legitimation);
    }

    /**
     * @param port int
     */
    public void setSmtpPort(final int port)
    {
        smtpPortProperty().set(port);
    }

    /**
     * @return {@link StringProperty}
     */
    public StringProperty smtpHostProperty()
    {
        return this.smtpHostProperty;
    }

    /**
     * @return {@link BooleanProperty}
     */
    public BooleanProperty smtpLegitimationProperty()
    {
        return this.smtpLegitimationProperty;
    }

    /**
     * @return {@link IntegerProperty}
     */
    public IntegerProperty smtpPortProperty()
    {
        return this.smtpPortProperty;
    }
}
