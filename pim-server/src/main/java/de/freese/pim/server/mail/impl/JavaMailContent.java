// Created: 26.01.2017
package de.freese.pim.server.mail.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

import org.apache.commons.lang3.StringUtils;

import de.freese.pim.server.mail.api.IMailContent;
import de.freese.pim.server.mail.utils.MailUtils;

/**
 * Container für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class JavaMailContent implements IMailContent
{
    /**
    *
    */
    private final Map<String, DataSource> attachmentMap = new TreeMap<>();

    /**
     *
     */
    private final String encoding;

    /**
     *
     */
    private final Map<String, DataSource> inlineMap = new HashMap<>();

    /**
     *
     */
    private final String messageContent;

    /**
    *
    */
    private final String messageContentType;

    // /**
    // * Erzeugt eine neue Instanz von {@link MailContent}
    // *
    // * @param dataSource {@link DataSource}
    // * @param url {@link URL}
    // * @throws IOException Falls was schief geht.
    // */
    // public MailContent(final DataSource dataSource, final URL url) throws IOException
    // {
    // super();
    //
    // Objects.requireNonNull(dataSource, "dataSource required");
    //
    // this.dataSource = dataSource;
    // this.url = url;
    //
    // this.message = null;
    // this.messageDataSource = null;
    // this.messageContent = null;
    //
    // try (InputStreamReader isr = new InputStreamReader(getInputStream()))
    // {
    // this.encoding = isr.getEncoding();
    // }
    // }

    /**
     * Erzeugt eine neue Instanz von {@link JavaMailContent}
     *
     * @param message {@link MimeMessage}
     * @throws Exception Falls was schief geht.
     */
    public JavaMailContent(final MimeMessage message) throws Exception
    {
        super();

        Objects.requireNonNull(message, "message required");

        DataSource messageDataSource = MailUtils.getTextDataSource(message);

        String enc = null;
        // enc = message.getEncoding();

        // "text/html; charset=UTF-8", "text/html; charset=UTF-8"
        ContentType ct = new ContentType(messageDataSource.getContentType());
        this.messageContentType = ct.getBaseType();

        if (StringUtils.isBlank(enc))
        {
            enc = ct.getParameter("charset");
        }

        if (StringUtils.isBlank(enc))
        {
            enc = "UTF-8";
        }

        this.encoding = enc;

        // this.encoding = detectMessageEncoding(message, messageDataSource);
        // String contentType = messageDataSource.getContentType();
        // contentType = Optional.ofNullable(contentType).map(ct -> ct.split("[;]")[0]).orElse("text/plain");
        // contentType = contentType.replaceAll(System.getProperty("line.separator"), "");
        // contentType = contentType.replaceAll("\\r\\n|\\r|\\n", "");
        // this.messageContentType = contentType;

        // Text-Nachricht: text/plain, text/html
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(messageDataSource.getInputStream(), this.encoding)))
        {
            this.messageContent = buffer.lines().collect(Collectors.joining("\n"));
        }

        // Inlines
        for (Entry<String, MimePart> entry : MailUtils.getInlineMap(message).entrySet())
        {
            this.inlineMap.put(entry.getKey(), entry.getValue().getDataHandler().getDataSource());
        }

        // Attachments
        for (Entry<String, MimePart> entry : MailUtils.getAttachmentMap(message).entrySet())
        {
            this.attachmentMap.put(entry.getKey(), entry.getValue().getDataHandler().getDataSource());

            // Test
            // String fileName = entry.getValue().getFileName();
            // String dsName = entry.getValue().getDataHandler().getDataSource().getName();
            // System.out.printf("%s / %s%n", fileName, dsName);
        }
    }

    // /**
    // * Erzeugt eine neue Instanz von {@link MailContent}
    // *
    // * @param path {@link Path}
    // * @throws IOException Falls was schief geht.
    // */
    // public MailContent(final Path path) throws IOException
    // {
    // this(new FileDataSource(path.toFile()), path.toUri().toURL());
    // }

    // /**
    // * Liefert das Encoding der Message.
    // *
    // * @param message {@link MimeMessage}
    // * @param dataSource {@link DataSource}
    // * @return String
    // * @throws MessagingException Falls was schief geht.
    // */
    // private String detectMessageEncoding(final MimeMessage message, final DataSource dataSource) throws MessagingException
    // {
    // String encoding = message.getEncoding();
    //
    // if (StringUtils.isBlank(encoding))
    // {
    // if (dataSource.getContentType().indexOf(';') > 0)
    // {
    // encoding = dataSource.getContentType().split("[;]")[1].split("[=]")[1].replaceAll("[\"]", "").toUpperCase();
    // }
    // else
    // {
    // encoding = "UTF-8";
    // }
    // }
    //
    // // StandardCharsets.UTF_8
    // return encoding;
    // }

    /**
     * @see de.freese.pim.server.mail.api.IMailContent#getAttachments()
     */
    @Override
    public Map<String, DataSource> getAttachments()
    {
        return this.attachmentMap;
    }

    /**
     * @see de.freese.pim.server.mail.api.IMailContent#getEncoding()
     */
    @Override
    public String getEncoding()
    {
        return this.encoding;
    }

    /**
     * @see de.freese.pim.server.mail.api.IMailContent#getInlineDataSource(java.lang.String)
     */
    @Override
    public DataSource getInlineDataSource(final String contentID) throws IOException
    {
        DataSource inline = this.inlineMap.get(contentID);

        return inline;
    }

    /**
     * @see de.freese.pim.server.mail.api.IMailContent#getMessageContent()
     */
    @Override
    public String getMessageContent()
    {
        return this.messageContent;
    }

    /**
     * @see de.freese.pim.server.mail.api.IMailContent#getMessageContentType()
     */
    @Override
    public String getMessageContentType()
    {
        return this.messageContentType;
    }
}
