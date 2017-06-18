/**
 *
 */
package de.freese.pim.server;

import de.freese.pim.server.addressbook.AllAddressbookTests;
import de.freese.pim.server.jdbc.TestJdbcTemplate;
import de.freese.pim.server.mail.AllMailTests;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Thomas Freese
 */
//@RunWith(Suite.class)
@SuiteClasses(
    {
        TestJdbcTemplate.class, AllAddressbookTests.class, AllMailTests.class, TestCrypt.class
    })
public class AllPIMTests
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
     * Erzeugt eine neue Instanz von {@link AllPIMTests}
     */
    public AllPIMTests()
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
