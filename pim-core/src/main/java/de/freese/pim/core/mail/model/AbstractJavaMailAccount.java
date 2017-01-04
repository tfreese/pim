// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

/**
 * Basis-Implementierung eines JavaMail {@link IMailAccount}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractJavaMailAccount extends AbstractMailAccount
{
    /**
    *
    */
    private Session session = null;

    /**
     *
     */
    private Store store = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractJavaMailAccount}
     */
    public AbstractJavaMailAccount()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.mail.model.AbstractMailAccount#init(de.freese.pim.core.mail.model.MailConfig)
     */
    @Override
    public void init(final MailConfig mailConfig) throws Exception
    {
        super.init(mailConfig);

        this.session = createSession();
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

        if (DEBUG)
        {
            properties.put("mail.debug", Boolean.TRUE.toString());
        }

        // Legitimation für Empfang.
        if (getMailConfig().isImapLegitimation())
        {
            properties.put("mail.imap.auth", "true");
            properties.put("mail.imap.starttls.enable", "true");
        }

        // Legitimation für Versand.
        if (getMailConfig().isSmtpLegitimation())
        {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(properties, authenticator);

        // Test Connection Empfang.
        Store s = connectStore(session);
        disconnectStore(s);
        s = null;

        // Test Connection Versand.
        Transport t = connectTransport(session);
        disconnectTransport(t);
        t = null;

        return session;
    }

    /**
     * Connecten des {@link Store}.
     *
     * @param session {@link Session}
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected abstract Store connectStore(final Session session) throws MessagingException;

    /**
     * Connecten des {@link Transport}.
     *
     * @param session {@link Session}
     * @return {@link Transport}
     * @throws MessagingException Falls was schief geht.
     */
    protected abstract Transport connectTransport(final Session session) throws MessagingException;

    /**
     * Schliessen des {@link Store}.
     *
     * @param store {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected void disconnectStore(final Store store) throws MessagingException
    {
        if ((store != null) && store.isConnected())
        {
            store.close();
        }
    }

    /**
     * Schliessen des {@link Store}.
     *
     * @param transport {@link Transport}
     * @throws MessagingException Falls was schief geht.
     */
    protected void disconnectTransport(final Transport transport) throws MessagingException
    {
        if ((transport != null) && transport.isConnected())
        {
            transport.close();
        }
    }

    /**
     * @return {@link Session}
     */
    protected Session getSession()
    {
        return this.session;
    }

    /**
     * @return {@link Store}
     * @throws MessagingException Falls was schief geht.
     */
    protected Store getStore() throws MessagingException
    {
        if ((this.store != null) && !this.store.isConnected())
        {
            // disconnectStore(this.store);
            this.store = null;
        }

        if (this.store == null)
        {
            this.store = connectStore(getSession());
        }

        return this.store;
    }
}
