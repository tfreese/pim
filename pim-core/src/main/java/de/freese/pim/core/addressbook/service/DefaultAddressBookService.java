// Created: 11.01.2017
package de.freese.pim.core.addressbook.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import de.freese.pim.core.addressbook.dao.IAddressBookDAO;
import de.freese.pim.core.addressbook.model.Kontakt;
import de.freese.pim.core.jdbc.tx.Transactional;
import de.freese.pim.core.mail.service.AbstractService;

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
    private final IAddressBookDAO addressBookDAO;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultAddressBookService}
     *
     * @param addressBookDAO {@link IAddressBookDAO}
     */
    public DefaultAddressBookService(final IAddressBookDAO addressBookDAO)
    {
        super();

        Objects.requireNonNull(addressBookDAO, "addressBookDAO required");

        this.addressBookDAO = addressBookDAO;
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory) throws Exception
    {
        return this.addressBookDAO.backup(directory);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    public boolean deleteAttribut(final long kontaktID, final String attribut) throws Exception
    {
        return this.addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#deleteKontakt(long)
     */
    @Override
    @Transactional
    public boolean deleteKontakt(final long id) throws Exception
    {
        return this.addressBookDAO.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    public List<Kontakt> getKontaktDetails(final long... ids) throws Exception
    {
        return this.addressBookDAO.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#getKontakte()
     */
    @Override
    public List<Kontakt> getKontakte() throws Exception
    {
        return this.addressBookDAO.getKontakte();
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean insertAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        return this.addressBookDAO.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        return this.addressBookDAO.searchKontakte(name);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean updateAttribut(final long kontaktID, final String attribut, final String wert) throws Exception
    {
        return this.addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.IAddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        return this.addressBookDAO.updateKontakt(id, nachname, vorname);
    }
}
