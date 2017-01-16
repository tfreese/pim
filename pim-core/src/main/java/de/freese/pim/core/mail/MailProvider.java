// Created: 04.01.2017
package de.freese.pim.core.mail;

import java.net.InetSocketAddress;

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
    Google("Google", "imap.gmail.com", MailPort.IMAPS, "smtp.gmail.com", MailPort.SMTPS);

    /**
     *
     */
    private final String displayName;

    /**
    *
    */
    private final InetSocketAddress imap;

    /**
    *
    */
    private final InetSocketAddress smtp;

    /**
     * Erzeugt eine neue Instanz von {@link MailProvider}
     *
     * @param displayName String
     * @param imapHost String
     * @param imapPort {@link MailPort}
     * @param smtpHost String
     * @param smtpPort {@link MailPort}
     */
    private MailProvider(final String displayName, final String imapHost, final MailPort imapPort, final String smtpHost,
            final MailPort smtpPort)
    {
        this.displayName = displayName;
        this.imap = new InetSocketAddress(imapHost, imapPort.getPort());
        this.smtp = new InetSocketAddress(smtpHost, smtpPort.getPort());
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
        return this.imap.getHostName();
    }

    /**
     * @return int
     */
    public int getImapPort()
    {
        return this.imap.getPort();
    }

    /**
     * @return String
     */
    public String getSmtpHost()
    {
        return this.smtp.getHostName();
    }

    /**
     * @return int
     */
    public int getSmtpPort()
    {
        return this.smtp.getPort();
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
