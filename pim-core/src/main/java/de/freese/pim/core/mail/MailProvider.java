// Created: 04.01.2017
package de.freese.pim.core.mail;

import java.net.InetSocketAddress;

/**
 * Enums für die verschiedenen Konfigurationen der MailProvider.
 *
 * @author Thomas Freese (EFREEST / AuVi)
 */
public enum MailProvider
{
    // IMAP: 993 (SSL)
    // SMTP: 465 (SSL), 587 (TLS/STARTTLS)
    /**
     *
     */
    EinsUndEins("imap.1und1.de", 993, "smtp.1und1.de", 587),

    /**
    *
    */
    GoogleMail("imap.gmail.com", 993, "smtp.gmail.com", 587);

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
     * @param imapHost String
     * @param imapPort int
     * @param smtpHost String
     * @param smtpPort int
     */
    private MailProvider(final String imapHost, final int imapPort, final String smtpHost, final int smtpPort)
    {
        this.imap = new InetSocketAddress(imapHost, imapPort);
        this.smtp = new InetSocketAddress(smtpHost, smtpPort);
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
}
