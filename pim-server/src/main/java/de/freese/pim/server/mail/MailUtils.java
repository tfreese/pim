/**
 * Created: 28.12.2016
 */

package de.freese.pim.server.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import de.freese.pim.common.model.mail.InternetAddress;

/**
 * Mail-Utils.
 *
 * @author Thomas Freese
 */
public final class MailUtils
{
    /**
     * @author Thomas Freese
     */
    public static abstract class AbstractTextPart
    {
        /**
         *
         */
        private final String content;

        /**
         *
         */
        private final String mimeType;

        /**
         * Erstellt ein neues {@link AbstractTextPart} Object.
         *
         * @param content String
         * @param mimeType String
         */
        private AbstractTextPart(final String content, final String mimeType)
        {
            super();

            this.content = Objects.requireNonNull(content, "content required");
            this.mimeType = Objects.requireNonNull(mimeType, "mimeType required");
        }

        /**
         * @return String
         */
        public String getContent()
        {
            return this.content;
        }

        /**
         * @return String
         */
        public String getMimeType()
        {
            return this.mimeType;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return getMimeType() + ": " + getContent();
        }
    }

    /**
     * @author Thomas Freese
     */
    public static final class HTMLTextPart extends AbstractTextPart
    {
        /**
         * Erstellt ein neues {@link HTMLTextPart} Object.
         *
         * @param text String
         */
        private HTMLTextPart(final String text)
        {
            super(text, CONTENT_TYPE_HTML);
        }
    }

    /**
     * @author Thomas Freese
     */
    public static final class PlainTextPart extends AbstractTextPart
    {
        /**
         * Erstellt ein neues {@link PlainTextPart} Object.
         *
         * @param text String
         */
        private PlainTextPart(final String text)
        {
            super(text, CONTENT_TYPE_PLAIN);
        }
    }

    /**
     *
     */
    public static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";

    /**
     *
     */
    public static final String CONTENT_TYPE_HTML = "text/html";

    /**
     *
     */
    public static final String CONTENT_TYPE_PLAIN = "text/plain";

    /**
     *
     */
    public static final String HEADER_CONTENT_ID = "Content-ID";

    /**
     *
     */
    public static final String HEADER_MESSAGE_ID = "Message-ID";

    /**
     *
     */
    public static final String MULTIPART_SUBTYPE_MIXED = "mixed";

    /**
     *
     */
    public static final String MULTIPART_SUBTYPE_RELATED = "related";

