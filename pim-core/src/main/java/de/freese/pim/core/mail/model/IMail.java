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
     * @throws Exception Falls was schief geht.
     */
    public InternetAddress getFrom() throws Exception;

    /**
     * Liefert die Message-ID.
     *
     * @return String
     * @throws Exception Falls was schief geht.
     */
    public String getMessageID() throws Exception;

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
     * @throws Exception Falls was schief geht.
     */
    public Date getReceivedDate() throws Exception;

    /**
     * Liefert die Subject.
     *
     * @return String
     * @throws Exception Falls was schief geht.
     */
    public String getSubject() throws Exception;

    /**
     * Liefert true, wenn die Mail bereits gelesen wurde.
     *
     * @return boolean
     * @throws Exception Falls was schief geht.
     */
    public boolean isSeen() throws Exception;
}
