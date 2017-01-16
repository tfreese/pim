/**
 * Created: 28.12.2016
 */

package de.freese.pim.core.mail.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

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
        private final String text;

        /**
         * Erstellt ein neues {@link AbstractTextPart} Object.
         *
         * @param text String
         */
        private AbstractTextPart(final String text)
        {
            super();

            this.text = text;
        }

        /**
         * @return String
         */
        public String getText()
        {
            return this.text;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return getText();
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
            super(text);
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
            super(text);
        }
    }

    /**
     * ^(.+)@(.+)\\.\\w{2,3}$
     */
    public static final String MAIL_REGEX = "^(.+)@(.+)\\.[a-zA-Z]{2,3}$";

    /**
     * Liefert alle vorhandenen Text Parts einer {@link Message}.
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
