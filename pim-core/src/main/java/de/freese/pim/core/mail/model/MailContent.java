// Created: 26.01.2017
package de.freese.pim.core.mail.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import de.freese.pim.core.mail.utils.MailUtils;

/**
 * Container für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
public class MailContent
{
    /**
    *
    */
    private final Map<String, MimeBodyPart> attachmentMap = new TreeMap<>();

    /**
     *
     */
    private final DataSource dataSource;

    /**
     *
     */
    private final String encoding;

    /**
     *
     */
    private final Map<String, MimeBodyPart> inlineMap = new HashMap<>();

    /**
     *
     */
    private final MimeMessage message;

    /**
    *
    */
    private final DataSource messageDataSource;

    /**
     *
     */
    private URL url = null;

    /**
     * Erzeugt eine neue Instanz von {@link MailContent}
     *
     * @param dataSource {@link DataSource}
     * @param url {@link URL}
     * @throws IOException Falls was schief geht.
     */
    public MailContent(final DataSource dataSource, final URL url) throws IOException
    {
        super();

        Objects.requireNonNull(dataSource, "dataSource required");

        this.dataSource = dataSource;
        this.url = url;

        this.message = null;
        this.messageDataSource = null;

        try (InputStreamReader isr = new InputStreamReader(getInputStream()))
        {
            this.encoding = isr.getEncoding();
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link MailContent}
     *
     * @param message {@link MimeMessage}
     * @throws IOException Falls was schief geht.
     */
    public MailContent(final MimeMessage message) throws IOException
    {
        super();

        Objects.requireNonNull(message, "message required");

        this.message = message;

        this.dataSource = null;

        try
        {
            this.messageDataSource = MailUtils.getTextDataSource(this.message);
            this.encoding = getMessageEncoding(this.message, this.messageDataSource);

            // Inlines
            this.inlineMap.putAll(MailUtils.getInlineMap(this.message));

            // Attachments
            this.attachmentMap.putAll(MailUtils.getAttachmentMap(this.message));
        }
        catch (MessagingException ex)
        {
            throw new IOException(ex);
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link MailContent}
     *
     * @param path {@link Path}
     * @throws IOException Falls was schief geht.
     */
    public MailContent(final Path path) throws IOException
    {
        this(new FileDataSource(path.toFile()), path.toUri().toURL());
    }

    /**
     * Liefert die {@link DataSource} des Attachements.
     *
     * @return {@link Map}
     */
    public Map<String, MimeBodyPart> getAttachments()
    {
        return this.attachmentMap;
    }

    /**
     * Liefert den Text der Mail.
     *
     * @return String
     * @throws IOException Falls was schief geht.
     * @throws RuntimeException Falls was schief geht.
     */
    public String getContent() throws IOException
    {
        // StandardCharsets.UTF_8
        String content = null;

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(getInputStream(), Charset.forName(getEncoding()))))
        {
            content = buffer.lines().collect(Collectors.joining("\n"));
        }

        return content;
    }

    /**
     * "text/html", "text/plain"
     *
     * @return String
     */
    public String getContentType()
    {
        String contentType = this.dataSource.getContentType();

        // contentType = contentType.replaceAll(System.getProperty("line.separator"), "");
        contentType = contentType.replaceAll("\\r\\n|\\r|\\n", "");

        return contentType;
    }

    /**
     * Liefert das Encoding oder null
     *
     * @return String
     */
    public String getEncoding()
    {
        return this.encoding;
    }

    /**
     * Liefert die {@link DataSource} des Inlines.
     *
     * @param contentID String
     * @return {@link DataSource} oder null
     * @throws IOException Falls was schief geht.
     */
    public DataSource getInlineDataSource(final String contentID) throws IOException
    {
        MimeBodyPart mimeBodyPart = this.inlineMap.get(contentID);

        if (mimeBodyPart != null)
        {
            try
            {
                return mimeBodyPart.getDataHandler().getDataSource();
            }
            catch (MessagingException ex)
            {
                throw new IOException(ex);
            }
        }

        return null;
    }

    /**
     * Liefert den {@link InputStream}.
     *
     * @return {@link InputStream}
     * @throws IOException Falls was schief geht.
     */
    public InputStream getInputStream() throws IOException
    {
        return this.dataSource.getInputStream();
    }

    /**
     * Liefert den Text der Mail.
     *
     * @return String
     * @throws IOException Falls was schief geht.
     * @throws RuntimeException Falls was schief geht.
     */
    public String getMessage() throws IOException
    {
        if (getMessageDataSource() == null)
        {
            return null;
        }

        String content = null;

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(getMessageDataSource().getInputStream(), getEncoding())))
        {
            content = buffer.lines().collect(Collectors.joining("\n"));
        }

        return content;
    }

    /**
     * "text/html; charset=UTF-8", "text/html; charset=UTF-8"
     *
     * @return String
     * @throws IOException Falls was schief geht.
     */
    public String getMessageContentType() throws IOException
    {
        String contentType = getMessageDataSource().getContentType();

        contentType = Optional.ofNullable(contentType).map(ct -> ct.split("[;]")[0]).orElse("text/plain");

        // contentType = contentType.replaceAll(System.getProperty("line.separator"), "");
        contentType = contentType.replaceAll("\\r\\n|\\r|\\n", "");

        return contentType;
    }

    /**
     * Liefert die {@link DataSource} deer Nachricht (Text).
     *
     * @return {@link DataSource}
     * @throws IOException Falls was schief geht.
     */
    public DataSource getMessageDataSource() throws IOException
    {
        return this.messageDataSource;
    }

    /**
     * Liefert das Encoding der Message.
     *
     * @param message {@link MimeMessage}
     * @param messageDataSource {@link DataSource}
     * @return String
     * @throws MessagingException Falls was schief geht.
     */
    private String getMessageEncoding(final MimeMessage message, final DataSource messageDataSource) throws MessagingException
    {
        String encoding = message.getEncoding();

        if (StringUtils.isBlank(encoding))
        {
            // text/html; charset=UTF-8
            if (messageDataSource.getContentType().indexOf(';') > 0)
            {
                encoding = messageDataSource.getContentType().split("[;]")[1].split("[=]")[1].replaceAll("[\"]", "").toUpperCase();
            }
            else
            {
                encoding = "UTF-8";
            }
        }

        // StandardCharsets.UTF_8
        return encoding;
    }

    /**
     * Liefert die {@link URL} zum Content.
     *
     * @return {@link URL}
     */
    public URL getUrl()
    {
        return this.url;
    }
}
