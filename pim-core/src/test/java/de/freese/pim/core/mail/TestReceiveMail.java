/**
 * Created: 27.12.2016
 */

package de.freese.pim.core.mail;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.ASCIIUtility;
import de.freese.pim.core.mail.function.FunctionStripNotLetter;
import de.freese.pim.core.mail.utils.MailUtils;
import de.freese.pim.core.mail.utils.MailUtils.AbstractTextPart;

/**
 * @author Thomas Freese
 */
// @Ignore
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestReceiveMail extends AbstractMailTest
{
    /**
     *
     */
    private static Session session = null;

    /**
     *
     */
    private static Store store = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterClass
    public static void afterClass() throws Exception
    {
        if (store != null)
        {
            store.close();
        }
    }

    /**
     *
     */
    @BeforeClass
    public static void beforeClass()
    {
        Authenticator authenticator = null;

        // Legitimation für Empfang.
        Properties properties = new Properties();
        properties.put("mail.imap.auth", "true");
        properties.put("mail.imap.starttls.enable", "true");

        session = Session.getInstance(properties, authenticator);
    }

    /**
    *
    */
    @Parameter(value = 1)
    public String password;
    /**
    *
    */
    @Parameter(value = 0)
    public String username = null;

    /**
     * Erstellt ein neues {@link TestReceiveMail} Object.
     */
    public TestReceiveMail()
    {
        super();
    }

    // /**
    // * @throws Exception Falls was schief geht.
    // */
    // @After
    // public void afterMethod() throws Exception
    // {
    // store.close();
    // }

    // /**
    // * @throws Exception Falls was schief geht.
    // */
    // @Before
    // public void beforeMethod() throws Exception
    // {
    // store = session.getStore("imaps");
    // store.connect(MAIL_IMAP_HOST, MAIL_IMAP_PORT, this.username, this.password);
    // }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test000Connect() throws Exception
    {
        store = session.getStore("imaps");
        store.connect(MAIL_IMAP_HOST, MAIL_IMAP_PORT, this.username, this.password);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010ListFolder() throws Exception
    {
        Folder defaultFolder = null;

        try
        {
            defaultFolder = store.getDefaultFolder();

            Stream.of(defaultFolder.list("*")).map(Folder::getFullName).forEach(System.out::println);
        }
        finally
        {
            if ((defaultFolder != null) && defaultFolder.isOpen())
            {
                defaultFolder.close(false);
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020SaveNewMails() throws Exception
    {
        System.out.println();
        Folder inboxFolder = null;

        try
        {
            // defaultFolder = store.getDefaultFolder();
            // inboxFolder = defaultFolder.getFolder("INBOX");
            inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

            // Nur ungelesene Mails holen.
            SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = inboxFolder.search(searchTerm);

            // Nur bestimmte Mail-Attribute vorladen.
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(UIDFolder.FetchProfileItem.UID);
            fp.add(IMAPFolder.FetchProfileItem.HEADERS);
            inboxFolder.fetch(messages, fp);

            Assert.assertNotNull(messages);
            Assert.assertTrue(messages.length > 0);

            for (Message message : messages)
            {
                int messageNumber = message.getMessageNumber();
                String messageID = message.getHeader("Message-ID")[0];
                Date receivedDate = message.getReceivedDate();
                String subject = message.getSubject();
                String from = Optional.ofNullable(message.getFrom()).map(f -> ((InternetAddress) f[0]).getAddress()).orElse(null);

                System.out.printf("From: %s%n", Arrays.toString(message.getFrom()));
                System.out.printf("%02d | %s | %tc | %s | %s%n", messageNumber, messageID, receivedDate, subject, from);

                try (OutputStream os = Files.newOutputStream(TMP_TEST_PATH.resolve(messageID + ".msg")))
                {
                    // ReceivedDate merken, da nicht im HEADER vorkommt und IMAPMessage read-only ist.
                    byte[] bytes = ASCIIUtility.getBytes("RECEIVED-DATE: " + receivedDate.toInstant().toString() + "\r\n");
                    os.write(bytes);

                    message.writeTo(os);
                }
            }
        }
        finally
        {
            if ((inboxFolder != null) && inboxFolder.isOpen())
            {
                inboxFolder.close(false);
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test021ReadSavedMails() throws Exception
    {
        System.out.println();
        // Files.newDirectoryStream(Paths.get("."), path -> path.toString().endsWith(".msg")).forEach(System.out::println);

        try (Stream<Path> mailFiles = Files.find(TMP_TEST_PATH, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".msg")))
        {
            for (Path mail : mailFiles.collect(Collectors.toList()))
            {
                try (InputStream is = new BufferedInputStream(Files.newInputStream(mail)))
                {
                    MimeMessage message = new MimeMessage(null, is);

                    int messageNumber = message.getMessageNumber();
                    String messageID = message.getHeader("Message-ID")[0];
                    Date receivedDate = Optional.ofNullable(message.getReceivedDate()).orElse(Date.from(Instant.parse(message.getHeader("RECEIVED-DATE")[0])));
                    String subject = message.getSubject();
                    String from = Optional.ofNullable(message.getFrom()).map(f -> ((InternetAddress) f[0]).getAddress()).orElse(null);

                    System.out.printf("%02d | %s | %tc | %s | %s%n", messageNumber, messageID, receivedDate, subject, from);
                }
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test022ReadTextFromSavedMails() throws Exception
    {
        System.out.println();

        try (Stream<Path> mailFiles = Files.find(TMP_TEST_PATH, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".msg")))
        {
            for (Path mail : mailFiles.collect(Collectors.toList()))
            {
                try (InputStream is = new BufferedInputStream(Files.newInputStream(mail)))
                {
                    MimeMessage message = new MimeMessage(null, is);

                    List<AbstractTextPart> textParts = MailUtils.getTextParts(message);

                    String linkRegEx = "^((http[s]?|ftp|file):.*)|(^(www.).*)";
                    String mailRegEx = "^(.+)@(.+).(.+)$"; // ^[A-Za-z0-9+_.-]+@(.+)$

                    // @formatter:off
                    List<String> values= textParts.stream()
                            .map(AbstractTextPart::getText)
                            .map(t -> Jsoup.parse(t).text())
                            //.parallel()
                            .map(t -> t.split(" "))
                            .flatMap(Arrays::stream)
                            .filter(t -> !t.matches(linkRegEx)) // URLs entfernen
                            .filter(t -> !t.matches(mailRegEx)) // Mails entfernen
                            .map(FunctionStripNotLetter.INSTANCE)
                            .filter(StringUtils::isNotBlank)
                            //.peek(t -> System.out.println(Thread.currentThread().getName()))
                            .distinct()
                            .collect(Collectors.toList());
                    // @formatter:on

                    Assert.assertNotNull(values);
                    System.out.println(values);

                    // @formatter:off
//                    List<String> values= textParts.parallelStream()
//                        .map(AbstractTextPart::getText)
//                        .map(t -> Jsoup.parse(t).text())
//                        .map(t -> t.split(" "))
//                        .flatMap(Arrays::stream)
//                        .map(StringUtils::lowerCase)
//                        .filter(t -> !t.matches(linkRegEx)) // URLs entfernen
//                        .filter(t -> !t.matches(mailRegEx)) // Mails entfernen
//                        //.filter(t -> !StringUtils.startsWith(t, "http:"))
//                        //.filter(t -> !StringUtils.startsWith(t, "https:"))
//                        //.filter(t -> !StringUtils.startsWith(t, "ftp:"))
//                        //.filter(t -> !StringUtils.startsWith(t, "file:"))
//                        //.filter(t -> StringUtils.containsNone(t, "@"))
//                        .map(FunctionStripNotLetter.INSTANCE)
//                        .map(FunctionStripSameChar.INSTANCE)
//                        .filter(t -> t.length() > 2) // Nur Texte mit mehr als 2 Zeichen
//                        .distinct()
//                        .collect(Collectors.toList());
                        // @formatter:on

                    // Locale locale = FunctionStripStopWords.guessLocale(values);
                    // Function<String, String> functionStemmer = FunctionStemmer.get(locale);
                    //
//                        // @formatter:off
//                        // parallelStream wegen Stemmer nicht möglich.
//                        values.stream()
//                            .map(t -> Locale.GERMAN.equals(locale) ? FunctionNormalizeGerman.INSTANCE.apply(t) : t)
//                            .map(FunctionStripStopWords.INSTANCE)
//                            .map(functionStemmer)
//                            .filter(t -> t.length() > 2)
//                            .distinct()
//                            .sorted()
//                            .forEach(System.out::println);
//                        // @formatter:on
                }
            }
        }
    }
}
