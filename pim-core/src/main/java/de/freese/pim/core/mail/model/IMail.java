// Created: 04.01.2017
package de.freese.pim.core.mail.model;

import java.nio.file.Path;
import java.util.Date;

import javax.mail.internet.InternetAddress;

/**
 * Interface für eine Mail
 *
 * @author Thomas Freese
 */
public interface IMail
{
    /**
     * Liefert den Absender.
     *
     * @return {@link InternetAddress}
     */
    public InternetAddress getFrom();

    /**
     * Liefert die Message-ID oder bei IMAP die UID.
     *
     * @return String
     */
    public String getID();

    /**
     * Liefert den lokalen Temp-{@link Path} der Mail.
     *
     * @return {@link Path}
     */
    public Path getPath();

    /**
     * Liefert das Empfangs-Datum.
     *
     * @return {@link Date}
     */
    public Date getReceivedDate();

    /**
     * Liefert das Sende-Datum.
     *
     * @return {@link Date}
     */
    public Date getSendDate();

    /**
     * Liefert die Subject.
     *
     * @return String
     */
    public String getSubject();

    /**
     * Liefert true, wenn die Mail bereits gelesen wurde.
     *
     * @return boolean
     */
    public boolean isSeen();
}
