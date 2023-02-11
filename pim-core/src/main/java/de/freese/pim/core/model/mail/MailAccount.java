// Created: 26.12.2016
package de.freese.pim.core.model.mail;

import de.freese.pim.core.mail.MailPort;

/**
 * Entity für einen Mail-Account.
 *
 * @author Thomas Freese
 */
public class MailAccount {
    private long id;

    // @NotNull
    // @Size(min = 0, max = 50)
    private String imapHost;

    private boolean imapLegitimation = true;

    // @NotNull
    private MailPort imapPort = MailPort.IMAPS;

    // @NotNull
    // @Pattern(regexp = Utils.MAIL_REGEX)
    // @Size(min = 0, max = 50)
    private String mail;

    // @NotNull
    // @Size(min = 0, max = 100)
    private String password;

    // @NotNull
    // @Size(min = 0, max = 50)
    private String smtpHost;

    private boolean smtpLegitimation = true;

    // @NotNull
    private MailPort smtpPort = MailPort.SMTPS;

    public MailAccount() {
        super();
    }

    public MailAccount(final MailAccount src) {
        this();

        copyFrom(src);
    }

    public void copyFrom(final MailAccount src) {
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

    public long getID() {
        return this.id;
    }

    public String getImapHost() {
        return this.imapHost;
    }

    public MailPort getImapPort() {
        return this.imapPort;
    }

    public String getMail() {
        return this.mail;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSmtpHost() {
        return this.smtpHost;
    }

    public MailPort getSmtpPort() {
        return this.smtpPort;
    }

    public boolean isImapLegitimation() {
        return this.imapLegitimation;
    }

    public boolean isSmtpLegitimation() {
        return this.smtpLegitimation;
    }

    public void setID(final long id) {
        this.id = id;
    }

    public void setImapHost(final String host) {
        this.imapHost = host;
    }

    public void setImapLegitimation(final boolean legitimation) {
        this.imapLegitimation = legitimation;
    }

    public void setImapPort(final MailPort port) {
        this.imapPort = port;
    }

    public void setMail(final String mail) {
        this.mail = mail;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setSmtpHost(final String host) {
        this.smtpHost = host;
    }

    public void setSmtpLegitimation(final boolean legitimation) {
        this.smtpLegitimation = legitimation;
    }

    public void setSmtpPort(final MailPort port) {
        this.smtpPort = port;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MailAccount [id=").append(getID());
        builder.append(", mail=").append(getMail());
        builder.append("]");

        return builder.toString();
    }
}
