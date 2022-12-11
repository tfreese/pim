// Created: 09.12.2016
package de.freese.pim.core.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.activation.FileTypeMap;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;

import de.freese.pim.core.utils.MailUtils;

/**
 * Builder für eine {@link MimeMessage}.
 *
 * @author Thomas Freese
 */
public final class JavaMailBuilder
{
    public static JavaMailBuilder create(final Session session)
    {
        return new JavaMailBuilder(session, StandardCharsets.UTF_8.name(), FileTypeMap.getDefaultFileTypeMap(), true);
    }

    /**
     * @param fileTypeMap {@link FileTypeMap}; optional, wird für Attachments benötigt
     */
    public static JavaMailBuilder create(final Session session, final String charset, final FileTypeMap fileTypeMap, final boolean validateAddresses)
    {
        return new JavaMailBuilder(session, charset, fileTypeMap, validateAddresses);
    }

    private final List<MimeBodyPart> attachments = new ArrayList<>();

    private final String charset;

    private final FileTypeMap fileTypeMap;

    private final List<MimeBodyPart> inlines = new ArrayList<>();

    private final List<InternetAddress> recipientsBcc = new ArrayList<>();

    private final List<InternetAddress> recipientsCc = new ArrayList<>();

    private final List<InternetAddress> recipientsTo = new ArrayList<>();

    private final Session session;

    private final boolean validateAddresses;

    private InternetAddress from;

    private boolean isHTML;

    private String messageID;

    private String subject;

    private String text;

    /**
     * @param fileTypeMap {@link FileTypeMap}; optional, wird für Attachments benötigt
     */
    private JavaMailBuilder(final Session session, final String charset, final FileTypeMap fileTypeMap, final boolean validateAddresses)
    {
        super();

        this.session = Objects.requireNonNull(session, "session required");
        this.charset = Objects.requireNonNull(charset, "charset required");
        this.fileTypeMap = Objects.requireNonNull(fileTypeMap, "fileTypeMap required");
        this.validateAddresses = validateAddresses;
    }

    public JavaMailBuilder attachment(final String attachmentFilename, final DataSource dataSource) throws MessagingException
    {
        Objects.requireNonNull(attachmentFilename, "Attachment filename must not be null");
        Objects.requireNonNull(dataSource, "DataSource must not be null");

        try
        {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setDisposition(Part.ATTACHMENT);
            mimeBodyPart.setFileName(MimeUtility.encodeText(attachmentFilename));
            mimeBodyPart.setDataHandler(new DataHandler(dataSource));

            this.attachments.add(mimeBodyPart);
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new MessagingException("Failed to encode attachment filename", ex);
        }

        return this;
    }

    public JavaMailBuilder attachment(final String attachmentFilename, final File file) throws MessagingException
    {
        Objects.requireNonNull(attachmentFilename, "Attachment filename must not be null");
        Objects.requireNonNull(file, "File must not be null");

        FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(getFileTypeMap());
        attachment(attachmentFilename, dataSource);

        return this;
    }

    public JavaMailBuilder attachment(final String attachmentFilename, final InputStream inputStream) throws MessagingException
    {
        Objects.requireNonNull(attachmentFilename, "Attachment filename must not be null");
        Objects.requireNonNull(inputStream, "InputStream must not be null");

        String contentType = getFileTypeMap().getContentType(attachmentFilename);
        DataSource dataSource = createDataSource(inputStream, contentType, attachmentFilename);
        attachment(attachmentFilename, dataSource);

        return this;
    }

    public JavaMailBuilder bcc(final InternetAddress bcc) throws MessagingException
    {
        Objects.requireNonNull(bcc, "BCC address must not be null");

        validateAddress(bcc);

        this.recipientsBcc.add(bcc);

        return this;
    }

    public JavaMailBuilder bcc(final InternetAddress... bcc) throws MessagingException
    {
        for (InternetAddress internetAddress : bcc)
        {
            Objects.requireNonNull(internetAddress, "BCC address must not be null");

            validateAddress(internetAddress);

            this.recipientsBcc.add(internetAddress);
        }

        return this;
    }

    public JavaMailBuilder bcc(final String bcc) throws MessagingException
    {
        Objects.requireNonNull(bcc, "BCC address must not be null");

        bcc(parseAddress(bcc));

        return this;
    }

    public JavaMailBuilder bcc(final String bcc, final String personal) throws Exception
    {
        Objects.requireNonNull(bcc, "BCC address must not be null");

        bcc(getCharset() != null ? new InternetAddress(bcc, personal, getCharset()) : new InternetAddress(bcc, personal));

        return this;
    }

    public MimeMessage build() throws MessagingException
    {
        return build(null);
    }

