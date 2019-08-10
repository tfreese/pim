/**
 *
 */
package de.freese.pim.server.addressbook;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import de.freese.pim.server.addressbook.dao.TestSpringContextAddressbookDAO;
import de.freese.pim.server.addressbook.dao.TestSpringManualTxAddressbookDAO;

/**
 * @author Thomas Freese
 */
@RunWith(Suite.class)
@SuiteClasses(
{
        TestSpringContextAddressbookDAO.class, TestSpringManualTxAddressbookDAO.class
})
@Ignore
public class AllAddressbookTests
{
    /**
     * Erzeugt eine neue Instanz von {@link AllAddressbookTests}
     */
    public AllAddressbookTests()
    {
        super();
    }
    // /**
    // * In der Methode werden alle Testklassen registriert die durch JUnit aufgerufen werden sollen.
    // *
    // * @return {@link Test}
    // */
    // public static Test suite()
    // {
    // TestSuite suite = new TestSuite("de.freese.jdbc");
    //
    // suite.addTest(new JUnit4TestAdapter(TestJdbcDao.class));
    //
    // return suite;
    // }
}
