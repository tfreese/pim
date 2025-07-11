// Created: 04.01.2017
package de.freese.pim.core.mail;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enums für die verschiedenen Mail-Ports.
 *
 * @author Thomas Freese
 */
public enum MailPort {
    /**
     * 143
     */
    IMAP(143),
    /**
     * 993, SSL
     */
    IMAPS(993),
    /**
     * 110
     */
    POP3(110),
    /**
     * 998, SSL
     */
    POP3S(998),
    /**
     * 25
     */
    SMTP(25),
    /**
     * 465, SSL
     */
    SMTP_SSL(465),
    /**
     * 587, TLS/STARTTLS
     */
    SMTPS(587),
    /**
     * -1
     */
    UNKNOWN(-1);

    public static MailPort findByPort(final int port) {
        final Optional<MailPort> result = Stream.of(values()).filter(mp -> mp.getPort() == port).findFirst();

        return result.orElseThrow(() -> new IllegalArgumentException("port not found: " + port));
    }

    private final int port;

    MailPort(final int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name(), getPort());
    }
}