    public MimeMessage build(final InputStream contentStream) throws MessagingException
    {
        MimeMessage mail = null;

        if (contentStream == null)
        {
            mail = new MimeMessage(getSession());
        }
        else
        {
            mail = new MimeMessage(getSession(), contentStream);
        }

        mail.setFrom(this.from);

        if (!this.recipientsTo.isEmpty())
        {
            mail.setRecipients(Message.RecipientType.TO, this.recipientsTo.toArray(new InternetAddress[0]));
        }

        if (!this.recipientsCc.isEmpty())
        {
            mail.setRecipients(Message.RecipientType.CC, this.recipientsCc.toArray(new InternetAddress[0]));
        }

        if (!this.recipientsBcc.isEmpty())
        {
            mail.setRecipients(Message.RecipientType.BCC, this.recipientsBcc.toArray(new InternetAddress[0]));
        }

        if (getCharset() != null)
        {
            mail.setSubject(this.subject, getCharset());
        }
        else
        {
            mail.setSubject(this.subject);
        }

        mail.setSentDate(new Date());

        if (this.messageID != null)
        {
            mail.setHeader(MailUtils.HEADER_MESSAGE_ID, this.messageID);
        }

        // mixed, für Attachments und Inlines
        MimeMultipart rootMultipart = new MimeMultipart(MailUtils.MULTIPART_SUBTYPE_MIXED);
        mail.setContent(rootMultipart);

        // related, für Text und Inlines
        MimeMultipart relatedMultipart = new MimeMultipart(MailUtils.MULTIPART_SUBTYPE_RELATED);
        MimeBodyPart relatedBodyPart = new MimeBodyPart();
        relatedBodyPart.setContent(relatedMultipart);
        rootMultipart.addBodyPart(relatedBodyPart);

        // Text
        if ((this.text != null) && (this.text.strip().length() > 0))
        {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            relatedMultipart.addBodyPart(textBodyPart);

            String contentType = MailUtils.CONTENT_TYPE_PLAIN;

            if (this.isHTML)
            {
                contentType = MailUtils.CONTENT_TYPE_HTML;
            }

            if (getCharset() != null)
            {
                textBodyPart.setContent(this.text, contentType + MailUtils.CONTENT_TYPE_CHARSET_SUFFIX + getCharset());
            }
            else
            {
                textBodyPart.setContent(this.text, contentType);
            }

            // if (getEncoding() != null)
            // {
            // textBodyPart.setText(this.text, getEncoding());
            // // textBodyPart.setContent(this.text, CONTENT_TYPE_PLAIN + CONTENT_TYPE_CHARSET_SUFFIX + getEncoding());
            // }
            // else
            // {
            // textBodyPart.setText(this.text);
            // // textBodyPart.setContent(this.text, CONTENT_TYPE_PLAIN);
            // }
        }

        // Inlines
        for (MimeBodyPart inline : this.inlines)
        {
            relatedMultipart.addBodyPart(inline);
        }

        // Attachments
        for (MimeBodyPart attachment : this.attachments)
        {
            rootMultipart.addBodyPart(attachment);
        }

        return mail;
    }

    public void buildAndSend(final JavaMailSender mailSender) throws Exception
    {
        buildAndSend(mailSender, null);
    }

    public void buildAndSend(final JavaMailSender mailSender, final InputStream contentStream) throws Exception
    {
        Objects.requireNonNull(mailSender, "sender required");

        MimeMessage mail = build(contentStream);

        mailSender.send(mail);
    }

    public JavaMailBuilder cc(final InternetAddress cc) throws MessagingException
    {
        Objects.requireNonNull(cc, "CC address must not be null");

        validateAddress(cc);

        this.recipientsCc.add(cc);

        return this;
    }

    public JavaMailBuilder cc(final InternetAddress... cc) throws MessagingException
    {
        for (InternetAddress internetAddress : cc)
        {
            Objects.requireNonNull(internetAddress, "CC address must not be null");

            validateAddress(internetAddress);

            this.recipientsCc.add(internetAddress);
        }

        return this;
    }

    public JavaMailBuilder cc(final String cc) throws MessagingException
    {
        Objects.requireNonNull(cc, "CC address must not be null");

        cc(parseAddress(cc));

        return this;
    }

    public JavaMailBuilder cc(final String cc, final String personal) throws Exception
    {
        Objects.requireNonNull(cc, "CC address must not be null");

        cc(getCharset() != null ? new InternetAddress(cc, personal, getCharset()) : new InternetAddress(cc, personal));

        return this;
    }

    public JavaMailBuilder from(final InternetAddress from) throws MessagingException
    {
        Objects.requireNonNull(from, "From address must not be null");

        validateAddress(from);

        this.from = from;

        return this;
    }

    public JavaMailBuilder from(final String from) throws MessagingException
    {
        Objects.requireNonNull(from, "From address must not be null");

        from(parseAddress(from));

        return this;
    }

    public JavaMailBuilder from(final String from, final String personal) throws Exception
    {
        Objects.requireNonNull(from, "From address must not be null");

        from(getCharset() != null ? new InternetAddress(from, personal, getCharset()) : new InternetAddress(from, personal));

        return this;
    }

