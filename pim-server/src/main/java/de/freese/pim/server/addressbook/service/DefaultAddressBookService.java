// Created: 11.01.2017
package de.freese.pim.server.addressbook.service;

import java.nio.file.Path;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.freese.pim.common.service.AbstractService;
import de.freese.pim.server.addressbook.dao.AddressBookDAO;
import de.freese.pim.server.addressbook.model.Kontakt;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
@Service("addressBookService")
public class DefaultAddressBookService extends AbstractService implements AddressBookService
{
    /**
     *
     */
    private AddressBookDAO addressBookDAO;

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory)
    {
        return this.addressBookDAO.backup(directory);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    public int deleteAttribut(final long kontaktID, final String attribut)
    {
        return this.addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#deleteKontakt(long)
     */
    @Override
    @Transactional
    public int deleteKontakt(final long id)
    {
        return this.addressBookDAO.deleteKontakt(id);
    }

    /**
     * @return {@link AddressBookDAO}
     */
    protected AddressBookDAO getAddressBookDAO()
    {
        return this.addressBookDAO;
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontaktDetails(final long...ids)
    {
        return this.addressBookDAO.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#getKontakte()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontakte()
    {
        return this.addressBookDAO.getKontakte();
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int insertAttribut(final long kontaktID, final String attribut, final String wert)
    {
        return this.addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname)
    {
        return this.addressBookDAO.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> searchKontakte(final String name)
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
    public int updateAttribut(final long kontaktID, final String attribut, final String wert)
    {
        return this.addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.server.addressbook.dao.AddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int updateKontakt(final long id, final String nachname, final String vorname)
    {
        return this.addressBookDAO.updateKontakt(id, nachname, vorname);
    }
}
