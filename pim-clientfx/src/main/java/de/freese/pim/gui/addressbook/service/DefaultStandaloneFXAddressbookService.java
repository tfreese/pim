// Created: 15.02.2017
package de.freese.pim.gui.addressbook.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.JavaType;
import de.freese.pim.core.PIMException;
import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.service.AddressBookService;
import de.freese.pim.gui.addressbook.model.FXKontakt;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * AddressbookService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 */
@Service("clientAddressBookService")
@Profile("ClientStandalone")
public class DefaultStandaloneFXAddressbookService extends AbstractFXAddressbookService
{
    private AddressBookService addressBookService;

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#deleteKontakt(long)
     */
    @Override
    public int deleteKontakt(final long id)
    {
        try
        {
            return getAddressBookService().deleteKontakt(id);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#getKontaktDetails(long[])
     */
    @Override
    public List<FXKontakt> getKontaktDetails(final long... ids)
    {
        try
        {
            List<Kontakt> contacts = getAddressBookService().getKontaktDetails(ids);

            return toFXContacts(contacts);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#insertKontakt(de.freese.pim.gui.addressbook.model.FXKontakt)
     */
    @Override
    public void insertKontakt(final FXKontakt kontakt)
    {
        try
        {
            long id = getAddressBookService().insertKontakt(kontakt.getNachname(), kontakt.getVorname());

            kontakt.setID(id);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    @Resource
    public void setAddressBookService(final AddressBookService addressBookService)
    {
        this.addressBookService = addressBookService;
    }

    /**
     * @see de.freese.pim.gui.addressbook.service.FXAddressbookService#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname)
    {
        try
        {
            return getAddressBookService().updateKontakt(id, nachname, vorname);
        }
        catch (Exception ex)
        {
            throw new PIMException(ex);
        }
    }

    protected AddressBookService getAddressBookService()
    {
        return this.addressBookService;
    }

    private List<FXKontakt> toFXContacts(final List<Kontakt> contacts) throws Exception
    {
        JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FXKontakt.class);

        byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(contacts);

        return getJsonMapper().readValue(jsonBytes, type);
    }
}