    public JavaMailBuilder inline(final String contentID, final DataSource dataSource) throws MessagingException
    {
        Objects.requireNonNull(contentID, "Content ID must not be null");
        Objects.requireNonNull(dataSource, "DataSource must not be null");

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDisposition(Part.INLINE);
        // We're using setHeader here to remain compatible with JavaMail 1.2,
        // rather than JavaMail 1.3's setContentID.
        mimeBodyPart.setHeader(MailUtils.HEADER_CONTENT_ID, "<" + contentID + ">");
        mimeBodyPart.setDataHandler(new DataHandler(dataSource));

        this.inlines.add(mimeBodyPart);

        return this;
    }

    public JavaMailBuilder inline(final String contentID, final File file) throws MessagingException
    {
        Objects.requireNonNull(contentID, "Content ID must not be null");
        Objects.requireNonNull(file, "File must not be null");

        FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(getFileTypeMap());
        inline(contentID, dataSource);

        return this;
    }

    public JavaMailBuilder inline(final String contentID, final InputStream inputStream, final String mimeType) throws MessagingException
    {
        Objects.requireNonNull(contentID, "Content ID must not be null");
        Objects.requireNonNull(inputStream, "InputStreamSource must not be null");

        // String contentType = getFileTypeMap().getContentType(attachmentFilename);
        DataSource dataSource = createDataSource(inputStream, mimeType, "inline");
        inline(contentID, dataSource);

        return this;
    }

    public JavaMailBuilder messageID(final String messageID)
    {
        this.messageID = Objects.requireNonNull(messageID, "messageID required");

        return this;
    }

    public JavaMailBuilder subject(final String subject)
    {
        this.subject = Objects.requireNonNull(subject, "subject required");

        return this;
    }

    public JavaMailBuilder text(final String text, final boolean isHTML)
    {
        this.text = Objects.requireNonNull(text, "Text must not be null");
        this.isHTML = isHTML;

        return this;
    }

    public JavaMailBuilder to(final InternetAddress to) throws MessagingException
    {
        Objects.requireNonNull(to, "To address must not be null");

        validateAddress(to);

        this.recipientsTo.add(to);

        return this;
    }

    public JavaMailBuilder to(final InternetAddress... to) throws MessagingException
    {
        for (InternetAddress internetAddress : to)
        {
            Objects.requireNonNull(internetAddress, "To address must not be null");

            validateAddress(internetAddress);

            this.recipientsTo.add(internetAddress);
        }

        return this;
    }

    public JavaMailBuilder to(final String to) throws MessagingException
    {
        Objects.requireNonNull(to, "To address must not be null");

        to(parseAddress(to));

        return this;
    }

    public JavaMailBuilder to(final String to, final String personal) throws Exception
    {
        Objects.requireNonNull(to, "To address must not be null");

        to(getCharset() != null ? new InternetAddress(to, personal, getCharset()) : new InternetAddress(to, personal));

        return this;
    }

    private DataSource createDataSource(final InputStream inputStream, final String contentType, final String name)
    {
        return new DataSource()
        {
            /**
             * @see jakarta.activation.DataSource#getContentType()
             */
            @Override
            public String getContentType()
            {
                return contentType;
            }

            /**
             * @see jakarta.activation.DataSource#getInputStream()
             */
            @Override
            public InputStream getInputStream() throws IOException
            {
                return inputStream;
            }

            /**
             * @see jakarta.activation.DataSource#getName()
             */
            @Override
            public String getName()
            {
                return name;
            }

            /**
             * @see jakarta.activation.DataSource#getOutputStream()
             */
            @Override
            public OutputStream getOutputStream()
            {
                throw new UnsupportedOperationException("Read-only jakarta.activation.DataSource");
            }
        };
    }

    private String getCharset()
    {
        return this.charset;
    }

    private FileTypeMap getFileTypeMap()
    {
        return this.fileTypeMap;
    }

    private Session getSession()
    {
        return this.session;
    }

    private InternetAddress parseAddress(final String address) throws MessagingException
    {
        InternetAddress[] parsed = InternetAddress.parse(address);

        if (parsed.length != 1)
        {
            throw new AddressException("Illegal address", address);
        }

        InternetAddress raw = parsed[0];

        try
        {
            return (getCharset() != null ? new InternetAddress(raw.getAddress(), raw.getPersonal(), getCharset()) : raw);
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new MessagingException("Failed to parse embedded personal name to correct encoding", ex);
        }
    }

    /**
     * Validate the given mail address. Called by all of MimeMessageHelper's address setters and adders.
     * <p>
     * Default implementation invokes {@code InternetAddress.validate()}, provided that address validation is activated for the helper instance.
     * <p>
     * Note that this method will just work on JavaMail >= 1.3. You can override it for validation on older JavaMail versions or for custom validation.
     *
     * @see jakarta.mail.internet.InternetAddress#validate()
     */
    private void validateAddress(final InternetAddress address) throws AddressException
    {
        if (this.validateAddresses)
        {
            address.validate();
        }
    }
}
