// Created: 11.01.2017
package de.freese.pim.server.addressbook;

import java.nio.file.Path;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.service.AbstractRemoteService;
import de.freese.pim.core.service.AddressBookService;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/addressBook", produces = MediaType.APPLICATION_JSON_VALUE)
public class AddressBookRestController extends AbstractRemoteService implements AddressBookService {
    private AddressBookService addressBookService;

    @Override
    @PostMapping("/backup")
    public boolean backup(final Path directory) {
        return getAddressBookService().backup(directory);
    }

    @Override
    @PostMapping("/attribute/delete/{contactID}/{attribute}")
    public int deleteAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut) {
        return getAddressBookService().deleteAttribut(kontaktID, attribut);
    }

    @Override
    @PostMapping("/contact/delete/{contactID}")
    public int deleteKontakt(@PathVariable("contactID") final long id) {
        return getAddressBookService().deleteKontakt(id);
    }

    @Override
    @PostMapping("details")
    public List<Kontakt> getKontaktDetails(@RequestParam("ids") final long... ids) {
        return getAddressBookService().getKontaktDetails(ids);
    }

    @Override
    @GetMapping("/contacts")
    public List<Kontakt> getKontakte() {
        return getAddressBookService().getKontakte();
    }

    @Override
    @PostMapping("/attribute/insert/{contactID}/{attribute}/{value}")
    public int insertAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut, @PathVariable("value") final String wert) {
        return getAddressBookService().insertAttribut(kontaktID, attribut, wert);
    }

    @Override
    @PostMapping("/contact/insert")
    public long insertKontakt(@RequestParam("surname") final String nachname, @RequestParam("forename") final String vorname) {
        return getAddressBookService().insertKontakt(nachname, vorname);
    }

    @Override
    @GetMapping("/contact/search/{name}")
    public List<Kontakt> searchKontakte(@PathVariable("name") final String name) {
        return getAddressBookService().searchKontakte(name);
    }

    @Resource
    public void setAddressBookService(final AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @Override
    @PostMapping("/attribute/update/{contactID}/{attribute}/{value}")
    public int updateAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut, @PathVariable("value") final String wert) {
        return getAddressBookService().updateAttribut(kontaktID, attribut, wert);
    }

    @Override
    @PostMapping("/contact/update/{contactID}")
    public int updateKontakt(@PathVariable("contactID") final long id, @RequestParam("surname") final String nachname, @RequestParam("forename") final String vorname) {
        return getAddressBookService().updateKontakt(id, nachname, vorname);
    }

    protected AddressBookService getAddressBookService() {
        return addressBookService;
    }
}
