// Created: 24.05.2016
package de.freese.pim.core.addressbook;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import de.freese.pim.core.TestConfig;
import de.freese.pim.core.dao.AddressBookDao;
import de.freese.pim.core.dao.DefaultAddressBookDao;

/**
 * TestCase für die manuelle TX-Steuerung mit Spring.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSpringManualTxAddressbookDAO extends AbstractDaoTestCase {
    private static AddressBookDao addressBookDAO;

    private static DataSource dataSource;

    private static PlatformTransactionManager transactionManager;

    @AfterAll
    static void afterAll() throws Exception {
        closeDataSource(dataSource);
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        final TestConfig config = new TestConfig();

        // dataSource = new JndiDataSourceLookup().getDataSource("jdbc/spring/manualTX"); // Wird in AllTests definiert.
        dataSource = config.dataSource();
        transactionManager = config.transactionManager(dataSource);
        addressBookDAO = config.addressBookDAO(dataSource);
        ((DefaultAddressBookDao) addressBookDAO).setSequenceQuery(config.sequenceQuery());
        ((DefaultAddressBookDao) addressBookDAO).afterPropertiesSet();
    }

    @Override
    @Test
    void test0100InsertKontakts() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest0100InsertKontakts(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0110InsertKontaktWithNullVorname() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest0110InsertKontaktWithNullVorname(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0120InsertKontaktWithBlankVorname() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0120InsertKontaktWithBlankVorname(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0130InsertKontaktExisting() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0130InsertKontaktExisting(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0200UpdateKontakt() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest0200UpdateKontakt(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0300InsertAttribut() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest0300InsertAttribut(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0310InsertInsertAttributWithNullValue() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0310InsertInsertAttributWithNullValue(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0320InsertInsertAttributWithBlankValue() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0320InsertInsertAttributWithBlankValue(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0330InsertInsertAttributWithNull() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0330InsertInsertAttributWithNull(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0340InsertInsertAttributWithBlank() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0340InsertInsertAttributWithBlank(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0350InsertAttributExisting() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            try {
                doTest0350InsertAttributExisting(addressBookDAO);

                // transactionManager.commit(transactionStatus);
            }
            catch (Throwable ex) {
                transactionManager.rollback(transactionStatus);
                throw ex;
            }
        });
    }

    @Override
    @Test
    void test0400UpdateAttribut() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest0400UpdateAttribut(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0500GetKontaktDetailsAll() throws Throwable {
        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try {
            doTest0500GetKontaktDetailsAll(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0510GetKontaktDetailsWithID() throws Throwable {
        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try {
            doTest0510GetKontaktDetailsWithID(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0520GetKontaktDetailsWithIDs() throws Throwable {
        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try {
            doTest0520GetKontaktDetailsWithIDs(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0600GetKontakte() throws Throwable {
        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try {
            doTest0600GetKontakte(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0700SearchKontakts() throws Throwable {
        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        transactionStatus.setRollbackOnly();

        try {
            doTest0700SearchKontakts(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test0900DeleteAttribut() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest0900DeleteAttribut(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }

    @Override
    @Test
    void test1000DeleteKontakt() throws Throwable {
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            doTest1000DeleteKontakt(addressBookDAO);

            transactionManager.commit(transactionStatus);
        }
        catch (Throwable ex) {
            transactionManager.rollback(transactionStatus);
            throw ex;
        }

        assertTrue(true);
    }
}
