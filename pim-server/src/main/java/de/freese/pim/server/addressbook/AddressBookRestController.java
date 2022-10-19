// Created: 11.01.2017
package de.freese.pim.server.addressbook;

import java.nio.file.Path;
import java.util.List;

import jakarta.annotation.Resource;

import de.freese.pim.core.model.addressbook.Kontakt;
import de.freese.pim.core.service.AbstractRemoteService;
import de.freese.pim.core.service.AddressBookService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/addressBook", produces = MediaType.APPLICATION_JSON_VALUE)
public class AddressBookRestController extends AbstractRemoteService implements AddressBookService
{
    private AddressBookService addressBookService;

    /**
     * @see de.freese.pim.core.service.AddressBookService#backup(java.nio.file.Path)
     */
    @Override
    @PostMapping("/backup")
    public boolean backup(final Path directory)
    {
        return getAddressBookService().backup(directory);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#deleteAttribut(long, java.lang.String)
     */
    @Override
    @PostMapping("/attribute/delete/{contactID}/{attribute}")
    public int deleteAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut)
    {
        return getAddressBookService().deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#deleteKontakt(long)
     */
    @Override
    @PostMapping("/contact/delete/{contactID}")
    public int deleteKontakt(@PathVariable("contactID") final long id)
    {
        return getAddressBookService().deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#getKontaktDetails(long[])
     */
    @Override
    @PostMapping("details")
    public List<Kontakt> getKontaktDetails(@RequestParam("ids") final long... ids)
    {
        return getAddressBookService().getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#getKontakte()
     */
    @Override
    @GetMapping("/contacts")
    public List<Kontakt> getKontakte()
    {
        return getAddressBookService().getKontakte();
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/attribute/insert/{contactID}/{attribute}/{value}")
    public int insertAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut,
                              @PathVariable("value") final String wert)
    {
        return getAddressBookService().insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/contact/insert")
    public long insertKontakt(@RequestParam("surname") final String nachname, @RequestParam("forename") final String vorname)
    {
        return getAddressBookService().insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#searchKontakte(java.lang.String)
     */
    @Override
    @GetMapping("/contact/search/{name}")
    public List<Kontakt> searchKontakte(@PathVariable("name") final String name)
    {
        return getAddressBookService().searchKontakte(name);
    }

    @Resource
    public void setAddressBookService(final AddressBookService addressBookService)
    {
        this.addressBookService = addressBookService;
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/attribute/update/{contactID}/{attribute}/{value}")
    public int updateAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut,
                              @PathVariable("value") final String wert)
    {
        return getAddressBookService().updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/contact/update/{contactID}")
    public int updateKontakt(@PathVariable("contactID") final long id, @RequestParam("surname") final String nachname,
                             @RequestParam("forename") final String vorname)
    {
        return getAddressBookService().updateKontakt(id, nachname, vorname);
    }

    protected AddressBookService getAddressBookService()
    {
        return this.addressBookService;
    }
}
