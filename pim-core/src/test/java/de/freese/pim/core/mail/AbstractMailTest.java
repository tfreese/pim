// Created: 30.12.2016
package de.freese.pim.core.mail;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    // @Parameters(name = "Account: {0}") // {index}
    // public static Iterable<Object[]> accounts() throws Exception {
    //     final Path path = TMP_TEST_PATH.resolve("testMail.properties");
    //
    //     Files.createDirectories(path.getParent());
    //
    //     if (Files.notExists(path)) {
    //         LoggerFactory.getLogger(AbstractMailTest.class).error("need property file with from, to and password: {}", path);
    //
    //         return Arrays.asList(new Object[][]{});
    //     }
    //
    //     final Properties properties = new Properties();
    //
    //     try (InputStream is = Files.newInputStream(path)) {
    //         properties.load(is);
    //     }
    //
    //     return Arrays.asList(new Object[][]{{properties.getProperty("from"), properties.getProperty("to"), properties.getProperty("password")}});
    // }

    private static MailProvider getMailProvider() {
        return MailProvider.EINS_UND_EINS;
    }

    protected String getFrom() {
        return null;
    }

    protected String getPassword() {
        return null;
    }

    protected String getTo() {
        return null;
    }
}
