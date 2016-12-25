// Created: 12.12.2016
package de.freese.pim.core.addressbook.mail;

import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.core.io.ClassPathResource;

import de.freese.pim.core.mail.JavaMailBuilder;
import de.freese.pim.core.mail.JavaMailSender;

/**
 * @author Thomas Freese (EFREEST / AuVi)
 */
// @Ignore
// @RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMail
{
    /**
     *
     */
    private static final String FROM = "thomas.freese@autovision-gmbh.com";

    /**
     *
     */
    private static JavaMailSender sender = null;

    /**
     *
     */
    private static final String TO = "thomas.freese@autovision-gmbh.com";

    /**
     *
     */
    @BeforeClass
    public static void beforeClass()
    {
        sender = new JavaMailSender();
        sender.setEncoding(StandardCharsets.UTF_8.name());
        sender.setHost("mailgate.vw.vwg");
        sender.setPort(25);
        sender.setProtocol("smtp");
    }

    /**
     * Erzeugt eine neue Instanz von {@link TestMail}
     */
    public TestMail()
    {
        super();
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
                .from(FROM)
                .to(TO)
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
                .from(FROM)
                .to(TO)
                .subject("test100PlainTextWithAttachment")
                .text("test100PlainTextWithAttachment", false)
                .attachment("text.txt", new ClassPathResource("text.txt").getFile())
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
                .from(FROM)
                .to(TO)
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
                .from(FROM)
                .to(TO)
                .subject("test210HtmlWithAttachment")
                .text(html.toString(), true)
                .attachment("text.txt", new ClassPathResource("text.txt").getFile())
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
                .from(FROM)
                .to(TO)
                .subject("test220HtmlWithInline")
                .text(html.toString(), true)
                .inline("image1", new ClassPathResource("pim.png").getFile())
                .buildAndSend();
        // @formatter:on
    }
}
