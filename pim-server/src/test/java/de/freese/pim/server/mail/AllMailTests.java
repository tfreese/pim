/**
 * Created: 27.12.2016
 */
package de.freese.pim.server.mail;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Thomas Freese
 */
@RunWith(Suite.class)
@SuiteClasses(
{
        TestSendMail.class, TestReceiveMail.class, TestMailRegEx.class, TestMailDAO.class
})
public class AllMailTests
{
    /**
     * Erstellt ein neues {@link AllMailTests} Object.
     */
    public AllMailTests()
    {
        super();
    }
}