// Created: 12.12.2016
package de.freese.pim.core.mail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.io.ClassPathResource;
import de.freese.pim.core.AbstractPimTest;

/**
 * @author Thomas Freese
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMail extends AbstractPimTest
{
    /**
     *
     */
    private static JavaMailSender sender = null;

    /**
     * @return {@link Iterable}
     * @throws Exception Falls was schief geht.
     */
    @Parameters(name = "Account: {1}") // {index}
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

        // Files.newInputStream(path) = Files.newInputStream(path, StandardOpenOption.READ)
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
    *
    */
    @AfterClass
    public static void afterClass()
    {
        // Empty
    }

    /**
     *
     */
    @BeforeClass
    public static void beforeClass()
    {
        sender = new JavaMailSender();
        sender.setEncoding(StandardCharsets.UTF_8.name());
        sender.setHost(MAIL_SMPT_HOST);
        sender.setPort(MAIL_SMPT_PORT);
        sender.setProtocol("smtp");

        // Legitimation für Versand.
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        sender.setJavaMailProperties(properties);
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
     * Erzeugt eine neue Instanz von {@link TestMail}
     */
    public TestMail()
    {
        super();
    }

    /**
     *
     */
    @Before
    public void beforeMethod()
    {
        sender.setUsername(this.from);
        sender.setPassword(this.password);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test000Connect() throws Exception
    {
        sender.testConnection();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test100PlainText() throws Exception
    {
        // @formatter:off
        JavaMailBuilder.create(sender, true)
                .from(this.from)
                .to(this.to)
                .subject("test100PlainText")
                .text("test100PlainText", false)
                .buildAndSend();
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test100PlainTextWithAttachment() throws Exception
    {
        // @formatter:off
        JavaMailBuilder.create(sender, true)
                .from(this.from)
                .to(this.to)
                .subject("test100PlainTextWithAttachment")
                .text("test100PlainTextWithAttachment", false)
                .attachment("text.txt", new ClassPathResource("mail/text.txt").getFile())
                .buildAndSend();
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test200Html() throws Exception
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"de\"><head></head><body>");
        html.append("<h1>test200Html</h1><br>");
        html.append("<font color=\"red\">test200Html</font><br>");
        html.append("</body></html>");

        // @formatter:off
        JavaMailBuilder.create(sender, true)
                .from(this.from)
                .to(this.to)
                .subject("test200Html")
                .text(html.toString(), true)
                .buildAndSend();
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test210HtmlWithAttachment() throws Exception
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"de\"><head></head><body>");
        html.append("<h1>test210HtmlWithAttachment</h1><br>");
        html.append("<font color=\"red\">test210HtmlWithAttachment</font><br>");
        html.append("</body></html>");

        // @formatter:off
        JavaMailBuilder.create(sender, true)
                .from(this.from)
                .to(this.to)
                .subject("test210HtmlWithAttachment")
                .text(html.toString(), true)
                .attachment("text.txt", new ClassPathResource("mail/text.txt").getFile())
                .buildAndSend();
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test220HtmlWithInline() throws Exception
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"de\"><head></head><body>");
        html.append("<h1>test220HtmlWithInline</h1><br>");
        html.append("<img src=\"cid:image1\"><br>");
        html.append("<font color=\"red\">test220HtmlWithInline</font><br>");
        html.append("<img src=\"cid:image1\"><br>");
        html.append("</body></html>");

        // @formatter:off
        JavaMailBuilder.create(sender, true)
                .from(this.from)
                .to(this.to)
                .subject("test220HtmlWithInline")
                .text(html.toString(), true)
                .inline("image1", new ClassPathResource("mail/pim.png").getFile())
                .buildAndSend();
        // @formatter:on
    }
}
