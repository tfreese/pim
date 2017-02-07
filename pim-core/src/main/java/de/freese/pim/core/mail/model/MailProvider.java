// Created: 04.01.2017
package de.freese.pim.core.mail.model;

/**
 * Enums für die verschiedenen Konfigurationen der MailProvider.
 *
 * @author Thomas Freese
 */
public enum MailProvider
{
    /**
     *
     */
    EinsUndEins("1&1", "imap.1und1.de", MailPort.IMAPS, "smtp.1und1.de", MailPort.SMTPS),
    /**
     *
     */
    Google("Google", "imap.gmail.com", MailPort.IMAPS, "smtp.gmail.com", MailPort.SMTPS),
    /**
     *
     */
    WebDe("web.de", "imap.web.de", MailPort.IMAPS, "smtp.web.de", MailPort.SMTPS),
    /**
     *
     */
    GMX("GMX", "imap.gmx.net", MailPort.IMAPS, "mail.gmx.net", MailPort.SMTPS);

    /**
     *
     */
    private final String displayName;

    /**
     *
     */
    private final String imapHost;

    /**
     *
     */
    private final String smtpHost;

    /**
     *
     */
    private final MailPort imapPort;

    /**
     *
     */
    private final MailPort smtpPort;

    /**
     * Erzeugt eine neue Instanz von {@link MailProvider}
     *
     * @param displayName String
     * @param imapHost    String
     * @param imapPort    {@link MailPort}
     * @param smtpHost    String
     * @param smtpPort    {@link MailPort}
     */
    private MailProvider(final String displayName, final String imapHost, final MailPort imapPort, final String smtpHost,
                         final MailPort smtpPort)
    {
        this.displayName = displayName;
        this.imapHost = imapHost;
        this.imapPort = imapPort;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    /**
     * @return String
     */
    public String getDisplayName()
    {
        return this.displayName;
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
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return getDisplayName();
    }
}