    /**
     * Liefert alle vorhandenen Attachment-MimeParts einer {@link Message}.<br>
     * Key = Filename<br>
     * Value = {@link MimePart}<br>
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link Map}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static Map<String, MimePart> getAttachmentMap(final Part part) throws MessagingException, IOException
    {
        Map<String, MimePart> map = new HashMap<>();

        List<MimePart> attachments = getAttachments(part);

        for (MimePart attachment : attachments)
        {
            String fileName = Optional.ofNullable(attachment.getFileName()).orElse("Mail");
            fileName = MimeUtility.decodeText(fileName);

            map.put(fileName, attachment);
        }

        return map;
    }

    /**
     * Liefert alle vorhandenen Attachments-MimeParts einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static List<MimePart> getAttachments(final Part part) throws MessagingException, IOException
    {
        List<MimePart> bodyParts = new ArrayList<>();

        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
        {
            bodyParts.add((MimePart) part);
        }
        else if (part.isMimeType("multipart/*") || part.isMimeType("MULTIPART/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<MimePart> list = getAttachments(bp);

                if (CollectionUtils.isNotEmpty(list))
                {
                    bodyParts.addAll(list);
                }
            }
        }

        return bodyParts;
    }

    /**
     * Liefert alle vorhandenen Inline-MimeParts einer {@link Message}.<br>
     * Key = ContentID<br>
     * Value = {@link MimePart}<br>
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link Map}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static Map<String, MimePart> getInlineMap(final Part part) throws MessagingException, IOException
    {
        Map<String, MimePart> map = new HashMap<>();

        List<MimePart> inlines = getInlines(part);

        for (MimePart inline : inlines)
        {
            String[] contentIDs = Optional.ofNullable(inline.getHeader(HEADER_CONTENT_ID)).orElse(null);

            if (ArrayUtils.isEmpty(contentIDs))
            {
                continue;
            }

            for (String contentID : contentIDs)
            {
                contentID = contentID.replace("<", "").replace(">", "");

                map.put(contentID, inline);
            }
        }

        return map;
    }

    /**
     * Liefert alle vorhandenen Inline-MimeParts einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static List<MimePart> getInlines(final Part part) throws MessagingException, IOException
    {
        List<MimePart> bodyParts = new ArrayList<>();

        if (Part.INLINE.equalsIgnoreCase(part.getDisposition()))
        {
            bodyParts.add((MimePart) part);
        }
        else if (part.isMimeType("multipart/*") || part.isMimeType("MULTIPART/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<MimePart> list = getInlines(bp);

                if (CollectionUtils.isNotEmpty(list))
                {
                    bodyParts.addAll(list);
                }
            }
        }

        return bodyParts;
    }

    /**
     * Liefert die Gesamtgröße aller einzelnen Parts.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return long
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static long getSizeOfAllParts(final Part part) throws MessagingException, IOException
    {
        long size = part.getSize();

        if (size < 0)
        {
            size = 0;
        }

        if (part.getContent() instanceof Multipart)
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                size += getSizeOfAllParts(bp);
            }
        }

        return size;
    }

    /**
     * Liefert die {@link DataSource} für den Text (text/plain, text/html) einer {@link Message}.<br>
     * Dabei wird zuerst nach HTML gesucht, dann nach Plain-Text.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static DataSource getTextDataSource(final Part part) throws MessagingException, IOException
    {
        List<DataSource> dataSources = getTextDataSources(part);

        Optional<DataSource> dataSource = dataSources.stream().filter(ds -> ds.getContentType().toLowerCase().startsWith(CONTENT_TYPE_HTML)).findFirst();

        if (!dataSource.isPresent())
        {
            // Kein HTML gefunden -> nach Plain-Text suchen.
            dataSource = dataSources.stream().filter(ds -> ds.getContentType().toLowerCase().startsWith(CONTENT_TYPE_PLAIN)).findFirst();
        }

        return dataSource.orElse(null);
    }

    /**
     * Liefert alle vorhandenen Text-DataSources (text/plain, text/html) einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static List<DataSource> getTextDataSources(final Part part) throws MessagingException, IOException
    {
        List<DataSource> dataSources = new ArrayList<>();

        // part.getContentType();

        if (part.isMimeType("text/plain") || part.isMimeType("TEXT/plain") || part.isMimeType("text/html") || part.isMimeType("TEXT/html"))
        {
            dataSources.add(part.getDataHandler().getDataSource());
        }
        else if (part.isMimeType("multipart/*") || part.isMimeType("MULTIPART/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<DataSource> ds = getTextDataSources(bp);

                if (CollectionUtils.isNotEmpty(ds))
                {
                    dataSources.addAll(ds);
                }
            }
        }

        return dataSources;
    }

    /**
     * Liefert alle vorhandenen Text-Parts (text/plain, text/html) einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static List<AbstractTextPart> getTextParts(final Part part) throws MessagingException, IOException
    {
        List<AbstractTextPart> textParts = new ArrayList<>();

        if (part.isMimeType("text/*") || part.isMimeType("TEXT/*"))
        {
            if (!(part.getContent() instanceof String))
            {
                return null;
            }

            String text = (String) part.getContent();

            if (part.isMimeType("text/plain") || part.isMimeType("TEXT/plain"))
            {
                textParts.add(new PlainTextPart(text));
            }
            else if (part.isMimeType("text/html") || part.isMimeType("TEXT/html"))
            {
                textParts.add(new HTMLTextPart(text));
            }
        }
        else if (part.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<AbstractTextPart> tp = getTextParts(bp);

                if (CollectionUtils.isNotEmpty(tp))
                {
                    textParts.addAll(tp);
                }
            }
        }

        return textParts;
    }

    /**
     * @param address {@link javax.mail.internet.InternetAddress}
     * @return {@link InternetAddress}
     */
    public static InternetAddress map(final javax.mail.internet.InternetAddress address)
    {
        if (address == null)
        {
            return null;
        }

        return new InternetAddress(address.getAddress(), address.getPersonal());

    }

    /**
     * @param addresses {@link javax.mail.internet.InternetAddress}[]
     * @return {@link InternetAddress}[]
     */
    public static InternetAddress[] map(final javax.mail.internet.InternetAddress[] addresses)
    {
        if (addresses == null)
        {
            return null;
        }

        return Stream.of(addresses).map(MailUtils::map).toArray(InternetAddress[]::new);

        // InternetAddress[] ia = new InternetAddress[addresses.length];
        //
        // for (int i = 0; i < addresses.length; i++)
        // {
        // ia[i] = new InternetAddress(addresses[i].getAddress(), addresses[i].getPersonal());
        // }
        //
        // return ia;
    }

    /**
     * Erstellt ein neues {@link MailUtils} Object.
     */
    private MailUtils()
    {
        super();
    }
}
