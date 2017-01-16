/**
 *
 */
package de.freese.pim.core.mail;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.freese.pim.core.mail.utils.MailUtils;

/**
 * Testklasse für RegEx-ausdruck für das MailFormat.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMailRegEx
{
    /**
     *
     */
    private static final String MAIL_REGEX = MailUtils.MAIL_REGEX;

    /**
     * Erzeugt eine neue Instanz von {@link TestMailRegEx}
     */
    public TestMailRegEx()
    {
        super();
    }

    /**
     * Falsche Mail-Formate.
     */
    @Test
    public void test010False()
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
    public void test020True()
    {
        Assert.assertTrue("a@b.de".matches(MAIL_REGEX));
        Assert.assertTrue("a@b.com".matches(MAIL_REGEX));
        Assert.assertTrue("a.c@b.de".matches(MAIL_REGEX));
        Assert.assertTrue("a.c@b.com".matches(MAIL_REGEX));
    }
}
