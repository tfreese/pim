// Created: 11.01.2017
package de.freese.pim.server.addressbook.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.server.addressbook.dao.AddressBookDAO;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.service.AbstractService;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
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
    public boolean backup(final Path directory) throws Exception
    {
        return this.addressBookDAO.backup(directory);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    public int deleteAttribut(final long kontaktID, final String attribut) throws Exception
    {
        return this.addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteKontakt(long)
     */
    @Override
    @Transactional
    public int deleteKontakt(final long id) throws Exception
    {
        return this.addressBookDAO.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontaktDetails(final long... ids) throws Exception
    {
        return this.addressBookDAO.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontakte()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontakte() throws Exception
    {
        return this.addressBookDAO.getKontakte();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int insertAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        return this.addressBookDAO.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        return this.addressBookDAO.searchKontakte(name);
    }

    /**
     * @param addressBookDAO {@link AddressBookDAO}
     */
    public void setAddressBookDAO(final AddressBookDAO addressBookDAO)
    {
        this.addressBookDAO = addressBookDAO;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int updateAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int updateKontakt(final long id, final String nachname, final String vorname) throws Exception
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
