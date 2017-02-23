// Created: 11.01.2017
package de.freese.pim.server.addressbook.service;

import java.nio.file.Path;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.freese.pim.server.addressbook.dao.AddressBookDAO;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.service.AbstractService;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
// @Service("addressBookService")
@RestController("addressBookService")
@RequestMapping(path = "/addressBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DefaultAddressBookService extends AbstractService implements AddressBookService
{
    /**
     *
     */
    private AddressBookDAO addressBookDAO = null;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultAddressBookService}
     */
    public DefaultAddressBookService()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    @PostMapping("/backup")
    public boolean backup(final Path directory) throws Exception
    {
        return this.addressBookDAO.backup(directory);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    @PostMapping("/attribute/delete/{contactID}/{attribute}")
    public int deleteAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut)
            throws Exception
    {
        return this.addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteKontakt(long)
     */
    @Override
    @Transactional
    @PostMapping("/contact/delete/{contactID}")
    public int deleteKontakt(@PathVariable("contactID") final long id) throws Exception
    {
        return this.addressBookDAO.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    @Transactional(readOnly = true)
    @PostMapping("details")
    public List<Kontakt> getKontaktDetails(@RequestParam("ids") final long... ids) throws Exception
    {
        return this.addressBookDAO.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontakte()
     */
    @Override
    @Transactional(readOnly = true)
    @GetMapping("/contacts")
    public List<Kontakt> getKontakte() throws Exception
    {
        return this.addressBookDAO.getKontakte();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    @PostMapping("/attribute/insert/{contactID}/{attribute}/{value}")
    public int insertAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut,
            @PathVariable("value") final String wert) throws Exception
    {
        return this.addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    @PostMapping("/contact/insert")
    public long insertKontakt(@RequestParam("surname") final String nachname, @RequestParam("forename") final String vorname)
            throws Exception
    {
        return this.addressBookDAO.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    @GetMapping("/contact/search/{name}")
    public List<Kontakt> searchKontakte(@PathVariable("name") final String name) throws Exception
    {
        return this.addressBookDAO.searchKontakte(name);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     */
    @Resource
    public void setAddressBookDAO(final AddressBookDAO addressBookDAO)
    {
        this.addressBookDAO = addressBookDAO;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    @PostMapping("/attribute/update/{contactID}/{attribute}/{value}")
    public int updateAttribut(@PathVariable("contactID") final long kontaktID, @PathVariable("attribute") final String attribut,
            @PathVariable("value") final String wert) throws Exception
    {
        return this.addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    @PostMapping("/contact/update/{contactID}")
    public int updateKontakt(@PathVariable("contactID") final long id, @RequestParam("surname") final String nachname,
            @RequestParam("forename") final String vorname) throws Exception
    {
        return this.addressBookDAO.updateKontakt(id, nachname, vorname);
    }

    /**
     * @return {@link AddressBookDAO}
     */
    protected AddressBookDAO getAddressBookDAO()
    {
        return this.addressBookDAO;
    }
}
