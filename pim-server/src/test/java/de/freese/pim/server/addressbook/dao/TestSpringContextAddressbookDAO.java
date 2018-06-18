/**
 * Created on 24.05.2016
 */
package de.freese.pim.server.addressbook.dao;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.server.addressbook.TestAddressbookConfig;

/**
 * TestCase für die TX-Steuerung mit Spring.
 *
 * @author Thomas Freese
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes =
{
        TestAddressbookConfig.class
})
@Transactional(transactionManager = "transactionManager")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
// @DirtiesContext
public class TestSpringContextAddressbookDAO extends AbstractDAOTextCase
{
    /**
     *
     */
    @Resource
    private AddressBookDAO addressBookDAO = null;

    /**
     * Erstellt ein neues {@link TestSpringContextAddressbookDAO} Object.
     */
    public TestSpringContextAddressbookDAO()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0100InsertKontakts()
     */
    @Override
    @Test
    @Commit
    public void test0100InsertKontakts() throws Throwable
    {
        doTest0100InsertKontakts(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0110InsertKontaktWithNullVorname()
     */
    @Override
    @Test
    @Commit
    public void test0110InsertKontaktWithNullVorname() throws Throwable
    {
        doTest0110InsertKontaktWithNullVorname(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0120InsertKontaktWithBlankVorname()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0120InsertKontaktWithBlankVorname() throws Throwable
    {
        doTest0120InsertKontaktWithBlankVorname(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0130InsertKontaktExisting()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0130InsertKontaktExisting() throws Throwable
    {
        doTest0130InsertKontaktExisting(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0200UpdateKontakt()
     */
    @Override
    @Test
    @Commit
    public void test0200UpdateKontakt() throws Throwable
    {
        doTest0200UpdateKontakt(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0300InsertAttribut()
     */
    @Override
    @Test
    @Commit
    public void test0300InsertAttribut() throws Throwable
    {
        doTest0300InsertAttribut(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0310InsertInsertAttributWithNullValue()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0310InsertInsertAttributWithNullValue() throws Throwable
    {
        doTest0310InsertInsertAttributWithNullValue(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0320InsertInsertAttributWithBlankValue()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0320InsertInsertAttributWithBlankValue() throws Throwable
    {
        doTest0320InsertInsertAttributWithBlankValue(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0330InsertInsertAttributWithNull()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0330InsertInsertAttributWithNull() throws Throwable
    {
        doTest0330InsertInsertAttributWithNull(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0340InsertInsertAttributWithBlank()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0340InsertInsertAttributWithBlank() throws Throwable
    {
        doTest0340InsertInsertAttributWithBlank(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0350InsertAttributExisting()
     */
    @Override
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    @Rollback
    public void test0350InsertAttributExisting() throws Throwable
    {
        doTest0350InsertAttributExisting(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0400UpdateAttribut()
     */
    @Override
    @Test
    @Commit
    public void test0400UpdateAttribut() throws Throwable
    {
        doTest0400UpdateAttribut(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0500GetKontaktDetailsAll()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    public void test0500GetKontaktDetailsAll() throws Throwable
    {
        doTest0500GetKontaktDetailsAll(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0510GetKontaktDetailsWithID()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    public void test0510GetKontaktDetailsWithID() throws Throwable
    {
        doTest0510GetKontaktDetailsWithID(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0520GetKontaktDetailsWithIDs()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    public void test0520GetKontaktDetailsWithIDs() throws Throwable
    {
        doTest0520GetKontaktDetailsWithIDs(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0600GetKontakte()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    public void test0600GetKontakte() throws Throwable
    {
        doTest0600GetKontakte(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0700SearchKontakts()
     */
    @Override
    @Test
    @Transactional(readOnly = true)
    public void test0700SearchKontakts() throws Throwable
    {
        doTest0700SearchKontakts(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test0900DeleteAttribut()
     */
    @Override
    @Test
    @Commit
    public void test0900DeleteAttribut() throws Throwable
    {
        doTest0900DeleteAttribut(this.addressBookDAO);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AbstractDAOTextCase#test1000DeleteKontakt()
     */
    @Override
    @Test
    @Commit
    public void test1000DeleteKontakt() throws Throwable
    {
        doTest1000DeleteKontakt(this.addressBookDAO);
    }
}
