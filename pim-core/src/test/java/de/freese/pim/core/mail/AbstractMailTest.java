// Created: 30.12.2016
package de.freese.pim.core.mail;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * <a href="https://javamail.java.net/nonav/docs/api/com/sun/mail/imap/package-summary.html">javamail.java.net</a>
 *
 * @author Thomas Freese
 */
public abstract class AbstractMailTest {
    public static final String MAIL_IMAP_HOST = getMailProvider().getImapHost();
    public static final MailPort MAIL_IMAP_PORT = getMailProvider().getImapPort();
    public static final String MAIL_SMPT_HOST = getMailProvider().getSmtpHost();
    public static final MailPort MAIL_SMPT_PORT = getMailProvider().getSmtpPort();
    public static final Path TMP_TEST_PATH = Paths.get(System.getProperty("user.dir"), "test");
    protected static final Boolean DEBUG = Boolean.FALSE;

    @Parameters(name = "Account: {0}") // {index}
    public static Iterable<Object[]> accounts() throws Exception {
        final Path path = TMP_TEST_PATH.resolve("testMail.properties");

        Files.createDirectories(path.getParent());

        if (Files.notExists(path)) {
            System.err.println("need property file with from, to and password: " + path);

            return Arrays.asList(new Object[][]{});
        }

        final Properties properties = new Properties();

        try (InputStream is = Files.newInputStream(path)) {
            properties.load(is);
        }

        return Arrays.asList(new Object[][]{{properties.getProperty("from"), properties.getProperty("to"), properties.getProperty("password")}});
    }

    private static MailProvider getMailProvider() {
        return MailProvider.EINS_UND_EINS;
    }

    @Parameter(value = 0)
    private String from;

    @Parameter(value = 2)
    private String password;

    @Parameter(value = 1)
    private String to;

    protected String getFrom() {
        return from;
    }

    protected String getPassword() {
        return password;
    }

    protected String getTo() {
        return to;
    }
}
