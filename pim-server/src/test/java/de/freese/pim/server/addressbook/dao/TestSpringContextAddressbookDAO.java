/**
 * Created on 24.05.2016
 */
package de.freese.pim.server.addressbook.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.annotation.Resource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import de.freese.pim.server.addressbook.TestAddressbookConfig;

/**
 * TestCase für die TX-Steuerung mit Spring.
 *
 * @author Thomas Freese
 */
@SpringBootTest(classes =
{
        TestAddressbookConfig.class
})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional(transactionManager = "transactionManager")
@ActiveProfiles("test")
// @DirtiesContext
class TestSpringContextAddressbookDAO extends AbstractDAOTestCase
{
    /**
     *
     */
    @Resource
    private AddressBookDAO addressBookDAO;

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0100InsertKontakts()
     */
    @Override
    @Test
    @Commit
    void test0100InsertKontakts() throws Throwable
    {
        doTest0100InsertKontakts(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0110InsertKontaktWithNullVorname()
     */
    @Override
    @Test
    @Commit
    void test0110InsertKontaktWithNullVorname() throws Throwable
    {
        doTest0110InsertKontaktWithNullVorname(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0120InsertKontaktWithBlankVorname()
     */
    @Override
    @Test
    @Rollback
    void test0120InsertKontaktWithBlankVorname() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0120InsertKontaktWithBlankVorname(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0130InsertKontaktExisting()
     */
    @Override
    @Test
    @Rollback
    void test0130InsertKontaktExisting() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0130InsertKontaktExisting(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0200UpdateKontakt()
     */
    @Override
    @Test
    @Commit
    void test0200UpdateKontakt() throws Throwable
    {
        doTest0200UpdateKontakt(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0300InsertAttribut()
     */
    @Override
    @Test
    @Commit
    void test0300InsertAttribut() throws Throwable
    {
        doTest0300InsertAttribut(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0310InsertInsertAttributWithNullValue()
     */
    @Override
    @Test
    @Rollback
    void test0310InsertInsertAttributWithNullValue() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0310InsertInsertAttributWithNullValue(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0320InsertInsertAttributWithBlankValue()
     */
    @Override
    @Test
    @Rollback
    void test0320InsertInsertAttributWithBlankValue() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0320InsertInsertAttributWithBlankValue(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0330InsertInsertAttributWithNull()
     */
    @Override
    @Test
    @Rollback
    void test0330InsertInsertAttributWithNull() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0330InsertInsertAttributWithNull(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0340InsertInsertAttributWithBlank()
     */
    @Override
    @Test
    @Rollback
    void test0340InsertInsertAttributWithBlank() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0340InsertInsertAttributWithBlank(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0350InsertAttributExisting()
     */
    @Override
    @Test
    @Rollback
    void test0350InsertAttributExisting() throws Throwable
    {
        SQLIntegrityConstraintViolationException exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            doTest0350InsertAttributExisting(this.addressBookDAO);
        });

        assertNotNull(exception);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0400UpdateAttribut()
     */
    @Override
    @Test
    @Commit
    void test0400UpdateAttribut() throws Throwable
    {
        doTest0400UpdateAttribut(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0500GetKontaktDetailsAll()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    void test0500GetKontaktDetailsAll() throws Throwable
    {
        doTest0500GetKontaktDetailsAll(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0510GetKontaktDetailsWithID()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    void test0510GetKontaktDetailsWithID() throws Throwable
    {
        doTest0510GetKontaktDetailsWithID(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0520GetKontaktDetailsWithIDs()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    void test0520GetKontaktDetailsWithIDs() throws Throwable
    {
        doTest0520GetKontaktDetailsWithIDs(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0600GetKontakte()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    void test0600GetKontakte() throws Throwable
    {
        doTest0600GetKontakte(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0700SearchKontakts()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    void test0700SearchKontakts() throws Throwable
    {
        doTest0700SearchKontakts(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test0900DeleteAttribut()
     */
    @Override
    @Test
    @Commit
    void test0900DeleteAttribut() throws Throwable
    {
        doTest0900DeleteAttribut(this.addressBookDAO);

        assertTrue(true);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTestCase#test1000DeleteKontakt()
     */
    @Override
    @Test
    @Commit
    void test1000DeleteKontakt() throws Throwable
    {
        doTest1000DeleteKontakt(this.addressBookDAO);

        assertTrue(true);
    }
}
