// Created: 26.01.2017
package de.freese.pim.common.model.mail;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import javax.activation.DataSource;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.freese.pim.common.model.mail.datasource.AttachmentDataSource;
import de.freese.pim.common.model.mail.datasource.InlineDataSource;
import de.freese.pim.common.model.mail.datasource.MessageDataSource;
import de.freese.pim.common.utils.MailUtils;

/**
 * JavaMail-Container für den Inhalt einer Mail.
 *
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JavaMailContent extends DefaultMailContent
{
    // /**
    // * Erzeugt eine neue Instanz von {@link JavaMailContent}
    // *
    // * @param dataSource {@link DataSource}
    // * @param url {@link URL}
    // * @throws IOException Falls was schief geht.
    // */
    // public JavaMailContent(final DataSource dataSource, final URL url) throws IOException
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

    // /**
    // * Erzeugt eine neue Instanz von {@link JavaMailContent}
    // *
    // * @param path {@link Path}
    // * @throws IOException Falls was schief geht.
    // */
    // public JavaMailContent(final Path path) throws IOException
    // {
    // this(new FileDataSource(path.toFile()), path.toUri().toURL());
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
        setMessage(new MessageDataSource(messageDataSource));

        String encoding = null;
        encoding = message.getEncoding();

        // "text/plain; charset=UTF-8", "text/html; charset=UTF-8"
        ContentType ct = new ContentType(messageDataSource.getContentType());
        setMessageContentType(ct.getBaseType());

        if (StringUtils.isBlank(encoding))
        {
            encoding = MimeUtility.javaCharset(ct.getParameter("charset"));
        }

        if (StringUtils.isBlank(encoding))
        {
            encoding = "UTF-8";
        }

        setEncoding(encoding);

        // String contentType = messageDataSource.getContentType();
        // contentType = Optional.ofNullable(contentType).map(ct -> ct.split("[;]")[0]).orElse("text/plain");
        // contentType = contentType.replaceAll(System.getProperty("line.separator"), "");
        // contentType = contentType.replaceAll("\\r\\n|\\r|\\n", "");

        // Text-Nachricht: text/plain, text/html
        // try (BufferedReader buffer = new BufferedReader(new InputStreamReader(messageDataSource.getInputStream(), encoding)))
        // {
        // String messageContent = buffer.lines().collect(Collectors.joining("\n"));
        // setMessageContent(messageContent);
        // }

        // Inlines
        Map<String, InlineDataSource> mapInlines = new TreeMap<>();

        for (Entry<String, MimePart> entry : MailUtils.getInlineMap(message).entrySet())
        {
            mapInlines.put(entry.getKey(), new InlineDataSource(entry.getValue().getDataHandler().getDataSource()));
        }

        setInlines(mapInlines);

        // Attachments
        Map<String, AttachmentDataSource> mapAttachments = new TreeMap<>();

        for (Entry<String, MimePart> entry : MailUtils.getAttachmentMap(message).entrySet())
        {
            mapAttachments.put(entry.getKey(), new AttachmentDataSource(entry.getValue().getDataHandler().getDataSource()));

            // Test
            // String fileName = entry.getValue().getFileName();
            // String dsName = entry.getValue().getDataHandler().getDataSource().getName();
            // System.out.printf("%s / %s%n", fileName, dsName);
        }

        setAttachments(mapAttachments);
    }
}
