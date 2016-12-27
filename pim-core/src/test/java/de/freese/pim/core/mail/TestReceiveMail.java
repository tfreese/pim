/**
 * Created: 27.12.2016
 */

package de.freese.pim.core.mail;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
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
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import com.sun.mail.imap.IMAPFolder;
import de.freese.pim.core.AbstractPimTest;

/**
 * https://javamail.java.net/nonav/docs/api/com/sun/mail/imap/package-summary.html
 *
 * @author Thomas Freese
 */
// @Ignore
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestReceiveMail extends AbstractPimTest
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
     * @return {@link Iterable}
     * @throws Exception Falls was schief geht.
     */
    @Parameters(name = "Account: {0}") // {index}
    public static Iterable<Object[]> accounts() throws Exception
    {
        Path path = TMP_TEST_PATH.resolve("testMail.properties");

        Files.createDirectories(path.getParent());

        if (Files.notExists(path))
        {
            System.err.println("need property file with from, to and password: " + path);

            return Arrays.asList(new Object[][] {});
        }

        Properties properties = new Properties();

        try (InputStream is = Files.newInputStream(path))
        {
            properties.load(is);
        }

        return Arrays.asList(new Object[][]
        {
                {
                        properties.getProperty("from"), properties.getProperty("password")
                }
        });
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterClass
    public static void afterClass() throws Exception
    {
        store.close();
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
    public void test020FetchNewMails() throws Exception
    {
        Folder inboxFolder = null;

        try
        {
            // defaultFolder = store.getDefaultFolder();
            // inboxFolder = defaultFolder.getFolder("INBOX");
            inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

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
                String from = null;

                if (message.getFrom() != null)
                {
                    from = ((InternetAddress) message.getFrom()[0]).getAddress();
                }

                System.out.printf("%02d | %s | %tc | %s | %s%n", messageNumber, messageID, receivedDate, subject, from);

                Files.copy(message.getInputStream(), TMP_TEST_PATH.resolve(messageID + ".msg"));
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
}
