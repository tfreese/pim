// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.List;
import java.util.stream.Collectors;

import de.freese.pim.gui.addressbook.model.FXKontakt;
import de.freese.pim.gui.addressbook.model.FXKontaktAttribut;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.addressbook.model.KontaktAttribut;
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
        List<Kontakt> pojos = getAddressBookService().getKontaktDetails(ids);

        List<FXKontakt> fxBeans = pojos.stream().map(this::toFXBean).collect(Collectors.toList());

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
     * Konvertiert das POJO in die FX-Bean.
     *
     * @param kontakt {@link Kontakt}
     * @return {@link FXKontakt}
     */
    private FXKontakt toFXBean(final Kontakt kontakt)
    {
        FXKontakt k = new FXKontakt();
        k.setID(kontakt.getID());
        k.setNachname(kontakt.getNachname());
        k.setVorname(kontakt.getVorname());

        k.getAttribute().addAll(kontakt.getAttribute().stream().map(this::toFXBean).collect(Collectors.toList()));

        return k;
    }

    /**
     * Konvertiert das POJO in die FX-Bean.
     *
     * @param attribut {@link KontaktAttribut}
     * @return {@link FXKontaktAttribut}
     */
    private FXKontaktAttribut toFXBean(final KontaktAttribut attribut)
    {
        FXKontaktAttribut a = new FXKontaktAttribut();
        a.setAttribut(attribut.getAttribut());
        a.setKontaktID(attribut.getKontaktID());
        a.setWert(attribut.getWert());

        return a;
    }

    /**
     * @return {@link AddressBookService}
     */
    protected AddressBookService getAddressBookService()
    {
        return this.addressBookService;
    }
}
