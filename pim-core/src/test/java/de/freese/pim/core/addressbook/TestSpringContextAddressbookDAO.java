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
    void test0100InsertKontakts() {
        doTest0100InsertKontakts(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test0110InsertKontaktWithNullVorname() {
        doTest0110InsertKontaktWithNullVorname(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Rollback
    void test0120InsertKontaktWithBlankVorname() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0120InsertKontaktWithBlankVorname(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0130InsertKontaktExisting() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0130InsertKontaktExisting(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Commit
    void test0200UpdateKontakt() {
        doTest0200UpdateKontakt(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test0300InsertAttribut() {
        doTest0300InsertAttribut(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Rollback
    void test0310InsertInsertAttributWithNullValue() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0310InsertInsertAttributWithNullValue(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0320InsertInsertAttributWithBlankValue() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0320InsertInsertAttributWithBlankValue(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0330InsertInsertAttributWithNull() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0330InsertInsertAttributWithNull(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0340InsertInsertAttributWithBlank() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0340InsertInsertAttributWithBlank(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Rollback
    void test0350InsertAttributExisting() {
        final SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> doTest0350InsertAttributExisting(addressBookDAO));

        assertNotNull(exception);
    }

    @Override
    @Test
    @Commit
    void test0400UpdateAttribut() {
        doTest0400UpdateAttribut(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0500GetKontaktDetailsAll() {
        doTest0500GetKontaktDetailsAll(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0510GetKontaktDetailsWithID() {
        doTest0510GetKontaktDetailsWithID(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0520GetKontaktDetailsWithIDs() {
        doTest0520GetKontaktDetailsWithIDs(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0600GetKontakte() {
        doTest0600GetKontakte(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Transactional(readOnly = true)
    void test0700SearchKontakts() {
        doTest0700SearchKontakts(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test0900DeleteAttribut() {
        doTest0900DeleteAttribut(addressBookDAO);

        assertTrue(true);
    }

    @Override
    @Test
    @Commit
    void test1000DeleteKontakt() {
        doTest1000DeleteKontakt(addressBookDAO);

        assertTrue(true);
    }
}
