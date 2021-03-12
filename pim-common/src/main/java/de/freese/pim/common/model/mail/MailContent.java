// Created: 07.02.2017
package de.freese.pim.common.model.mail;

import java.util.Map;
import javax.activation.DataSource;
import de.freese.pim.common.model.mail.datasource.AttachmentDataSource;
import de.freese.pim.common.model.mail.datasource.InlineDataSource;
import de.freese.pim.common.model.mail.datasource.MessageDataSource;

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
     * Value = {@link AttachmentDataSource}<br>
     *
     * @return {@link Map}
     */
    public Map<String, AttachmentDataSource> getAttachments();

    /**
     * Liefert das Encoding.
     *
     * @return String
     */
    public String getEncoding();

    /**
     * Liefert die {@link DataSource} der Inlines.<br>
     * Key = contentID<br>
     * Value = {@link InlineDataSource}<br>
     *
     * @return {@link Map}
     */
    public Map<String, InlineDataSource> getInlines();

    /**
     * Liefert die {@link DataSource} des Textes der Mail.
     *
     * @return {@link MessageDataSource}
     */
    public MessageDataSource getMessage();

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
