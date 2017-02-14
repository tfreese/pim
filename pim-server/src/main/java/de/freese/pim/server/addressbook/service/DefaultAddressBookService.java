// Created: 11.01.2017
package de.freese.pim.server.addressbook.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.server.addressbook.dao.IAddressBookDAO;
import de.freese.pim.server.addressbook.model.Kontakt;
import de.freese.pim.server.service.AbstractService;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
public class DefaultAddressBookService extends AbstractService implements IAddressBookService
{
    /**
     *
     */
    private IAddressBookDAO addressBookDAO = null;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultAddressBookService}
     */
    public DefaultAddressBookService()
    {
        super();

    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory) throws Exception
    {
        return this.addressBookDAO.backup(directory);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    public boolean deleteAttribut(final long kontaktID, final String attribut) throws Exception
    {
        return this.addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#deleteKontakt(long)
     */
    @Override
    @Transactional
    public boolean deleteKontakt(final long id) throws Exception
    {
        return this.addressBookDAO.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontaktDetails(final long... ids) throws Exception
    {
        return this.addressBookDAO.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#getKontakte()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontakte() throws Exception
    {
        return this.addressBookDAO.getKontakte();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean insertAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        return this.addressBookDAO.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        return this.addressBookDAO.searchKontakte(name);
    }

    /**
     * @param addressBookDAO {@link IAddressBookDAO}
     */
    public void setAddressBookDAO(final IAddressBookDAO addressBookDAO)
    {
        this.addressBookDAO = addressBookDAO;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean updateAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.IAddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        return this.addressBookDAO.updateKontakt(id, nachname, vorname);
    }

    /**
     * @return {@link IAddressBookDAO}
     */
    protected IAddressBookDAO getAddressBookDAO()
    {
        return this.addressBookDAO;
    }
}
