// Created: 04.01.2017
package de.freese.pim.core.mail;

/**
 * Enums für die verschiedenen Konfigurationen der MailProvider.
 *
 * @author Thomas Freese
 */
public enum MailProvider {
    EinsUndEins("1&1", "imap.1und1.de", MailPort.IMAPS, "smtp.1und1.de", MailPort.SMTPS),

    GMX("GMX", "imap.gmx.net", MailPort.IMAPS, "mail.gmx.net", MailPort.SMTPS),

    Google("Google", "imap.gmail.com", MailPort.IMAPS, "smtp.gmail.com", MailPort.SMTPS),

    WebDe("web.de", "imap.web.de", MailPort.IMAPS, "smtp.web.de", MailPort.SMTPS);

    private final String displayName;

    private final String imapHost;

    private final MailPort imapPort;

    private final String smtpHost;

    private final MailPort smtpPort;

    MailProvider(final String displayName, final String imapHost, final MailPort imapPort, final String smtpHost, final MailPort smtpPort) {
        this.displayName = displayName;
        this.imapHost = imapHost;
        this.imapPort = imapPort;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getImapHost() {
        return this.imapHost;
    }

    public MailPort getImapPort() {
        return this.imapPort;
    }

    public String getSmtpHost() {
        return this.smtpHost;
    }

    public MailPort getSmtpPort() {
        return this.smtpPort;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
