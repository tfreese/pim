/**
 * Created: 30.12.2016
 */
package de.freese.pim.core.mail;

import de.freese.pim.core.AbstractPimTest;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * https://javamail.java.net/nonav/docs/api/com/sun/mail/imap/package-summary.html
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailTest extends AbstractPimTest
{
    /**
     *
     */
    protected static final Boolean DEBUG = Boolean.FALSE;

    /**
     *
     */
    public static final String MAIL_IMAP_HOST = getMailProvider().getImapHost();

    /**
     *
     */
    public static final MailPort MAIL_IMAP_PORT = getMailProvider().getImapPort();

    /**
     *
     */
    public static final String MAIL_SMPT_HOST = getMailProvider().getSmtpHost();

    /**
     *
     */
    public static final MailPort MAIL_SMPT_PORT = getMailProvider().getSmtpPort();

    /**
     * @return {@link Iterable}
     *
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

            return Arrays.asList(new Object[][]
            {
            });
        }

        Properties properties = new Properties();

        try (InputStream is = Files.newInputStream(path))
        {
            properties.load(is);
        }

        return Arrays.asList(new Object[][]
        {
            {
                properties.getProperty("from"), properties.getProperty("to"), properties.getProperty("password")
            }
        });
    }

    /**
     * @return {@link MailProvider}
     */
    private static MailProvider getMailProvider()
    {
        return MailProvider.EinsUndEins;
    }

    /**
     *
     */
    @Parameter(value = 0)
    public String from = null;

    /**
     *
     */
    @Parameter(value = 2)
    public String password;

    /**
     *
     */
    @Parameter(value = 1)
    public String to;

    /**
     * Erstellt ein neues {@link AbstractMailTest} Object.
     */
    public AbstractMailTest()
    {
        super();
    }
}
