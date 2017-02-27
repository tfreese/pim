/**
 * Created on 24.05.2016
 */
package de.freese.pim.server.addressbook.dao;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.freese.pim.server.addressbook.TestAddressbookConfig;
import de.freese.pim.server.addressbook.service.DefaultAddressBookService;
import de.freese.pim.server.jdbc.JdbcTemplate;
import de.freese.pim.server.jdbc.transaction.ConnectionHolder;
import de.freese.pim.server.jdbc.transaction.TransactionalInvocationHandler;

/**
 * TestCase mit eigenem TX-Management durch Proxy und Lambdas.
 *
 * @author Thomas Freese
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestConnectionHolder extends AbstractDAOTextCase
{
    /**
     * {@link JdbcTemplate} mit eigenem TX-Management.
     *
     * @author Thomas Freese
     */
    private static class ConnectionHolderJdbcTemplate extends JdbcTemplate
    {
        /**
         * Erstellt ein neues {@link ConnectionHolderJdbcTemplate} Object.
         */
        public ConnectionHolderJdbcTemplate()
        {
            super();
        }

        /**
         * @see de.freese.pim.server.jdbc.JdbcTemplate#closeConnection(java.sql.Connection)
         */
        @Override
        protected void closeConnection(final Connection connection)
        {
            try
            {
                if (!ConnectionHolder.isEmpty())
                {
                    // Transaction-Context, nichts tun.
                    // Wird vom TransactionalInvocationHandler erledigt.
                }
                else
                {
                    // Kein Transaction-Context.
                    // connection.setReadOnly(false);

                    connection.close();
                }
            }
            catch (SQLException sex)
            {
                throw convertException(sex);
            }
        }

        /**
         * @see de.freese.pim.server.jdbc.JdbcTemplate#getConnection()
         */
        @SuppressWarnings("resource")
        @Override
        protected Connection getConnection()
        {
            try
            {
                Connection connection = null;

                if (!ConnectionHolder.isEmpty())
                {
                    // Transaction-Context
                    connection = ConnectionHolder.get();
                }
                else
                {
                    // Kein Transaction-Context -> ReadOnly Connection
                    connection = getDataSource().getConnection();

                    // ReadOnly Flag ändern geht nur ausserhalb einer TX.
                    if (!connection.isReadOnly())
                    {
                        connection.setReadOnly(true);
                    }

                    if (!connection.getAutoCommit())
                    {
                        connection.setAutoCommit(true);
                    }
                }

                return connection;
            }
            catch (SQLException sex)
            {
                throw convertException(sex);
            }
        }
    }

    /**
     *
     */
    public static List<DataSource> dataSources = Arrays.asList(new TestAddressbookConfig().dataSource(),
            new TestAddressbookConfig().dataSource());

    // /**
    // *
    // */
    // // @Rule // bei jeder Methode
    // @ClassRule // beforeClass, afterClass
    // public static ExternalResource externalResource = new ExternalResource()
    // {
    // /**
    // * @see org.junit.rules.ExternalResource#after()
    // */
    // @Override
    // protected void after()
    // {
    // System.out.println("after");
    // closeDataSource(dataSource);
    // }
    // };
    /**
     *
     */
    @AfterClass
    public static void afterClass()
    {
        dataSources.forEach(ds -> closeDataSource(ds));
    }

    /**
     * @return {@link Iterable}
     * @throws Exception Falls was schief geht.
     */
    @Parameters(name = "DAO: {0}") // {index}
    public static Iterable<Object[]> connectionPool() throws Exception
    {
        DefaultAddressBookService defaultAddressBookService = new DefaultAddressBookService();
        defaultAddressBookService.setAddressBookDAO(
                new DefaultAddressBookDAO().jdbcTemplate(new ConnectionHolderJdbcTemplate().dataSource(dataSources.get(0))));

        return Arrays.asList(new Object[][]
        {
                {
                        DefaultAddressBookDAO.class.getSimpleName(),
                        (AddressBookDAO) Proxy.newProxyInstance(TestConnectionHolder.class.getClassLoader(), new Class<?>[]
                        {
                                AddressBookDAO.class
                        }, new TransactionalInvocationHandler(dataSources.get(0), defaultAddressBookService))
                },
                {
                        TxLambdaAddressBookDAO.class.getSimpleName(),
                        new TxLambdaAddressBookDAO().jdbcTemplate(new ConnectionHolderJdbcTemplate().dataSource(dataSources.get(1)))
                }
        });
    }

    /**
     *
     */
    @Parameter(value = 1)
    public AddressBookDAO addressBookDAO = null;

    /**
     * Wird nur für die Parameter-Injection benötigt.
     */
    @Parameter(value = 0)
    public String daoName = null;

    /**
     * Erstellt ein neues {@link TestConnectionHolder} Object.
     */
    public TestConnectionHolder()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0100InsertKontakts()
     */
    @Override
    @Test
    public void test0100InsertKontakts() throws Throwable
    {
        doTest0100InsertKontakts(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0110InsertKontaktWithNullVorname()
     */
    @Override
    @Test
    public void test0110InsertKontaktWithNullVorname() throws Throwable
    {
        doTest0110InsertKontaktWithNullVorname(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0120InsertKontaktWithBlankVorname()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0120InsertKontaktWithBlankVorname() throws Throwable
    {
        doTest0120InsertKontaktWithBlankVorname(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0130InsertKontaktExisting()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0130InsertKontaktExisting() throws Throwable
    {
        doTest0130InsertKontaktExisting(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0200UpdateKontakt()
     */
    @Override
    @Test
    public void test0200UpdateKontakt() throws Throwable
    {
        doTest0200UpdateKontakt(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0300InsertAttribut()
     */
    @Override
    @Test
    public void test0300InsertAttribut() throws Throwable
    {
        doTest0300InsertAttribut(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0310InsertInsertAttributWithNullValue()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0310InsertInsertAttributWithNullValue() throws Throwable
    {
        doTest0310InsertInsertAttributWithNullValue(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0320InsertInsertAttributWithBlankValue()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0320InsertInsertAttributWithBlankValue() throws Throwable
    {
        doTest0320InsertInsertAttributWithBlankValue(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0330InsertInsertAttributWithNull()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0330InsertInsertAttributWithNull() throws Throwable
    {
        doTest0330InsertInsertAttributWithNull(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0340InsertInsertAttributWithBlank()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0340InsertInsertAttributWithBlank() throws Throwable
    {
        doTest0340InsertInsertAttributWithBlank(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0350InsertAttributExisting()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0350InsertAttributExisting() throws Throwable
    {
        doTest0350InsertAttributExisting(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0400UpdateAttribut()
     */
    @Override
    @Test
    public void test0400UpdateAttribut() throws Throwable
    {
        doTest0400UpdateAttribut(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0500GetKontaktDetailsAll()
     */
    @Override
    @Test
    public void test0500GetKontaktDetailsAll() throws Throwable
    {
        doTest0500GetKontaktDetailsAll(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0510GetKontaktDetailsWithID()
     */
    @Override
    @Test
    public void test0510GetKontaktDetailsWithID() throws Throwable
    {
        doTest0510GetKontaktDetailsWithID(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0520GetKontaktDetailsWithIDs()
     */
    @Override
    @Test
    public void test0520GetKontaktDetailsWithIDs() throws Throwable
    {
        doTest0520GetKontaktDetailsWithIDs(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0600GetKontakte()
     */
    @Override
    @Test
    public void test0600GetKontakte() throws Throwable
    {
        doTest0600GetKontakte(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0700SearchKontakts()
     */
    @Override
    @Test
    public void test0700SearchKontakts() throws Throwable
    {
        doTest0700SearchKontakts(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0900DeleteAttribut()
     */
    @Override
    @Test
    public void test0900DeleteAttribut() throws Throwable
    {
        doTest0900DeleteAttribut(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test1000DeleteKontakt()
     */
    @Override
    @Test
    public void test1000DeleteKontakt() throws Throwable
    {
        doTest1000DeleteKontakt(this.addressBookDAO);
    }
}
