/**
 * Created: 26.12.2016
 */

package de.freese.pim.core.mail.model;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
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
@SuppressWarnings("restriction")
// @JsonRootName("mailAccount")
public class MailAccount
{
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
    private Session session = null;

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
     *
     */
    private Store store = null;

    /**
     * Erstellt ein neues {@link MailAccount} Object.
     */
    public MailAccount()
    {
        super();
    }

    /**
     * @throws MessagingException Falls was schief geht.
     */
    public void close() throws MessagingException
    {
        if (this.store != null)
        {
            this.store.close();
        }
    }

    /**
     * Erzeugt die Mail-Session.
     *
     * @return {@link Session}
     * @throws MessagingException Falls was schief geht.
     */
    private Session createSession() throws MessagingException
    {
        Authenticator authenticator = null;

        Properties properties = new Properties();

        // Legitimation für Empfang.
        if (isImapLegitimation())
        {
            properties.put("mail.imap.auth", "true");
            properties.put("mail.imap.starttls.enable", "true");
        }

        // Legitimation für Versand.
        if (isSmtpLegitimation())
        {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(properties, authenticator);

        // Test Connection Empfang.
        this.store = session.getStore("imaps");
        this.store.connect(getImapHost(), getImapPort(), getMail(), getPassword());

        // Test Connection Versand.
        // this.mailSender.testConnection();
        Transport transport = session.getTransport("smtp");
        transport.connect(getSmtpHost(), getSmtpPort(), getMail(), getPassword());
        transport.close();

        return session;
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
     * Liefert die Mail-Session.
     *
     * @return {@link Session}
     * @throws MessagingException Falls was schief geht.
     */
    public Session getSession() throws MessagingException
    {
        if (this.session == null)
        {
            this.session = createSession();
        }

        return this.session;
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
