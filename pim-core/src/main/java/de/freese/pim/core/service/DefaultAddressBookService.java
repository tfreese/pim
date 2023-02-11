// Created: 11.01.2017
package de.freese.pim.core.service;

import java.nio.file.Path;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.core.dao.AddressBookDAO;
import de.freese.pim.core.model.addressbook.Kontakt;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
@Service("addressBookService")
@Profile("!ClientREST")
public class DefaultAddressBookService extends AbstractService implements AddressBookService {
    private AddressBookDAO addressBookDAO;

    /**
     * @see de.freese.pim.core.service.AddressBookService#backup(java.nio.file.Path)
     */
    @Override
    public boolean backup(final Path directory) {
        return this.addressBookDAO.backup(directory);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    public int deleteAttribut(final long kontaktID, final String attribut) {
        return this.addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#deleteKontakt(long)
     */
    @Override
    @Transactional
    public int deleteKontakt(final long id) {
        return this.addressBookDAO.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#getKontaktDetails(long[])
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontaktDetails(final long... ids) {
        return this.addressBookDAO.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#getKontakte()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontakte() {
        return this.addressBookDAO.getKontakte();
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int insertAttribut(final long kontaktID, final String attribut, final String wert) {
        return this.addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname) {
        return this.addressBookDAO.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#searchKontakte(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> searchKontakte(final String name) {
        return this.addressBookDAO.searchKontakte(name);
    }

    @Resource
    public void setAddressBookDAO(final AddressBookDAO addressBookDAO) {
        this.addressBookDAO = addressBookDAO;
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int updateAttribut(final long kontaktID, final String attribut, final String wert) {
        return this.addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.service.AddressBookService#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public int updateKontakt(final long id, final String nachname, final String vorname) {
        return this.addressBookDAO.updateKontakt(id, nachname, vorname);
    }

    protected AddressBookDAO getAddressBookDAO() {
        return this.addressBookDAO;
    }
}
