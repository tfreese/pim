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

import de.freese.pim.core.dao.AddressBookDao;
import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.model.addressbook.KontaktAttribut;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDaoTestCase {
    protected static void closeDataSource(final DataSource dataSource) throws Exception {
        if (dataSource instanceof SingleConnectionDataSource ds) {
            ds.destroy();
        }
        else if (dataSource instanceof Closeable ds) {
            ds.close();
        }
    }

    abstract void test0100InsertKontakts();

    abstract void test0110InsertKontaktWithNullVorname();

    abstract void test0120InsertKontaktWithBlankVorname();

    abstract void test0130InsertKontaktExisting();

    abstract void test0200UpdateKontakt();

    abstract void test0300InsertAttribut();

    abstract void test0310InsertInsertAttributWithNullValue();

    abstract void test0320InsertInsertAttributWithBlankValue();

    abstract void test0330InsertInsertAttributWithNull();

    abstract void test0340InsertInsertAttributWithBlank();

    abstract void test0350InsertAttributExisting();

    abstract void test0400UpdateAttribut();

    abstract void test0500GetKontaktDetailsAll();

    abstract void test0510GetKontaktDetailsWithID();

    abstract void test0520GetKontaktDetailsWithIDs();

    abstract void test0600GetKontakte();

    abstract void test0700SearchKontakts();

    abstract void test0900DeleteAttribut();

    abstract void test1000DeleteKontakt();

    protected void doTest0100InsertKontakts(final AddressBookDao addressBookDAO) {
        long id = addressBookDAO.insertKontakt("BNachname", "BVorname");
        assertEquals(1, id);

        id = addressBookDAO.insertKontakt("ANachname", "AVorname");
        assertEquals(2, id);
    }

    protected void doTest0110InsertKontaktWithNullVorname(final AddressBookDao addressBookDAO) {
        final long id = addressBookDAO.insertKontakt("CNachname", null);
        assertEquals(3, id);
    }

    protected void doTest0120InsertKontaktWithBlankVorname(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            addressBookDAO.insertKontakt("DNachname", "");
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }

        fail();
    }

    protected void doTest0130InsertKontaktExisting(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            addressBookDAO.insertKontakt("ANachname", "AVorname");
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }

        fail();
    }

    protected void doTest0200UpdateKontakt(final AddressBookDao addressBookDAO) {
        final int affectedRows = addressBookDAO.updateKontakt(2, "ANachname", "A-Vorname");

        assertEquals(1, affectedRows);
    }

    protected void doTest0300InsertAttribut(final AddressBookDao addressBookDAO) {
        final int affectedRows = addressBookDAO.insertAttribut(2, "STREET", "Street 1");

        assertEquals(1, affectedRows);
    }

    protected void doTest0310InsertInsertAttributWithNullValue(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            final int affectedRows = addressBookDAO.insertAttribut(2, "STREET", null);

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0320InsertInsertAttributWithBlankValue(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            final int affectedRows = addressBookDAO.insertAttribut(2, "STREET", "");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0330InsertInsertAttributWithNull(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            final int affectedRows = addressBookDAO.insertAttribut(2, null, "Street 1");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0340InsertInsertAttributWithBlank(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            final int affectedRows = addressBookDAO.insertAttribut(2, "", "Street 1");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0350InsertAttributExisting(final AddressBookDao addressBookDAO) throws Throwable {
        try {
            // Muss fehlschlagen.
            final int affectedRows = addressBookDAO.insertAttribut(2, "STREET", "Street 1");

            assertEquals(0, affectedRows);
        }
        catch (RuntimeException ex) {
            throw ex.getCause();
        }
    }

    protected void doTest0400UpdateAttribut(final AddressBookDao addressBookDAO) {
        final int affectedRows = addressBookDAO.updateAttribut(2, "STREET", "Street 2");

        assertEquals(1, affectedRows);
    }

    protected void doTest0500GetKontaktDetailsAll(final AddressBookDao addressBookDAO) {
        final List<Kontakt> kontakts = addressBookDAO.getKontaktDetails();

        Assertions.assertNotNull(kontakts);
        assertEquals(3, kontakts.size());

        assertEquals(1, kontakts.getFirst().getID());
        assertEquals("BNachname", kontakts.getFirst().getNachname());
        assertEquals("BVorname", kontakts.getFirst().getVorname());
        List<KontaktAttribut> attribute = kontakts.getFirst().getAttribute();
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
        assertEquals(2, attribute.getFirst().getKontaktID());
        assertEquals("STREET", attribute.getFirst().getAttribut());
        assertEquals("Street 2", attribute.getFirst().getWert());

    }

    protected void doTest0510GetKontaktDetailsWithID(final AddressBookDao addressBookDAO) {
        final List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(2);

        Assertions.assertNotNull(kontakts);
        assertEquals(1, kontakts.size());
        assertEquals(2, kontakts.getFirst().getID());
        assertEquals("ANachname", kontakts.getFirst().getNachname());
        assertEquals("A-Vorname", kontakts.getFirst().getVorname());

        final List<KontaktAttribut> attribute = kontakts.getFirst().getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.getFirst().getKontaktID());
        assertEquals("STREET", attribute.getFirst().getAttribut());
        assertEquals("Street 2", attribute.getFirst().getWert());
    }

    protected void doTest0520GetKontaktDetailsWithIDs(final AddressBookDao addressBookDAO) {
        final List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(2, 3);

        Assertions.assertNotNull(kontakts);
        assertEquals(2, kontakts.size());

        assertEquals(3, kontakts.getFirst().getID());
        assertEquals("CNachname", kontakts.getFirst().getNachname());
        assertNull(kontakts.getFirst().getVorname());
        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        assertEquals(0, attribute.size());

        assertEquals(2, kontakts.get(1).getID());
        assertEquals("ANachname", kontakts.get(1).getNachname());
        assertEquals("A-Vorname", kontakts.get(1).getVorname());
        attribute = kontakts.get(1).getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.getFirst().getKontaktID());
        assertEquals("STREET", attribute.getFirst().getAttribut());
        assertEquals("Street 2", attribute.getFirst().getWert());
    }

    protected void doTest0600GetKontakte(final AddressBookDao addressBookDAO) {
        final List<Kontakt> kontakte = addressBookDAO.getKontakte();

        Assertions.assertNotNull(kontakte);
        assertEquals(3, kontakte.size());

        assertEquals(2, kontakte.getFirst().getID());
        assertEquals("ANachname", kontakte.getFirst().getNachname());
        assertEquals("A-Vorname", kontakte.getFirst().getVorname());

        assertEquals(1, kontakte.get(1).getID());
        assertEquals("BNachname", kontakte.get(1).getNachname());
        assertEquals("BVorname", kontakte.get(1).getVorname());

        assertEquals(3, kontakte.get(2).getID());
        assertEquals("CNachname", kontakte.get(2).getNachname());
        assertNull(kontakte.get(2).getVorname());
    }

    protected void doTest0700SearchKontakts(final AddressBookDao addressBookDAO) {
        final List<Kontakt> kontakte = addressBookDAO.searchKontakte("vor");

        Assertions.assertNotNull(kontakte);
        assertEquals(2, kontakte.size());

        assertEquals(2, kontakte.getFirst().getID());
        assertEquals("ANachname", kontakte.getFirst().getNachname());
        assertEquals("A-Vorname", kontakte.getFirst().getVorname());

        final List<KontaktAttribut> attribute = kontakte.getFirst().getAttribute();
        assertEquals(1, attribute.size());
        assertEquals(2, attribute.getFirst().getKontaktID());
        assertEquals("STREET", attribute.getFirst().getAttribut());
        assertEquals("Street 2", attribute.getFirst().getWert());

        assertEquals(1, kontakte.get(1).getID());
        assertEquals("BNachname", kontakte.get(1).getNachname());
        assertEquals("BVorname", kontakte.get(1).getVorname());
        assertEquals(0, kontakte.get(1).getAttribute().size());
    }

    protected void doTest0900DeleteAttribut(final AddressBookDao addressBookDAO) {
        final int affectedRows = addressBookDAO.deleteAttribut(2, "STREET");

        assertEquals(1, affectedRows);
    }

    protected void doTest1000DeleteKontakt(final AddressBookDao addressBookDAO) {
        final int affectedRows = addressBookDAO.deleteKontakt(3);

        assertEquals(1, affectedRows);
    }
}
