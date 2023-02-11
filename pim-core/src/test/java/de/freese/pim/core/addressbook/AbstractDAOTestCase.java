// Created: 10.07.2016
package de.freese.pim.core.addressbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Closeable;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import de.freese.pim.core.dao.AddressBookDAO;
import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.model.addressbook.KontaktAttribut;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDAOTestCase {
    protected static void closeDataSource(final DataSource dataSource) throws Exception {
        if (dataSource instanceof SingleConnectionDataSource ds) {
            ds.destroy();
        }
        else if (dataSource instanceof Closeable ds) {
            ds.close();
        }
    }

    abstract void test0100InsertKontakts() throws Throwable;

    abstract void test0110InsertKontaktWithNullVorname() throws Throwable;

    abstract void test0120InsertKontaktWithBlankVorname() throws Throwable;

    abstract void test0130InsertKontaktExisting() throws Throwable;

    abstract void test0200UpdateKontakt() throws Throwable;

    abstract void test0300InsertAttribut() throws Throwable;

    abstract void test0310InsertInsertAttributWithNullValue() throws Throwable;

    abstract void test0320InsertInsertAttributWithBlankValue() throws Throwable;

    abstract void test0330InsertInsertAttributWithNull() throws Throwable;

    abstract void test0340InsertInsertAttributWithBlank() throws Throwable;

    abstract void test0350InsertAttributExisting() throws Throwable;

    abstract void test0400UpdateAttribut() throws Throwable;

    abstract void test0500GetKontaktDetailsAll() throws Throwable;

    abstract void test0510GetKontaktDetailsWithID() throws Throwable;

    abstract void test0520GetKontaktDetailsWithIDs() throws Throwable;

    abstract void test0600GetKontakte() throws Throwable;

    abstract void test0700SearchKontakts() throws Throwable;

    abstract void test0900DeleteAttribut() throws Throwable;

    abstract void test1000DeleteKontakt() throws Throwable;

    protected void doTest0100InsertKontakts(final AddressBookDAO addressBookDAO) throws Throwable {
        long id = addressBookDAO.insertKontakt("BNachname", "BVorname");
        assertEquals(1, id);

        id = addressBookDAO.insertKontakt("ANachname", "AVorname");
        assertEquals(2, id);
    }

    protected void doTest0110InsertKontaktWithNullVorname(final AddressBookDAO addressBookDAO) throws Throwable {
        long id = addressBookDAO.insertKontakt("CNachname", null);
        assertEquals(3, id);
    }

    protected void doTest0120InsertKontaktWithBlankVorname(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            addressBookDAO.insertKontakt("DNachname", "");
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }

        fail();
    }

    protected void doTest0130InsertKontaktExisting(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            addressBookDAO.insertKontakt("ANachname", "AVorname");
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }

        fail();
    }

    protected void doTest0200UpdateKontakt(final AddressBookDAO addressBookDAO) throws Throwable {
        int affectedRows = addressBookDAO.updateKontakt(2, "ANachname", "A-Vorname");

        assertEquals(1, affectedRows);
    }

    protected void doTest0300InsertAttribut(final AddressBookDAO addressBookDAO) throws Throwable {
        int affectedRows = addressBookDAO.insertAttribut(2, "STREET", "Street 1");

        assertEquals(1, affectedRows);
    }

    protected void doTest0310InsertInsertAttributWithNullValue(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            int affectedRows = addressBookDAO.insertAttribut(2, "STREET", null);

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0320InsertInsertAttributWithBlankValue(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            int affectedRows = addressBookDAO.insertAttribut(2, "STREET", "");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0330InsertInsertAttributWithNull(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            int affectedRows = addressBookDAO.insertAttribut(2, null, "Street 1");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0340InsertInsertAttributWithBlank(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            int affectedRows = addressBookDAO.insertAttribut(2, "", "Street 1");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0350InsertAttributExisting(final AddressBookDAO addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            int affectedRows = addressBookDAO.insertAttribut(2, "STREET", "Street 1");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0400UpdateAttribut(final AddressBookDAO addressBookDAO) throws Throwable {
        int affectedRows = addressBookDAO.updateAttribut(2, "STREET", "Street 2");

        assertEquals(1, affectedRows);
    }

    protected void doTest0500GetKontaktDetailsAll(final AddressBookDAO addressBookDAO) throws Throwable {
        List<Kontakt> kontakts = addressBookDAO.getKontaktDetails();

        Assertions.assertNotNull(kontakts);
        assertEquals(3, kontakts.size());

        assertEquals(1, kontakts.get(0).getID());
        assertEquals("BNachname", kontakts.get(0).getNachname());
        assertEquals("BVorname", kontakts.get(0).getVorname());
        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        assertEquals(0, attribute.size());

        assertEquals(3, kontakts.get(1).getID());
        assertEquals("CNachname", kontakts.get(1).getNachname());
        assertNull(kontakts.get(1).getVorname());
        attribute = kontakts.get(1).getAttribute();
        assertEquals(0, attribute.size());

        assertEquals(2, kontakts.get(2).getID());
        assertEquals("ANachname", kontakts.get(2).getNachname());
        assertEquals("A-Vorname", kontakts.get(2).getVorname());
        attribute = kontakts.get(2).getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.get(0).getKontaktID());
        assertEquals("STREET", attribute.get(0).getAttribut());
        assertEquals("Street 2", attribute.get(0).getWert());

    }

    protected void doTest0510GetKontaktDetailsWithID(final AddressBookDAO addressBookDAO) throws Throwable {
        List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(2);

        Assertions.assertNotNull(kontakts);
        assertEquals(1, kontakts.size());
        assertEquals(2, kontakts.get(0).getID());
        assertEquals("ANachname", kontakts.get(0).getNachname());
        assertEquals("A-Vorname", kontakts.get(0).getVorname());

        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.get(0).getKontaktID());
        assertEquals("STREET", attribute.get(0).getAttribut());
        assertEquals("Street 2", attribute.get(0).getWert());
    }

    protected void doTest0520GetKontaktDetailsWithIDs(final AddressBookDAO addressBookDAO) throws Throwable {
        List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(2, 3);

        Assertions.assertNotNull(kontakts);
        assertEquals(2, kontakts.size());

        assertEquals(3, kontakts.get(0).getID());
        assertEquals("CNachname", kontakts.get(0).getNachname());
        assertNull(kontakts.get(0).getVorname());
        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        assertEquals(0, attribute.size());

        assertEquals(2, kontakts.get(1).getID());
        assertEquals("ANachname", kontakts.get(1).getNachname());
        assertEquals("A-Vorname", kontakts.get(1).getVorname());
        attribute = kontakts.get(1).getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.get(0).getKontaktID());
        assertEquals("STREET", attribute.get(0).getAttribut());
        assertEquals("Street 2", attribute.get(0).getWert());
    }

    protected void doTest0600GetKontakte(final AddressBookDAO addressBookDAO) throws Throwable {
        List<Kontakt> kontakte = addressBookDAO.getKontakte();

        Assertions.assertNotNull(kontakte);
        assertEquals(3, kontakte.size());

        assertEquals(2, kontakte.get(0).getID());
        assertEquals("ANachname", kontakte.get(0).getNachname());
        assertEquals("A-Vorname", kontakte.get(0).getVorname());

        assertEquals(1, kontakte.get(1).getID());
        assertEquals("BNachname", kontakte.get(1).getNachname());
        assertEquals("BVorname", kontakte.get(1).getVorname());

        assertEquals(3, kontakte.get(2).getID());
        assertEquals("CNachname", kontakte.get(2).getNachname());
        assertNull(kontakte.get(2).getVorname());
    }

    protected void doTest0700SearchKontakts(final AddressBookDAO addressBookDAO) throws Throwable {
        List<Kontakt> kontakte = addressBookDAO.searchKontakte("vor");

        Assertions.assertNotNull(kontakte);
        assertEquals(2, kontakte.size());

        assertEquals(2, kontakte.get(0).getID());
        assertEquals("ANachname", kontakte.get(0).getNachname());
        assertEquals("A-Vorname", kontakte.get(0).getVorname());

        List<KontaktAttribut> attribute = kontakte.get(0).getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.get(0).getKontaktID());
        assertEquals("STREET", attribute.get(0).getAttribut());
        assertEquals("Street 2", attribute.get(0).getWert());

        assertEquals(1, kontakte.get(1).getID());
        assertEquals("BNachname", kontakte.get(1).getNachname());
        assertEquals("BVorname", kontakte.get(1).getVorname());
        assertEquals(0, kontakte.get(1).getAttribute().size());
    }

    protected void doTest0900DeleteAttribut(final AddressBookDAO addressBookDAO) throws Throwable {
        int affectedRows = addressBookDAO.deleteAttribut(2, "STREET");

        assertEquals(1, affectedRows);
    }

    protected void doTest1000DeleteKontakt(final AddressBookDAO addressBookDAO) throws Throwable {
        int affectedRows = addressBookDAO.deleteKontakt(3);

        assertEquals(1, affectedRows);
    }
}
