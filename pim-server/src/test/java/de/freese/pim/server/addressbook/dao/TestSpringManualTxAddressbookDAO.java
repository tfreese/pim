/**
 * Created on 24.05.2016
 */
package de.freese.pim.server.addressbook.dao;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import de.freese.pim.server.addressbook.TestAddressbookConfig;

/**
 * TestCase für die manuelle TX-Steuerung mit Spring.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSpringManualTxAddressbookDAO extends AbstractDAOTextCase
{
    /**
     *
     */
    private static AddressBookDAO addressBookDAO = null;

    /**
     *
     */
    private static DataSource dataSource = null;

    /**
     *
     */
    private static PlatformTransactionManager transactionManager = null;

    /**
     *
     */
    @AfterClass
    public static void afterClass()
    {
        closeDataSource(dataSource);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        TestAddressbookConfig config = new TestAddressbookConfig();

        // dataSource = new JndiDataSourceLookup().getDataSource("jdbc/spring/manualTX"); // Wird in AllTests definiert.
        dataSource = config.dataSource();
        transactionManager = config.transactionManager(dataSource);
        addressBookDAO = config.addressBookDAO(dataSource);
    }

    /**
     * Erstellt ein neues {@link TestSpringManualTxAddressbookDAO} Object.
     */
    public TestSpringManualTxAddressbookDAO()
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
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0100InsertKontakts(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0110InsertKontaktWithNullVorname()
     */
    @Override
    @Test
    public void test0110InsertKontaktWithNullVorname() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0110InsertKontaktWithNullVorname(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0120InsertKontaktWithBlankVorname()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0120InsertKontaktWithBlankVorname() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0120InsertKontaktWithBlankVorname(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0130InsertKontaktExisting()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0130InsertKontaktExisting() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0130InsertKontaktExisting(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0200UpdateKontakt()
     */
    @Override
    @Test
    public void test0200UpdateKontakt() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0200UpdateKontakt(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0300InsertAttribut()
     */
    @Override
    @Test
    public void test0300InsertAttribut() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0300InsertAttribut(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0310InsertInsertAttributWithNullValue()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0310InsertInsertAttributWithNullValue() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0310InsertInsertAttributWithNullValue(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0320InsertInsertAttributWithBlankValue()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0320InsertInsertAttributWithBlankValue() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0320InsertInsertAttributWithBlankValue(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0330InsertInsertAttributWithNull()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0330InsertInsertAttributWithNull() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0330InsertInsertAttributWithNull(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0340InsertInsertAttributWithBlank()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0340InsertInsertAttributWithBlank() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0340InsertInsertAttributWithBlank(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0350InsertAttributExisting()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void test0350InsertAttributExisting() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0350InsertAttributExisting(addressBookDAO);

            // transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0400UpdateAttribut()
     */
    @Override
    @Test
    public void test0400UpdateAttribut() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0400UpdateAttribut(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0500GetKontaktDetailsAll()
     */
    @Override
    @Test
    public void test0500GetKontaktDetailsAll() throws Throwable
    {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try
        {
            doTest0500GetKontaktDetailsAll(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0510GetKontaktDetailsWithID()
     */
    @Override
    @Test
    public void test0510GetKontaktDetailsWithID() throws Throwable
    {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try
        {
            doTest0510GetKontaktDetailsWithID(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0520GetKontaktDetailsWithIDs()
     */
    @Override
    @Test
    public void test0520GetKontaktDetailsWithIDs() throws Throwable
    {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try
        {
            doTest0520GetKontaktDetailsWithIDs(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0600GetKontakte()
     */
    @Override
    @Test
    public void test0600GetKontakte() throws Throwable
    {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try
        {
            doTest0600GetKontakte(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0700SearchKontakts()
     */
    @Override
    @Test
    public void test0700SearchKontakts() throws Throwable
    {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try
        {
            doTest0700SearchKontakts(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0900DeleteAttribut()
     */
    @Override
    @Test
    public void test0900DeleteAttribut() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest0900DeleteAttribut(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test1000DeleteKontakt()
     */
    @Override
    @Test
    public void test1000DeleteKontakt() throws Throwable
    {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try
        {
            doTest1000DeleteKontakt(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex)
        {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
    }
}
