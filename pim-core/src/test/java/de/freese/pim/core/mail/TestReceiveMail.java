// Created: 27.12.2016
package de.freese.pim.core.mail;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import jakarta.mail.Authenticator;
import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.SearchTerm;

import org.eclipse.angus.mail.imap.IMAPFolder;
import org.eclipse.angus.mail.util.ASCIIUtility;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import de.freese.pim.core.function.FunctionStripNotLetter;
import de.freese.pim.core.mail.api.JavaMailContent;

/**
 * @author Thomas Freese
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled
class TestReceiveMail extends AbstractMailTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReceiveMail.class);

    private static Session session;

    private static Store store;

    @AfterAll
    static void afterAll() throws Exception {
        if (store != null) {
            store.close();
        }
    }

    @BeforeAll
    static void beforeAll() {
        final Authenticator authenticator = null;

        // Legitimation für Empfang.
        final Properties properties = new Properties();
        properties.put("mail.debug", DEBUG.toString());
        properties.put("mail.imap.auth", "true");
        properties.put("mail.imap.starttls.enable", "true");

        session = Session.getInstance(properties, authenticator);
    }

    // @After
    // public void afterMethod() throws Exception {
    // store.close();
    // }

    @Test
    void test000Connect() throws Exception {
        store = session.getStore("imaps");

        assertNotNull(store);

        store.connect(MAIL_IMAP_HOST, MAIL_IMAP_PORT.getPort(), getFrom(), getPassword());
    }

    @Test
    void test010ListFolder() throws Exception {
        Folder defaultFolder = null;

        try {
            defaultFolder = store.getDefaultFolder();

            assertNotNull(defaultFolder);

            Stream.of(defaultFolder.list("*")).map(Folder::getFullName).forEach(LOGGER::info);
        }
        finally {
            if (defaultFolder != null && defaultFolder.isOpen()) {
                defaultFolder.close(false);
            }
        }
    }

    @Test
    void test020SaveNewMails() throws Exception {
        Folder inboxFolder = null;

        try {
            // defaultFolder = store.getDefaultFolder();
            // inboxFolder = defaultFolder.getFolder("INBOX");
            inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

            // Nur ungelesene Mails holen.
            final SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            final Message[] messages = inboxFolder.search(searchTerm);

            // Nur bestimmte Mail-Attribute vorladen.
            final FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(UIDFolder.FetchProfileItem.UID);
            fp.add(IMAPFolder.FetchProfileItem.HEADERS);
            fp.add(FetchProfile.Item.FLAGS);
            // fp.add(FetchProfile.Item.CONTENT_INFO);
            inboxFolder.fetch(messages, fp);

            assertNotNull(messages);

            for (Message message : messages) {
                final int messageNumber = message.getMessageNumber();
                final String messageID;

                if (inboxFolder instanceof IMAPFolder imapFolder) {
                    messageID = Long.toString(imapFolder.getUID(message));
                }
                else {
                    messageID = message.getHeader("Message-ID")[0];
                }

                assertNotNull(message);

                final Date receivedDate = message.getReceivedDate();
                final String subject = message.getSubject();
                final String from = Optional.ofNullable(message.getFrom()).map(f -> ((InternetAddress) f[0]).getAddress()).orElse(null);

                LOGGER.info("From: {}; Size: {}", Arrays.toString(message.getFrom()), message.getSize());
                LOGGER.info("{}", "%02d | %s | %tc | %s | %s%n".formatted(messageNumber, messageID, receivedDate, subject, from));

                try (OutputStream os = Files.newOutputStream(TMP_TEST_PATH.resolve(messageID + ".eml"))) {
                    // ReceivedDate merken, da nicht im HEADER vorkommt und IMAPMessage read-only ist.
                    final byte[] bytes = ASCIIUtility.getBytes("RECEIVED-DATE: " + receivedDate.toInstant().toString() + "\r\n");
                    os.write(bytes);

                    message.writeTo(os);
                }

                // Nur den Content speichern.
                try (InputStream is = message.getInputStream()) {
                    Files.copy(is, TMP_TEST_PATH.resolve(messageID + ".content"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        finally {
            if (inboxFolder != null && inboxFolder.isOpen()) {
                inboxFolder.close(false);
            }
        }
    }

    @Test
    void test021ReadSavedMails() throws Exception {
        // Files.newDirectoryStream(Paths.get("."), path -> path.toString().endsWith(".msg")).forEach(System.out::println);

        try (Stream<Path> mailFiles = Files.find(TMP_TEST_PATH, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".eml"))) {
            for (Path mail : mailFiles.toList()) {
                try (InputStream is = new BufferedInputStream(Files.newInputStream(mail))) {
                    final MimeMessage message = new MimeMessage(null, is);

                    assertNotNull(message);

                    final int messageNumber = message.getMessageNumber();
                    final String messageID = message.getHeader("Message-ID")[0];
                    final Date receivedDate = Optional.ofNullable(message.getReceivedDate()).orElse(Date.from(Instant.parse(message.getHeader("RECEIVED-DATE")[0])));
                    final String subject = message.getSubject();
                    final String from = Optional.ofNullable(message.getFrom()).map(f -> ((InternetAddress) f[0]).getAddress()).orElse(null);

                    LOGGER.info("{}", "%02d | %s | %tc | %s | %s%n".formatted(messageNumber, messageID, receivedDate, subject, from));
                }
            }
        }
    }

    @Test
    void test022ReadAttachmentsFromSavedMails() throws Exception {
        try (Stream<Path> mailFiles = Files.find(TMP_TEST_PATH, 1, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".eml"))) {
            for (Path mailPath : mailFiles.toList()) {
                try (InputStream is = new BufferedInputStream(Files.newInputStream(mailPath))) {
                    final MimeMessage message = new MimeMessage(null, is);

                    if (message.getContent() instanceof Multipart multiPart) {
                        for (int i = 0; i < multiPart.getCount(); i++) {
                            final MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);

                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                final Path attachment = mailPath.getParent().resolve(mailPath.getFileName().toString() + "_attachment");

                                assertNotNull(attachment);

                                try (InputStream attIS = part.getInputStream()) {
                                    Files.copy(attIS, attachment, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void test023ReadTextFromSavedMails() throws Exception {
        try (Stream<Path> mailFiles = Files.find(TMP_TEST_PATH, 1, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".eml"))) {
            for (Path mail : mailFiles.toList()) {
                try (InputStream is = new BufferedInputStream(Files.newInputStream(mail))) {
                    final MimeMessage message = new MimeMessage(null, is);

                    final MailContent mailContent = new JavaMailContent(message);

                    final String linkRegEx = "^((http[s]?|ftp|file):.*)|(^(www.).*)";
                    final String mailRegEx = "^(.+)@(.+).(.+)$"; // ^[A-Za-z0-9+_.-]+@(.+)$

                    final List<String> values = Stream.of(mailContent.getMessageContent())
                            .map(t -> Jsoup.parse(t).text())
                            //.parallel()
                            .map(t -> t.split(" "))
                            .flatMap(Arrays::stream)
                            .filter(t -> !t.matches(linkRegEx)) // URLs entfernen
                            .filter(t -> !t.matches(mailRegEx)) // Mails entfernen
                            .map(FunctionStripNotLetter.INSTANCE)
                            .filter(s -> !s.isBlank())
                            //.peek(t -> System.out.println(Thread.currentThread().getName()))
                            .distinct()
                            .toList();

                    assertNotNull(values);
                    LOGGER.info(values.toString());

                    // final List<String> values = textParts.parallelStream()
                    //         .map(AbstractTextPart::getText)
                    //         .map(t -> Jsoup.parse(t).text())
                    //         .map(t -> t.split(" "))
                    //         .flatMap(Arrays::stream)
                    //         .map(StringUtils::lowerCase)
                    //         .filter(t -> !t.matches(linkRegEx)) // URLs entfernen
                    //         .filter(t -> !t.matches(mailRegEx)) // Mails entfernen
                    //         //.filter(t -> !StringUtils.startsWith(t, "http:"))
                    //         //.filter(t -> !StringUtils.startsWith(t, "https:"))
                    //         //.filter(t -> !StringUtils.startsWith(t, "ftp:"))
                    //         //.filter(t -> !StringUtils.startsWith(t, "file:"))
                    //         //.filter(t -> StringUtils.containsNone(t, "@"))
                    //         .map(FunctionStripNotLetter.INSTANCE)
                    //         .map(FunctionStripSameChar.INSTANCE)
                    //         .filter(t -> t.length() > 2) // Nur Texte mit mehr als 2 Zeichen
                    //         .distinct()
                    //         .collect(Collectors.toList());
                    //
                    // Locale locale = FunctionStripStopWords.guessLocale(values);
                    // Function<String, String> functionStemmer = FunctionStemmer.get(locale);
                    //
                    // // parallelStream wegen Stemmer nicht möglich.
                    // values.stream()
                    //         .map(t -> Locale.GERMAN.equals(locale) ? FunctionNormalizeGerman.INSTANCE.apply(t) : t)
                    //         .map(FunctionStripStopWords.INSTANCE)
                    //         .map(functionStemmer)
                    //         .filter(t -> t.length() > 2)
                    //         .distinct()
                    //         .sorted()
                    //         .forEach(LOGGER::info);
                }
            }
        }
    }
}
