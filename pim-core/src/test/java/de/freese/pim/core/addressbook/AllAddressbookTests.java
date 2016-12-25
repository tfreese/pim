/**
 *
 */
package de.freese.pim.core.addressbook;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import de.freese.pim.core.addressbook.dao.TestAddressbookDAO;
import de.freese.pim.core.addressbook.dao.TestContextSpringAddressbookDAO;
import de.freese.pim.core.addressbook.dao.TestManualTxSpringAddressbookDAO;

/**
 * @author Thomas Freese
 */
@RunWith(Suite.class)
@SuiteClasses(
{
        TestAddressbookDAO.class, TestContextSpringAddressbookDAO.class, TestManualTxSpringAddressbookDAO.class
})
public class AllAddressbookTests
{
    /**
     *
     */
    private static SimpleNamingContextBuilder namingContext = null;

    /**
    *
    */
    @AfterClass
    public static void afterClass()
    {
        namingContext.clear();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        namingContext = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // namingContext.bind("java:comp/env/jdbc/spring/manualTX", new TestConfig().dataSource());
    }

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
