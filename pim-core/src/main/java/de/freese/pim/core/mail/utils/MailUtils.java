/**
 * Created: 28.12.2016
 */

package de.freese.pim.core.mail.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.collections4.CollectionUtils;

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

            Objects.requireNonNull(content, "content required");
            Objects.requireNonNull(mimeType, "mimeType required");

            this.content = content;
            this.mimeType = mimeType;
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
    public static class HTMLTextPart extends AbstractTextPart
    {
        /**
         * Erstellt ein neues {@link HTMLTextPart} Object.
         *
         * @param text String
         */
        private HTMLTextPart(final String text)
        {
            super(text, "text/html");
        }
    }

    /**
     * @author Thomas Freese
     */
    public static class PlainTextPart extends AbstractTextPart
    {
        /**
         * Erstellt ein neues {@link PlainTextPart} Object.
         *
         * @param text String
         */
        private PlainTextPart(final String text)
        {
            super(text, "text/plain");
        }
    }

    /**
     * ^(.+)@(.+)\\.\\w{2,3}$
     */
    public static final String MAIL_REGEX = "^(.+)@(.+)\\.[a-zA-Z]{2,3}$";

    /**
     * Liefert alle vorhandenen Attachments-MimeBodyPart einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static List<MimeBodyPart> getAttachments(final Part part) throws MessagingException, IOException
    {
        List<MimeBodyPart> bodyParts = new ArrayList<>();

        if (Part.ATTACHMENT.toLowerCase().equals(part.getDisposition().toLowerCase()))
        {
            bodyParts.add((MimeBodyPart) part);
        }
        else if (part.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<MimeBodyPart> list = getAttachments(bp);

                if (CollectionUtils.isNotEmpty(list))
                {
                    bodyParts.addAll(list);
                }
            }
        }

        return bodyParts;
    }

    /**
     * Liefert alle vorhandenen Inline-MimeBodyPart einer {@link Message}.
     *
     * @param part {@link Part}, @see {@link Message}
     * @return {@link List}; ist niemals null
     * @throws IOException Falls was schief geht.
     * @throws MessagingException Falls was schief geht.
     */
    public static List<MimeBodyPart> getInlines(final Part part) throws MessagingException, IOException
    {
        List<MimeBodyPart> bodyParts = new ArrayList<>();

        if (Part.INLINE.toLowerCase().equals(part.getDisposition().toLowerCase()))
        {
            bodyParts.add((MimeBodyPart) part);
        }
        else if (part.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);

                List<MimeBodyPart> list = getInlines(bp);

                if (CollectionUtils.isNotEmpty(list))
                {
                    bodyParts.addAll(list);
                }
            }
        }

        return bodyParts;
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

        // if(part instanceof MimePart)
        // {
        // String encoding = ((MimePart) part).getEncoding();
        // }

        if (part.isMimeType("text/plain") || part.isMimeType("text/html"))
        {
            dataSources.add(part.getDataHandler().getDataSource());
        }
        else if (part.isMimeType("multipart/*"))
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

        if (part.isMimeType("text/*"))
        {
            if (!(part.getContent() instanceof String))
            {
                return null;
            }

            String text = (String) part.getContent();

            if (part.isMimeType("text/plain"))
            {
                textParts.add(new PlainTextPart(text));
            }
            else if (part.isMimeType("text/html"))
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
     * Erstellt ein neues {@link MailUtils} Object.
     */
    private MailUtils()
    {
        super();
    }
}
