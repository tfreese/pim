package de.freese.pim.gui.addressbook.service;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import de.freese.pim.core.PIMException;
import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.service.AddressBookService;
import de.freese.pim.gui.addressbook.model.FxKontakt;

/**
 * AddressbookService für JavaFX, wenn es keinen Server gibt.
 *
 * @author Thomas Freese
 * @since 15.02.2017
 */
@Service("clientAddressBookService")
@Profile("ClientStandalone")
public class DefaultStandaloneFxAddressbookService extends AbstractFxAddressbookService {
    private AddressBookService addressBookService;

    @Override
    public int deleteKontakt(final long id) {
        try {
            return getAddressBookService().deleteKontakt(id);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public List<FxKontakt> getKontaktDetails(final long... ids) {
        try {
            final List<Kontakt> contacts = getAddressBookService().getKontaktDetails(ids);

            return contacts.stream().map(FxKontakt::from).toList();
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Override
    public void insertKontakt(final FxKontakt kontakt) {
        try {
            final long id = getAddressBookService().insertKontakt(kontakt.getNachname(), kontakt.getVorname());

            kontakt.setID(id);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    @Resource
    public void setAddressBookService(final AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @Override
    public int updateKontakt(final long id, final String nachname, final String vorname) {
        try {
            return getAddressBookService().updateKontakt(id, nachname, vorname);
        }
        catch (Exception ex) {
            throw new PIMException(ex);
        }
    }

    protected AddressBookService getAddressBookService() {
        return addressBookService;
    }

    // private List<FxKontakt> toFXContacts(final List<Kontakt> contacts) throws Exception {
    //     final JavaType type = getJsonMapper().getTypeFactory().constructCollectionType(ArrayList.class, FxKontakt.class);
    //
    //     final byte[] jsonBytes = getJsonMapper().writer().writeValueAsBytes(contacts);
    //
    //     return getJsonMapper().readValue(jsonBytes, type);
    // }
}
