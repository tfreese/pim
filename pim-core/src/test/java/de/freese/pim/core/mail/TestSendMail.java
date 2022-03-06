// Created: 12.12.2016
package de.freese.pim.core.mail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import javax.mail.Session;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled
class TestSendMail extends AbstractMailTest
{
    /**
     *
     */
    private static JavaMailSender sender;
    /**
     *
     */
    private static Session session;

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        sender = new JavaMailSender();
        sender.setHost(MAIL_SMPT_HOST);
        sender.setPort(MAIL_SMPT_PORT.getPort());
        sender.setProtocol("smtp");

        // Legitimation für Versand.
        Properties properties = new Properties();
        properties.put("mail.debug", DEBUG.toString());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        sender.setJavaMailProperties(properties);
        session = sender.getSession();
    }

    /**
     *
     */
    @BeforeEach
    void beforeEach()
    {
        sender.setAuthentication(this.from, this.password);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test000Connect() throws Exception
    {
        sender.testConnection();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test100PlainText() throws Exception
    {
        // @formatter:off
        JavaMailBuilder.create(session)
                .from(this.from)
                .to(this.to)
                .subject("test100PlainText")
                .text("test100PlainText", false)
                .buildAndSend(sender)
                ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test100PlainTextWithAttachment() throws Exception
    {
        // Resource resource = new ClassPathResource("mail/text.txt");
        // InputStream inputStream = resource.getInputStream();

        // byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        // InputStream inputStream = new ByteArrayInputStream(bytes);
        // @formatter:off
        JavaMailBuilder.create( session)
                .from(this.from)
                .to(this.to)
                .subject("test100PlainTextWithAttachment")
                .text("test100PlainTextWithAttachment", false)
                .attachment("text.txt", new ClassPathResource("mail/text.txt").getFile())
                .buildAndSend(sender)
                ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test200Html() throws Exception
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"de\"><head></head><body>");
        html.append("<h1>test200Html</h1><br>");
        html.append("<font color=\"red\">test200Html</font><br>");
        html.append("</body></html>");

        // @formatter:off
        JavaMailBuilder.create( session)
                .from(this.from)
                .to(this.to)
                .subject("test200Html")
                .text(html.toString(), true)
                .buildAndSend(sender)
                ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test210HtmlWithAttachment() throws Exception
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"de\"><head></head><body>");
        html.append("<h1>test210HtmlWithAttachment</h1><br>");
        html.append("<font color=\"red\">test210HtmlWithAttachment</font><br>");
        html.append("</body></html>");

        // @formatter:off
        JavaMailBuilder.create(session)
                .from(this.from)
                .to(this.to)
                .subject("test210HtmlWithAttachment")
                .text(html.toString(), true)
                .attachment("text.txt", new ClassPathResource("mail/text.txt").getFile())
                .buildAndSend(sender)
                ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test220HtmlWithInline() throws Exception
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"de\"><head></head><body>");
        html.append("<h1>test220HtmlWithInline</h1><br>");
        html.append("<img src=\"cid:image1\"><br>");
        html.append("<font color=\"red\">test220HtmlWithInline</font><br>");
        html.append("<img src=\"cid:image1\"><br>");
        html.append("</body></html>");

        // @formatter:off
        JavaMailBuilder.create(session)
                .from(this.from)
                .to(this.to)
                .subject("test220HtmlWithInline")
                .text(html.toString(), true)
                .inline("image1", new ClassPathResource("mail/pim.png").getFile())
                .buildAndSend(sender);
        // @formatter:on

        assertTrue(true);
    }
}
