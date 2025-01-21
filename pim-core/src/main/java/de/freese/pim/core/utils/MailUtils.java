// Created: 28.12.2016
package de.freese.pim.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimePart;
import jakarta.mail.internet.MimeUtility;

import de.freese.pim.core.mail.InternetAddress;

/**
 * Mail-Utils.
 *
 * @author Thomas Freese
 */
public final class MailUtils {
    public static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_PLAIN = "text/plain";
    public static final String HEADER_CONTENT_ID = "Content-ID";
    public static final String HEADER_MESSAGE_ID = "Message-ID";
    public static final String MULTIPART_SUBTYPE_MIXED = "mixed";
    public static final String MULTIPART_SUBTYPE_RELATED = "related";

    /**
     * @author Thomas Freese
     */
    public abstract static class AbstractTextPart {
        private final String content;

        private final String mimeType;

        AbstractTextPart(final String content, final String mimeType) {
            super();

            this.content = Objects.requireNonNull(content, "content required");
            this.mimeType = Objects.requireNonNull(mimeType, "mimeType required");
        }

        public String getContent() {
            return this.content;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        @Override
        public String toString() {
            return getMimeType() + ": " + getContent();
        }
    }

    /**
     * @author Thomas Freese
     */
    public static final class HTMLTextPart extends AbstractTextPart {
        HTMLTextPart(final String text) {
            super(text, CONTENT_TYPE_HTML);
        }
    }

    /**
     * @author Thomas Freese
     */
    public static final class PlainTextPart extends AbstractTextPart {
        PlainTextPart(final String text) {
            super(text, CONTENT_TYPE_PLAIN);
        }
    }

    /**
     * Liefert alle vorhandenen Attachment-MimeParts einer {@link Message}.<br>
     * Key = Filename<br>
     * Value = {@link MimePart}<br>
     *
     * @return {@link Map}; ist niemals null
     */
    public static Map<String, MimePart> getAttachmentMap(final Part part) throws Exception {
        final Map<String, MimePart> map = new HashMap<>();

        final List<MimePart> attachments = getAttachments(part);

        for (MimePart attachment : attachments) {
            String fileName = Optional.ofNullable(attachment.getFileName()).orElse("Mail");
            fileName = MimeUtility.decodeText(fileName);

            map.put(fileName, attachment);
        }

        return map;
    }

