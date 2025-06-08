// Created: 04.01.2017
package de.freese.pim.core.mail;

/**
 * Enums für die verschiedenen Konfigurationen der MailProvider.
 *
 * @author Thomas Freese
 */
public enum MailProvider {
    EINS_UND_EINS("1&1", "imap.1und1.de", MailPort.IMAPS, "smtp.1und1.de", MailPort.SMTPS),

    GMX("GMX", "imap.gmx.net", MailPort.IMAPS, "mail.gmx.net", MailPort.SMTPS),

    GOOGLE("Google", "imap.gmail.com", MailPort.IMAPS, "smtp.gmail.com", MailPort.SMTPS),

    WEB_DE("web.de", "imap.web.de", MailPort.IMAPS, "smtp.web.de", MailPort.SMTPS);

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
        return displayName;
    }

    public String getImapHost() {
        return imapHost;
    }

    public MailPort getImapPort() {
        return imapPort;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public MailPort getSmtpPort() {
        return smtpPort;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
