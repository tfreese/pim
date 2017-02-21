/**
 *
 */
package de.freese.pim.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.freese.pim.common.utils.Utils;

/**
 * Testklasse für RegEx-Ausdrücke.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRegEx
{
    /**
    *
    */
    private static final String MAIL_REGEX = Utils.MAIL_REGEX;

    /**
     * Erzeugt eine neue Instanz von {@link TestRegEx}
     */
    public TestRegEx()
    {
        super();
    }

    /**
     * Falsche Mail-Formate.
     */
    @Test
    public void test010ThreadName()
    {
        // \\d{1,}$

        String[] splits = "a-1".split("[-_]");
        Assert.assertEquals(2, splits.length);
        Assert.assertEquals("a", splits[0]);
        Assert.assertEquals("1", splits[1]);

        splits = "a-1".split("(?<=[a-z])|(?=\\d)");
        Assert.assertEquals(3, splits.length);
        Assert.assertEquals("a", splits[0]);
        Assert.assertEquals("-", splits[1]);
        Assert.assertEquals("1", splits[2]);

        // Pattern pattern = Pattern.compile("([a-z]+)|(\\d{1,})");
        Pattern pattern = Pattern.compile("[a-zA-Z]+"); // Liefert nur Buchstaben

        Matcher matcher = pattern.matcher("a-1");
        matcher.find();
        Assert.assertEquals("a", matcher.group());

        matcher = pattern.matcher("a_1");
        matcher.find();
        Assert.assertEquals("a", matcher.group());

        matcher = pattern.matcher("a1");
        matcher.find();
        Assert.assertEquals("a", matcher.group());
    }

    /**
     * Falsche Mail-Formate.
     */
    @Test
    public void test020MailFalse()
    {
        Assert.assertFalse("a".matches(MAIL_REGEX));
        Assert.assertFalse("a@".matches(MAIL_REGEX));
        Assert.assertFalse("a@b".matches(MAIL_REGEX));
        Assert.assertFalse("a@b.".matches(MAIL_REGEX));
        Assert.assertFalse("a@b.d".matches(MAIL_REGEX));
    }

    /**
     * Richtige Mail-Formate.
     */
    @Test
    public void test020MailTrue()
    {
        Assert.assertTrue("a@b.de".matches(MAIL_REGEX));
        Assert.assertTrue("a@b.com".matches(MAIL_REGEX));
        Assert.assertTrue("a.c@b.de".matches(MAIL_REGEX));
        Assert.assertTrue("a.c@b.com".matches(MAIL_REGEX));
    }
}
