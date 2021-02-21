/**
 *
 */
package de.freese.pim.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.platform.suite.api.SelectPackages;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Thomas Freese
 */
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
    private static SimpleNamingContextBuilder namingContext;

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        namingContext.clear();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        namingContext = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // namingContext.bind("java:comp/env/jdbc/spring/manualTX", new TestConfig().dataSource());
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
