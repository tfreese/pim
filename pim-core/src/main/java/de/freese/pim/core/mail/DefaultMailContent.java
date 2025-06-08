// Created: 16.02.2017
package de.freese.pim.core.mail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.datasource.AttachmentDataSource;
import de.freese.pim.core.mail.datasource.InlineDataSource;
import de.freese.pim.core.mail.datasource.MessageDataSource;

/**
 * Container für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class DefaultMailContent implements MailContent {
    private Map<String, AttachmentDataSource> attachmentMap;

    private String encoding;

    private Map<String, InlineDataSource> inlineMap;

    private MessageDataSource message;

    @JsonIgnore
    private String messageContent;

    private String messageContentType;

    @Override
    public Map<String, AttachmentDataSource> getAttachments() {
        return attachmentMap;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public Map<String, InlineDataSource> getInlines() {
        return inlineMap;
    }

    @Override
    public MessageDataSource getMessage() {
        return message;
    }

    /**
     * Text-Nachricht: text/plain, text/html
     */
    @Override
    public String getMessageContent() {
        if (messageContent == null) {
            try {
                try (BufferedReader buffer = new BufferedReader(new InputStreamReader(getMessage().getInputStream(), getEncoding()))) {
                    messageContent = buffer.lines().collect(Collectors.joining("\n"));
                }
            }
            catch (Exception ex) {
                LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
            }

        }

        return messageContent;
    }

    /**
     * "text/plain; charset=UTF-8", "text/html; charset=UTF-8"
     */
    @Override
    public String getMessageContentType() {
        return messageContentType;
    }

    public void setAttachments(final Map<String, AttachmentDataSource> attachments) {
        this.attachmentMap = attachments;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setInlines(final Map<String, InlineDataSource> inlines) {
        this.inlineMap = inlines;
    }

    public void setMessage(final MessageDataSource message) {
        this.message = message;
    }

    /**
     * Text-Nachricht: text/plain, text/html
     */
    public void setMessageContent(final String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * "text/plain; charset=UTF-8", "text/html; charset=UTF-8"
     */
    public void setMessageContentType(final String messageContentType) {
        this.messageContentType = messageContentType;
    }
}
