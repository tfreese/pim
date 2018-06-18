/**
 *
 */
package de.freese.pim.server.addressbook;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import de.freese.pim.server.addressbook.dao.TestSpringContextAddressbookDAO;
import de.freese.pim.server.addressbook.dao.TestSpringManualTxAddressbookDAO;

/**
 * @author Thomas Freese
 */
// @RunWith(Suite.class)
@SuiteClasses(
{
        TestSpringContextAddressbookDAO.class, TestSpringManualTxAddressbookDAO.class
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
