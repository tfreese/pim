package de.freese.pim.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Testklasse für RegEx-Ausdrücke.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestRegEx
{
    private static final String MAIL_REGEX = Utils.MAIL_REGEX;

    /**
     * Falsche Mail-Formate.
     */
    @Test
    void test010ThreadName()
    {
        // \\d{1,}$

        String[] splits = "a-1".split("[-_]");
        Assertions.assertEquals(2, splits.length);
        Assertions.assertEquals("a", splits[0]);
        Assertions.assertEquals("1", splits[1]);

        splits = "a-1".split("(?<=[a-z])|(?=\\d)");
        Assertions.assertEquals(3, splits.length);
        Assertions.assertEquals("a", splits[0]);
        Assertions.assertEquals("-", splits[1]);
        Assertions.assertEquals("1", splits[2]);

        // Pattern pattern = Pattern.compile("([a-z]+)|(\\d{1,})");
        Pattern pattern = Pattern.compile("[a-zA-Z]+"); // Liefert nur Buchstaben

        Matcher matcher = pattern.matcher("a-1");
        matcher.find();
        Assertions.assertEquals("a", matcher.group());

        matcher = pattern.matcher("a_1");
        matcher.find();
        Assertions.assertEquals("a", matcher.group());

        matcher = pattern.matcher("a1");
        matcher.find();
        Assertions.assertEquals("a", matcher.group());
    }

    /**
     * Falsche Mail-Formate.
     */
    @Test
    void test020MailFalse()
    {
        Assertions.assertFalse("a".matches(MAIL_REGEX));
        Assertions.assertFalse("a@".matches(MAIL_REGEX));
        Assertions.assertFalse("a@b".matches(MAIL_REGEX));
        Assertions.assertFalse("a@b.".matches(MAIL_REGEX));
        Assertions.assertFalse("a@b.d".matches(MAIL_REGEX));
    }

    /**
     * Richtige Mail-Formate.
     */
    @Test
    void test020MailTrue()
    {
        Assertions.assertTrue("a@b.de".matches(MAIL_REGEX));
        Assertions.assertTrue("a@b.com".matches(MAIL_REGEX));
        Assertions.assertTrue("a.c@b.de".matches(MAIL_REGEX));
        Assertions.assertTrue("a.c@b.com".matches(MAIL_REGEX));
    }

    /**
     * Richtige Mail-Formate.
     */
    @Test
    void test030Misc()
    {
        Assertions.assertTrue("20040117.000000".matches(".*\\d{6}.*"));
    }
}
