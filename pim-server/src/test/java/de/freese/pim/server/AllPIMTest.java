/**
 *
 */
package de.freese.pim.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Thomas Freese
 */
@RunWith(JUnitPlatform.class)
@SelectPackages("de.freese.pim.server")
// @SelectClasses(
// {
// AllAddressbookTest.class, AllMailTests.class, TestCrypt.class
// })
@SuppressWarnings("deprecation")
public class AllPIMTest
{

    /**
     *
     */
    private static SimpleNamingContextBuilder namingContext = null;

    /**
     *
     */
    @AfterClass
    static void afterClass()
    {
        namingContext.clear();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    static void beforeClass() throws Exception
    {
        namingContext = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // namingContext.bind("java:comp/env/jdbc/spring/manualTX", new TestConfig().dataSource());
    }

    /**
     * Erzeugt eine neue Instanz von {@link AllPIMTest}
     */
    public AllPIMTest()
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
