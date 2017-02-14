/**
 * Created: 10.07.2016
 */

package de.freese.pim.server.addressbook.dao;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import de.freese.pim.server.addressbook.dao.AddressBookDAO;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.addressbook.model.KontaktAttribut;

/**
 * Basis-TestCase für die DAO-Implementierungen.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDAOTextCase
{
    /**
     * @param dataSource {@link DataSource}
     */
    protected static void closeDataSource(final DataSource dataSource)
    {
        ((SingleConnectionDataSource) dataSource).destroy();
    }

    /**
     * Erstellt ein neues {@link AbstractDAOTextCase} Object.
     */
    public AbstractDAOTextCase()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0100InsertKontakts() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0110InsertKontaktWithNullVorname() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0120InsertKontaktWithBlankVorname() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0130InsertKontaktExisting() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0200UpdateKontakt() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0300InsertAttribut() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0310InsertInsertAttributWithNullValue() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0320InsertInsertAttributWithBlankValue() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0330InsertInsertAttributWithNull() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0340InsertInsertAttributWithBlank() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0350InsertAttributExisting() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0400UpdateAttribut() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0500GetKontaktDetailsAll() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0510GetKontaktDetailsWithID() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0520GetKontaktDetailsWithIDs() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0600GetKontakte() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0700SearchKontakts() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test0900DeleteAttribut() throws Exception;

    /**
     * @throws Exception Falls was schief geht.
     */
    public abstract void test1000DeleteKontakt() throws Exception;

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0100InsertKontakts(final AddressBookDAO addressBookDAO) throws Exception
    {
        long id = addressBookDAO.insertKontakt("BNachname", "BVorname");
        Assert.assertEquals(1, id);

        id = addressBookDAO.insertKontakt("ANachname", "AVorname");
        Assert.assertEquals(2, id);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0110InsertKontaktWithNullVorname(final AddressBookDAO addressBookDAO) throws Exception
    {
        long id = addressBookDAO.insertKontakt("CNachname", null);
        Assert.assertEquals(3, id);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0120InsertKontaktWithBlankVorname(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        addressBookDAO.insertKontakt("DNachname", "");

        Assert.assertTrue(false);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0130InsertKontaktExisting(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        addressBookDAO.insertKontakt("ANachname", "AVorname");

        Assert.assertTrue(false);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0200UpdateKontakt(final AddressBookDAO addressBookDAO) throws Exception
    {
        boolean success = addressBookDAO.updateKontakt(2, "ANachname", "A-Vorname");

        Assert.assertTrue(success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0300InsertAttribut(final AddressBookDAO addressBookDAO) throws Exception
    {
        boolean success = addressBookDAO.insertAttribut(2, "STREET", "Street 1");

        Assert.assertTrue(success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0310InsertInsertAttributWithNullValue(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        boolean success = addressBookDAO.insertAttribut(2, "STREET", null);

        Assert.assertEquals(false, success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0320InsertInsertAttributWithBlankValue(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        boolean success = addressBookDAO.insertAttribut(2, "STREET", "");

        Assert.assertEquals(false, success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0330InsertInsertAttributWithNull(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        boolean success = addressBookDAO.insertAttribut(2, null, "Street 1");

        Assert.assertEquals(false, success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0340InsertInsertAttributWithBlank(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        boolean success = addressBookDAO.insertAttribut(2, "", "Street 1");

        Assert.assertEquals(false, success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0350InsertAttributExisting(final AddressBookDAO addressBookDAO) throws Exception
    {
        // Muss fehlschlagen.
        boolean success = addressBookDAO.insertAttribut(2, "STREET", "Street 1");

        Assert.assertEquals(false, success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0400UpdateAttribut(final AddressBookDAO addressBookDAO) throws Exception
    {
        boolean success = addressBookDAO.updateAttribut(2, "STREET", "Street 2");

        Assert.assertTrue(success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0500GetKontaktDetailsAll(final AddressBookDAO addressBookDAO) throws Exception
    {
        List<Kontakt> kontakts = addressBookDAO.getKontaktDetails();

        Assert.assertNotNull(kontakts);
        Assert.assertEquals(3, kontakts.size());

        Assert.assertEquals(1, kontakts.get(0).getID());
        Assert.assertEquals("BNachname", kontakts.get(0).getNachname());
        Assert.assertEquals("BVorname", kontakts.get(0).getVorname());
        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        Assert.assertEquals(0, attribute.size());

        Assert.assertEquals(3, kontakts.get(1).getID());
        Assert.assertEquals("CNachname", kontakts.get(1).getNachname());
        Assert.assertEquals(null, kontakts.get(1).getVorname());
        attribute = kontakts.get(1).getAttribute();
        Assert.assertEquals(0, attribute.size());

        Assert.assertEquals(2, kontakts.get(2).getID());
        Assert.assertEquals("ANachname", kontakts.get(2).getNachname());
        Assert.assertEquals("A-Vorname", kontakts.get(2).getVorname());
        attribute = kontakts.get(2).getAttribute();
        Assert.assertEquals(1, attribute.size());
        Assert.assertEquals(2, attribute.get(0).getKontaktID());
        Assert.assertEquals("STREET", attribute.get(0).getAttribut());
        Assert.assertEquals("Street 2", attribute.get(0).getWert());

    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0510GetKontaktDetailsWithID(final AddressBookDAO addressBookDAO) throws Exception
    {
        List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(2);

        Assert.assertNotNull(kontakts);
        Assert.assertEquals(1, kontakts.size());
        Assert.assertEquals(2, kontakts.get(0).getID());
        Assert.assertEquals("ANachname", kontakts.get(0).getNachname());
        Assert.assertEquals("A-Vorname", kontakts.get(0).getVorname());

        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        Assert.assertEquals(1, attribute.size());
        Assert.assertEquals(2, attribute.get(0).getKontaktID());
        Assert.assertEquals("STREET", attribute.get(0).getAttribut());
        Assert.assertEquals("Street 2", attribute.get(0).getWert());
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0520GetKontaktDetailsWithIDs(final AddressBookDAO addressBookDAO) throws Exception
    {
        List<Kontakt> kontakts = addressBookDAO.getKontaktDetails(2, 3);

        Assert.assertNotNull(kontakts);
        Assert.assertEquals(2, kontakts.size());

        Assert.assertEquals(3, kontakts.get(0).getID());
        Assert.assertEquals("CNachname", kontakts.get(0).getNachname());
        Assert.assertEquals(null, kontakts.get(0).getVorname());
        List<KontaktAttribut> attribute = kontakts.get(0).getAttribute();
        Assert.assertEquals(0, attribute.size());

        Assert.assertEquals(2, kontakts.get(1).getID());
        Assert.assertEquals("ANachname", kontakts.get(1).getNachname());
        Assert.assertEquals("A-Vorname", kontakts.get(1).getVorname());
        attribute = kontakts.get(1).getAttribute();
        Assert.assertEquals(1, attribute.size());
        Assert.assertEquals(2, attribute.get(0).getKontaktID());
        Assert.assertEquals("STREET", attribute.get(0).getAttribut());
        Assert.assertEquals("Street 2", attribute.get(0).getWert());
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0600GetKontakte(final AddressBookDAO addressBookDAO) throws Exception
    {
        List<Kontakt> kontakte = addressBookDAO.getKontakte();

        Assert.assertNotNull(kontakte);
        Assert.assertEquals(3, kontakte.size());

        Assert.assertEquals(2, kontakte.get(0).getID());
        Assert.assertEquals("ANachname", kontakte.get(0).getNachname());
        Assert.assertEquals("A-Vorname", kontakte.get(0).getVorname());

        Assert.assertEquals(1, kontakte.get(1).getID());
        Assert.assertEquals("BNachname", kontakte.get(1).getNachname());
        Assert.assertEquals("BVorname", kontakte.get(1).getVorname());

        Assert.assertEquals(3, kontakte.get(2).getID());
        Assert.assertEquals("CNachname", kontakte.get(2).getNachname());
        Assert.assertEquals(null, kontakte.get(2).getVorname());
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0700SearchKontakts(final AddressBookDAO addressBookDAO) throws Exception
    {
        List<Kontakt> kontakte = addressBookDAO.searchKontakte("vor");

        Assert.assertNotNull(kontakte);
        Assert.assertEquals(2, kontakte.size());

        Assert.assertEquals(2, kontakte.get(0).getID());
        Assert.assertEquals("ANachname", kontakte.get(0).getNachname());
        Assert.assertEquals("A-Vorname", kontakte.get(0).getVorname());

        List<KontaktAttribut> attribute = kontakte.get(0).getAttribute();
        Assert.assertEquals(1, attribute.size());
        Assert.assertEquals(2, attribute.get(0).getKontaktID());
        Assert.assertEquals("STREET", attribute.get(0).getAttribut());
        Assert.assertEquals("Street 2", attribute.get(0).getWert());

        Assert.assertEquals(1, kontakte.get(1).getID());
        Assert.assertEquals("BNachname", kontakte.get(1).getNachname());
        Assert.assertEquals("BVorname", kontakte.get(1).getVorname());
        Assert.assertEquals(0, kontakte.get(1).getAttribute().size());
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest0900DeleteAttribut(final AddressBookDAO addressBookDAO) throws Exception
    {
        boolean success = addressBookDAO.deleteAttribut(2, "STREET");

        Assert.assertTrue(success);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     * @throws Exception Falls was schief geht.
     */
    protected void doTest1000DeleteKontakt(final AddressBookDAO addressBookDAO) throws Exception
    {
        boolean success = addressBookDAO.deleteKontakt(3);

        Assert.assertTrue(success);
    }
}
