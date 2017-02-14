// Created: 07.02.2017
package de.freese.pim.server.mail.api;

import java.io.IOException;
import java.util.Map;

import javax.activation.DataSource;

/**
 * Interface für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public interface MailContent
{
    /**
     * Liefert die {@link DataSource} der Attachements.<br>
     * Key = Filename<br>
     * Value = {@link DataSource}<br>
     *
     * @return {@link Map}
     */
    public Map<String, DataSource> getAttachments();

    /**
     * Liefert das Encoding.
     *
     * @return String
     */
    public String getEncoding();

    /**
     * Liefert die {@link DataSource} des Inlines.
     *
     * @param contentID String
     * @return {@link DataSource} oder null
     * @throws IOException Falls was schief geht.
     */
    public DataSource getInlineDataSource(String contentID) throws IOException;

    /**
     * Liefert den Text der Mail.
     *
     * @return String
     */
    public String getMessageContent();

    /**
     * Liefert den ContetnType für den Text der Mail.
     *
     * @return String
     */
    public String getMessageContentType();
}