// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;

import de.freese.pim.gui.addressbook.model.FXKontakt;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.addressbook.service.AddressBookService;

/**
 * AddressbookService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
public class DefaultEmbeddedFXAddressbookService extends AbstractFXAddressbookService
{
    /**
     *
     */
    private AddressBookService addressBookService = null;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultEmbeddedFXAddressbookService}
     */
    public DefaultEmbeddedFXAddressbookService()
    {
        super();
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id) throws Exception
    {
        return getAddressBookService().deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#getKontaktDetails(long[])
     */
    @Override
    public List<FXKontakt> getKontaktDetails(final long... ids) throws Exception
    {
        List<Kontakt> contacts = getAddressBookService().getKontaktDetails(ids);

        List<FXKontakt> fxBeans = toFXContacts(contacts);

        return fxBeans;
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#insertKontakt(de.freese.pim.gui.addressbook.model.FXKontakt)
     */
    @Override
    public void insertKontakt(final FXKontakt kontakt) throws Exception
    {
        long id = getAddressBookService().insertKontakt(kontakt.getNachname(), kontakt.getVorname());

        kontakt.setID(id);
    }

    /**
     * @param addressBookService {@link AddressBookService}
     */
    public void setAddressBookService(final AddressBookService addressBookService)
    {
        this.addressBookService = addressBookService;
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        return getAddressBookService().updateKontakt(id, nachname, vorname);
    }

    /**
     * Konvertiert die POJOs in die FX-Beans.
     *
     * @param contacts {@link List}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    private List<FXKontakt> toFXContacts(final List<Kontakt> contacts) throws Exception
    {
        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FXKontakt.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(contacts);
        List<FXKontakt> fxBeans = getJsonMapper().readValue(jsonBytes, type);

        return fxBeans;
    }

    /**
     * @return {@link AddressBookService}
     */
    protected AddressBookService getAddressBookService()
    {
        return this.addressBookService;
    }
}
