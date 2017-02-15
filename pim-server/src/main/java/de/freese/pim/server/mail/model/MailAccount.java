/**
 * Created: 26.12.2016
 */
package de.freese.pim.server.mail.model;

import de.freese.pim.common.model.mail.MailPort;

/**
 * Entity für einen Mail-Account.
 *
 * @author Thomas Freese
 */
public class MailAccount
{
    /**
     *
     */
    private long id = 0;

    /**
     *
     */
    private String imapHost = null;

    /**
     *
     */
    private boolean imapLegitimation = true;

    /**
     *
     */
    private MailPort imapPort = MailPort.IMAPS;

    /**
     *
     */
    private String mail = null;

    /**
     *
     */
    private String password = null;

    /**
     *
     */
    private String smtpHost = null;

    /**
     *
     */
    private boolean smtpLegitimation = true;

    /**
     *
     */
    private MailPort smtpPort = MailPort.SMTPS;

    /**
     * Erstellt ein neues {@link MailAccount} Object.
     */
    public MailAccount()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link MailAccount} Object mit den Attributen der Quelle.
     *
     * @param src {@link MailAccount}
     */
    public MailAccount(final MailAccount src)
    {
        this();

        copyFrom(src);
    }

    /**
     * Kopiert die Attribute von der Quelle.
     *
     * @param src {@link MailAccount}
     */
    public void copyFrom(final MailAccount src)
    {
        setID(src.getID());
        setImapHost(src.getImapHost());
        setImapLegitimation(src.isImapLegitimation());
        setImapPort(src.getImapPort());
        setMail(src.getMail());
        setPassword(src.getPassword());
        setSmtpHost(src.getSmtpHost());
        setSmtpLegitimation(src.isSmtpLegitimation());
        setSmtpPort(src.getSmtpPort());
    }

    /**
     * @return long
     */
    public long getID()
    {
        return this.id;
    }

    /**
     * @return String
     */
    public String getImapHost()
    {
        return this.imapHost;
    }

    /**
     * @return {@link MailPort}
     */
    public MailPort getImapPort()
    {
        return this.imapPort;
    }

    /**
     * @return String
     */
    public String getMail()
    {
        return this.mail;
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @return String
     */
    public String getSmtpHost()
    {
        return this.smtpHost;
    }

    /**
     * @return {@link MailPort}
     */
    public MailPort getSmtpPort()
    {
        return this.smtpPort;
    }

    /**
     * @return boolean
     */
    public boolean isImapLegitimation()
    {
        return this.imapLegitimation;
    }

    /**
     * @return boolean
     */
    public boolean isSmtpLegitimation()
    {
        return this.smtpLegitimation;
    }

    /**
     * @param id long
     */
    public void setID(final long id)
    {
        this.id = id;
    }

    /**
     * @param host String
     */
    public void setImapHost(final String host)
    {
        this.imapHost = host;
    }

    /**
     * @param legitimation boolean
     */
    public void setImapLegitimation(final boolean legitimation)
    {
        this.imapLegitimation = legitimation;
    }

    /**
     * @param port {@link MailPort}
     */
    public void setImapPort(final MailPort port)
    {
        this.imapPort = port;
    }

    /**
     * @param mail String
     */
    public void setMail(final String mail)
    {
        this.mail = mail;
    }

    /**
     * @param password String
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /**
     * @param host String
     */
    public void setSmtpHost(final String host)
    {
        this.smtpHost = host;
    }

    /**
     * @param legitimation boolean
     */
    public void setSmtpLegitimation(final boolean legitimation)
    {
        this.smtpLegitimation = legitimation;
    }

    /**
     * @param port {@link MailPort}
     */
    public void setSmtpPort(final MailPort port)
    {
        this.smtpPort = port;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MailAccount [id=").append(getID());
        builder.append(", mail=").append(getMail());
        builder.append("]");

        return builder.toString();
    }
}
