/**
 * Created: 27.12.2016
 */
package de.freese.pim.server.mail;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

/**
 * @author Thomas Freese
 */
@RunWith(JUnitPlatform.class)
@SelectPackages("de.freese.pim.server.mail")
// @SelectClasses(
// {
// TestSendMail.class, TestReceiveMail.class, TestMailDAO.class
// })
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
