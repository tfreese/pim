/**
 *
 */
package de.freese.pim.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import de.freese.pim.core.addressbook.AllAddressbookTests;
import de.freese.pim.core.persistence.TestJdbcTemplate;
import de.freese.pim.core.utils.io.TestMonitorIO;

/**
 * @author Thomas Freese
 */
@RunWith(Suite.class)
@SuiteClasses(
{
        // AllMailTests.class
        AllAddressbookTests.class, TestJdbcTemplate.class, TestMonitorIO.class
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