    /**
     * Liefert alle vorhandenen Attachments-MimeParts einer {@link Message}.
     *
     * @return {@link List}; ist niemals null
     */
    public static List<MimePart> getAttachments(final Part part) throws Exception {
        final List<MimePart> bodyParts = new ArrayList<>();

        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
            bodyParts.add((MimePart) part);
        }
        else if (part.isMimeType("multipart/*") || part.isMimeType("MULTIPART/*")) {
            final Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                final Part bp = mp.getBodyPart(i);

                final List<MimePart> list = getAttachments(bp);

                if (list != null && !list.isEmpty()) {
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
     * @return {@link Map}; ist niemals null
     */
    public static Map<String, MimePart> getInlineMap(final Part part) throws Exception {
        final Map<String, MimePart> map = new HashMap<>();

        final List<MimePart> inlines = getInlines(part);

        for (MimePart inline : inlines) {
            final String[] contentIDs = inline.getHeader(HEADER_CONTENT_ID);

            if (contentIDs == null) {
                continue;
            }

            for (String contentID : contentIDs) {
                contentID = contentID.replace("<", "").replace(">", "");

                map.put(contentID, inline);
            }
        }

        return map;
    }

    /**
     * Liefert alle vorhandenen Inline-MimeParts einer {@link Message}.
     *
     * @return {@link List}; ist niemals null
     */
    public static List<MimePart> getInlines(final Part part) throws Exception {
        final List<MimePart> bodyParts = new ArrayList<>();

        if (Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
            bodyParts.add((MimePart) part);
        }
        else if (part.isMimeType("multipart/*") || part.isMimeType("MULTIPART/*")) {
            final Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                final Part bp = mp.getBodyPart(i);

                final List<MimePart> list = getInlines(bp);

                if (list != null && !list.isEmpty()) {
                    bodyParts.addAll(list);
                }
            }
        }

        return bodyParts;
    }

    /**
     * Liefert die Gesamtgröße aller einzelnen Parts.
     */
    public static long getSizeOfAllParts(final Part part) throws Exception {
        long size = part.getSize();

        if (size < 0) {
            size = 0;
        }

        if (part.getContent() instanceof Multipart mp) {
            for (int i = 0; i < mp.getCount(); i++) {
                final Part bp = mp.getBodyPart(i);

                size += getSizeOfAllParts(bp);
            }
        }

        return size;
    }

    /**
     * Liefert die {@link DataSource} für den Text (text/plain, text/html) einer {@link Message}.<br>
     * Dabei wird zuerst nach HTML gesucht, dann nach Plain-Text.
     *
     * @return {@link List}; ist niemals null
     */
    public static DataSource getTextDataSource(final Part part) throws Exception {
        final List<DataSource> dataSources = getTextDataSources(part);

        Optional<DataSource> dataSource = dataSources.stream().filter(ds -> ds.getContentType().toLowerCase().startsWith(CONTENT_TYPE_HTML)).findFirst();

        if (dataSource.isEmpty()) {
            // Kein HTML gefunden, dann nach Plain-Text suchen.
            dataSource = dataSources.stream().filter(ds -> ds.getContentType().toLowerCase().startsWith(CONTENT_TYPE_PLAIN)).findFirst();
        }

        return dataSource.orElse(null);
    }

    /**
     * Liefert alle vorhandenen Text-DataSources (text/plain, text/html) einer {@link Message}.
     *
     * @param part {@link Part}
     *
     * @return {@link List}; ist niemals null
     */
    public static List<DataSource> getTextDataSources(final Part part) throws Exception {
        final List<DataSource> dataSources = new ArrayList<>();

        if (part.isMimeType("text/plain") || part.isMimeType("TEXT/plain") || part.isMimeType("text/html") || part.isMimeType("TEXT/html")) {
            dataSources.add(part.getDataHandler().getDataSource());
        }
        else if (part.isMimeType("multipart/*") || part.isMimeType("MULTIPART/*")) {
            final Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                final Part bp = mp.getBodyPart(i);

                final List<DataSource> ds = getTextDataSources(bp);

                if (ds != null && !ds.isEmpty()) {
                    dataSources.addAll(ds);
                }
            }
        }

        return dataSources;
    }

    /**
     * Liefert alle vorhandenen Text-Parts (text/plain, text/html) einer {@link Message}.
     *
     * @return {@link List}; never null
     */
    public static List<AbstractTextPart> getTextParts(final Part part) throws Exception {
        final List<AbstractTextPart> textParts = new ArrayList<>();

        if (part.isMimeType("text/*") || part.isMimeType("TEXT/*")) {
            if (!(part.getContent() instanceof String text)) {
                return Collections.emptyList();
            }

            if (part.isMimeType("text/plain") || part.isMimeType("TEXT/plain")) {
                textParts.add(new PlainTextPart(text));
            }
            else if (part.isMimeType("text/html") || part.isMimeType("TEXT/html")) {
                textParts.add(new HTMLTextPart(text));
            }
        }
        else if (part.isMimeType("multipart/*")) {
            final Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                final Part bp = mp.getBodyPart(i);

                final List<AbstractTextPart> tp = getTextParts(bp);

                if (!tp.isEmpty()) {
                    textParts.addAll(tp);
                }
            }
        }

        return textParts;
    }

    public static InternetAddress map(final jakarta.mail.internet.InternetAddress address) {
        if (address == null) {
            return null;
        }

        return new InternetAddress(address.getAddress(), address.getPersonal());

    }

    public static InternetAddress[] map(final jakarta.mail.internet.InternetAddress[] addresses) {
        if (addresses == null) {
            return null;
        }

        return Stream.of(addresses).map(MailUtils::map).toArray(InternetAddress[]::new);

        // InternetAddress[] ia = new InternetAddress[addresses.length];
        //
        // for (int i = 0; i < addresses.length; i++) {
        // ia[i] = new InternetAddress(addresses[i].getAddress(), addresses[i].getPersonal());
        // }
        //
        // return ia;
    }

    private MailUtils() {
        super();
    }
}
