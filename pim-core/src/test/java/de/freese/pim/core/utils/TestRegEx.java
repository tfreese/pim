package de.freese.pim.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestRegEx {
    private static final String MAIL_REGEX = Utils.MAIL_REGEX;

    /**
     * Falsche Mail-Formate.
     */
    @Test
    void test010ThreadName() {
        // \\d{1,}$

        String[] splits = "a-1".split("[-_]");
        assertEquals(2, splits.length);
        assertEquals("a", splits[0]);
        assertEquals("1", splits[1]);

        splits = "a-1".split("(?<=[a-z])|(?=\\d)");
        assertEquals(3, splits.length);
        assertEquals("a", splits[0]);
        assertEquals("-", splits[1]);
        assertEquals("1", splits[2]);

        // Pattern pattern = Pattern.compile("([a-z]+)|(\\d{1,})");
        final Pattern pattern = Pattern.compile("[a-zA-Z]+"); // Liefert nur Buchstaben

        Matcher matcher = pattern.matcher("a-1");
        assertTrue(matcher.find());
        assertEquals("a", matcher.group());

        matcher = pattern.matcher("a_1");
        assertTrue(matcher.find());
        assertEquals("a", matcher.group());

        matcher = pattern.matcher("a1");
        assertTrue(matcher.find());
        assertEquals("a", matcher.group());
    }

    /**
     * Falsche Mail-Formate.
     */
    @Test
    void test020MailFalse() {
        assertFalse("a".matches(MAIL_REGEX));
        assertFalse("a@".matches(MAIL_REGEX));
        assertFalse("a@b".matches(MAIL_REGEX));
        assertFalse("a@b.".matches(MAIL_REGEX));
        assertFalse("a@b.d".matches(MAIL_REGEX));
    }

    /**
     * Richtige Mail-Formate.
     */
    @Test
    void test020MailTrue() {
        assertTrue("a@b.de".matches(MAIL_REGEX));
        assertTrue("a@b.com".matches(MAIL_REGEX));
        assertTrue("a.c@b.de".matches(MAIL_REGEX));
        assertTrue("a.c@b.com".matches(MAIL_REGEX));
    }

    /**
     * Richtige Mail-Formate.
     */
    @Test
    void test030Misc() {
        assertTrue("20040117.000000".matches(".*\\d{6}.*"));
    }
}
