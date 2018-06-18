// Created: 04.01.2017
package de.freese.pim.common.model.mail;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enums für die verschiedenen Mail-Ports.
 *
 * @author Thomas Freese
 */
public enum MailPort
{
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

    /**
     * Liefert den passenden ENUM für den Port oder null.
     *
     * @param port int
     * @return MailPort
     */
    public static MailPort findByPort(final int port)
    {
        Optional<MailPort> result = Stream.of(values()).filter(mp -> mp.getPort() == port).findFirst();

        return result.orElseThrow(() -> new IllegalArgumentException("port not found: " + port));
    }

    /**
    *
    */
    private final int port;

    /**
     * Erzeugt eine neue Instanz von {@link MailPort}
     *
     * @param port int
     */
    private MailPort(final int port)
    {
        this.port = port;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s (%d)", name(), getPort());
    }
}
