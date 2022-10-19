// Created: 07.02.2017
package de.freese.pim.core.mail;

import java.util.Map;

import jakarta.activation.DataSource;

import de.freese.pim.core.mail.datasource.AttachmentDataSource;
import de.freese.pim.core.mail.datasource.InlineDataSource;
import de.freese.pim.core.mail.datasource.MessageDataSource;

/**
 * Interface für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public interface MailContent
{
    /**
     * Liefert die {@link DataSource} der Attachments.<br>
     * Key = Filename<br>
     * Value = {@link AttachmentDataSource}<br>
     *
     * @return {@link Map}
     */
    Map<String, AttachmentDataSource> getAttachments();

    String getEncoding();

    /**
     * Liefert die {@link DataSource} der Inlines.<br>
     * Key = contentID<br>
     * Value = {@link InlineDataSource}<br>
     *
     * @return {@link Map}
     */
    Map<String, InlineDataSource> getInlines();

    MessageDataSource getMessage();

    String getMessageContent();

    String getMessageContentType();
}
