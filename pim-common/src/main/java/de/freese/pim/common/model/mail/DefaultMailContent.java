// Created: 16.02.2017
package de.freese.pim.common.model.mail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.freese.pim.common.model.mail.datasource.AttachmentDataSource;
import de.freese.pim.common.model.mail.datasource.InlineDataSource;
import de.freese.pim.common.model.mail.datasource.MessageDataSource;

/**
 * Container für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class DefaultMailContent implements MailContent
{
    /**
    *
    */
    private Map<String, AttachmentDataSource> attachmentMap;
    /**
     *
     */
    private String encoding;
    /**
     *
     */
    private Map<String, InlineDataSource> inlineMap;
    /**
    *
    */
    private MessageDataSource message;
    /**
     *
     */
    @JsonIgnore
    private transient String messageContent;
    /**
    *
    */
    private String messageContentType;

    /**
     * @see de.freese.pim.common.model.mail.MailContent#getAttachments()
     */
    @Override
    public Map<String, AttachmentDataSource> getAttachments()
    {
        return this.attachmentMap;
    }

    /**
     * @see de.freese.pim.common.model.mail.MailContent#getEncoding()
     */
    @Override
    public String getEncoding()
    {
        return this.encoding;
    }

    /**
     * @see de.freese.pim.common.model.mail.MailContent#getInlines()
     */
    @Override
    public Map<String, InlineDataSource> getInlines()
    {
        return this.inlineMap;
    }

    /**
     * @return {@link MessageDataSource}
     */
    @Override
    public MessageDataSource getMessage()
    {
        return this.message;
    }

    /**
     * Text-Nachricht: text/plain, text/html
     *
     * @see de.freese.pim.common.model.mail.MailContent#getMessageContent()
     */
    @Override
    public String getMessageContent()
    {
        if (this.messageContent == null)
        {
            try
            {
                try (BufferedReader buffer = new BufferedReader(new InputStreamReader(getMessage().getInputStream(), getEncoding())))
                {
                    this.messageContent = buffer.lines().collect(Collectors.joining("\n"));
                }
            }
            catch (Exception ex)
            {
                LoggerFactory.getLogger(getClass()).error(null, ex);
            }

        }

        return this.messageContent;
    }

    /**
     * "text/plain; charset=UTF-8", "text/html; charset=UTF-8"
     *
     * @see de.freese.pim.common.model.mail.MailContent#getMessageContentType()
     */
    @Override
    public String getMessageContentType()
    {
        return this.messageContentType;
    }

    /**
     * @param attachments {@link Map}
     */
    public void setAttachments(final Map<String, AttachmentDataSource> attachments)
    {
        this.attachmentMap = attachments;
    }

    /**
     * @param encoding String
     */
    public void setEncoding(final String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * @param inlines {@link Map}
     */
    public void setInlines(final Map<String, InlineDataSource> inlines)
    {
        this.inlineMap = inlines;
    }

    /**
     * @param message {@link MessageDataSource}
     */
    public void setMessage(final MessageDataSource message)
    {
        this.message = message;
    }

    /**
     * Text-Nachricht: text/plain, text/html
     *
     * @param messageContent String
     */
    public void setMessageContent(final String messageContent)
    {
        this.messageContent = messageContent;
    }

    /**
     * "text/plain; charset=UTF-8", "text/html; charset=UTF-8"
     *
     * @param messageContentType String
     */
    public void setMessageContentType(final String messageContentType)
    {
        this.messageContentType = messageContentType;
    }
}
