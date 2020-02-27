/**
 *
 */
package de.freese.pim.server.addressbook;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

/**
 * @author Thomas Freese
 */
@RunWith(JUnitPlatform.class)
@SelectPackages("de.freese.pim.server.addressbook.dao")
// @SelectClasses(
// {
// TestSpringContextAddressbookDAO.class, TestSpringManualTxAddressbookDAO.class
// })
public class AllAddressbookTest
{
    /**
     * Erzeugt eine neue Instanz von {@link AllAddressbookTest}
     */
    public AllAddressbookTest()
    {
        super();
    }
}
