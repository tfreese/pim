// Created: 11.01.2017
package de.freese.pim.server.addressbook.service;

import java.nio.file.Path;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.freese.pim.common.service.AbstractRemoteService;
import de.freese.pim.server.addressbook.model.Kontakt;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/addressBook", produces = MediaType.APPLICATION_JSON_VALUE)
public class AddressBookRestController extends AbstractRemoteService implements AddressBookService
{
    /**
     *
     */
    private AddressBookService addressBookService;

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    @PostMapping("/backup")
    public boolean backup(final Path directory)
    {
        return getAddressBookService().backup(directory);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @PostMapping("/attribute/delete/{contactID}/{attribute}")
    public int deleteAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut)
    {
        return getAddressBookService().deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteKontakt(long)
     */
    @Override
    @PostMapping("/contact/delete/{contactID}")
    public int deleteKontakt(@PathVariable("contactID") final long id)
    {
        return getAddressBookService().deleteKontakt(id);
    }

    /**
     * @return {@link AddressBookService}
     */
    protected AddressBookService getAddressBookService()
    {
        return this.addressBookService;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    @PostMapping("details")
    public List<Kontakt> getKontaktDetails(@RequestParam("ids") final long...ids)
    {
        return getAddressBookService().getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontakte()
     */
    @Override
    @GetMapping("/contacts")
    public List<Kontakt> getKontakte()
    {
        return getAddressBookService().getKontakte();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/attribute/insert/{contactID}/{attribute}/{value}")
    public int insertAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut,
                              @PathVariable("value") final String wert)
    {
        return getAddressBookService().insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/contact/insert")
    public long insertKontakt(@RequestParam("surname") final String nachname, @RequestParam("forename") final String vorname)
    {
        return getAddressBookService().insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    @GetMapping("/contact/search/{name}")
    public List<Kontakt> searchKontakte(@PathVariable("name") final String name)
    {
        return getAddressBookService().searchKontakte(name);
    }

    /**
     * @param addressBookService {@link AddressBookService}
     */
    @Resource
    public void setAddressBookService(final AddressBookService addressBookService)
    {
        this.addressBookService = addressBookService;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/attribute/update/{contactID}/{attribute}/{value}")
    public int updateAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut,
                              @PathVariable("value") final String wert)
    {
        return getAddressBookService().updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @PostMapping("/contact/update/{contactID}")
    public int updateKontakt(@PathVariable("contactID") final long id, @RequestParam("surname") final String nachname,
                             @RequestParam("forename") final String vorname)
    {
        return getAddressBookService().updateKontakt(id, nachname, vorname);
    }
}
