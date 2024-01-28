// Created: 24.05.2016
package de.freese.pim.core.addressbook;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLIntegrityConstraintViolationException;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.core.TestConfig;
import de.freese.pim.core.dao.AddressBookDao;

/**
 * TestCase für die TX-Steuerung mit Spring.
 *
 * @author Thomas Freese
 */
@SpringBootTest(classes = {TestConfig.class})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional(transactionManager = "transactionManager")
@ActiveProfiles("test")
@DirtiesContext
class TestSpringContextAddressbookDAO extends AbstractDaoTestCase {
    @Resource
    private AddressBookDao addressBookDAO;

    @Override
    @Test
    @Commit
    void test0100InsertKontakts() throws Throwable {
        doTest0100InsertKontakts(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test0110InsertKontaktWithNullVorname() throws Throwable {
        doTest0110InsertKontaktWithNullVorname(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Rollback
    void test0120InsertKontaktWithBlankVorname() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0120InsertKontaktWithBlankVorname(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0130InsertKontaktExisting() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0130InsertKontaktExisting(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Commit
    void test0200UpdateKontakt() throws Throwable {
        doTest0200UpdateKontakt(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test0300InsertAttribut() throws Throwable {
        doTest0300InsertAttribut(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Rollback
    void test0310InsertInsertAttributWithNullValue() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0310InsertInsertAttributWithNullValue(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0320InsertInsertAttributWithBlankValue() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0320InsertInsertAttributWithBlankValue(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0330InsertInsertAttributWithNull() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0330InsertInsertAttributWithNull(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0340InsertInsertAttributWithBlank() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0340InsertInsertAttributWithBlank(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0350InsertAttributExisting() throws Throwable {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0350InsertAttributExisting(this.addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Commit
    void test0400UpdateAttribut() throws Throwable {
        doTest0400UpdateAttribut(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0500GetKontaktDetailsAll() throws Throwable {
        doTest0500GetKontaktDetailsAll(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0510GetKontaktDetailsWithID() throws Throwable {
        doTest0510GetKontaktDetailsWithID(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0520GetKontaktDetailsWithIDs() throws Throwable {
        doTest0520GetKontaktDetailsWithIDs(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0600GetKontakte() throws Throwable {
        doTest0600GetKontakte(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0700SearchKontakts() throws Throwable {
        doTest0700SearchKontakts(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test0900DeleteAttribut() throws Throwable {
        doTest0900DeleteAttribut(this.addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test1000DeleteKontakt() throws Throwable {
        doTest1000DeleteKontakt(this.addressBookDAO);

        assertTrue(true);
    }
}
