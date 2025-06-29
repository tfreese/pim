// Created: 11.01.2017
package de.freese.pim.core.service;

import java.nio.file.Path;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.core.dao.AddressBookDao;
import de.freese.pim.core.model.addressbook.Kontakt;

/**
 * Service für das AddressBook.
 *
 * @author Thomas Freese
 */
@Service("addressBookService")
@Profile("!ClientREST")
public class DefaultAddressBookService extends AbstractService implements AddressBookService {
    private AddressBookDao addressBookDAO;

    @Override
    public boolean backup(final Path directory) {
        return addressBookDAO.backup(directory);
    }

    @Override
    @Transactional
    public int deleteAttribut(final long kontaktID, final String attribut) {
        return addressBookDAO.deleteAttribut(kontaktID, attribut);
    }

    @Override
    @Transactional
    public int deleteKontakt(final long id) {
        return addressBookDAO.deleteKontakt(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontaktDetails(final long... ids) {
        return addressBookDAO.getKontaktDetails(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontakte() {
        return addressBookDAO.getKontakte();
    }

    @Override
    @Transactional
    public int insertAttribut(final long kontaktID, final String attribut, final String wert) {
        return addressBookDAO.insertAttribut(kontaktID, attribut, wert);
    }

    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname) {
        return addressBookDAO.insertKontakt(nachname, vorname);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> searchKontakte(final String name) {
        return addressBookDAO.searchKontakte(name);
    }

    @Resource
    public void setAddressBookDAO(final AddressBookDao addressBookDAO) {
        this.addressBookDAO = addressBookDAO;
    }

    @Override
    @Transactional
    public int updateAttribut(final long kontaktID, final String attribut, final String wert) {
        return addressBookDAO.updateAttribut(kontaktID, attribut, wert);
    }

    @Override
    @Transactional
    public int updateKontakt(final long id, final String nachname, final String vorname) {
        return addressBookDAO.updateKontakt(id, nachname, vorname);
    }

    protected AddressBookDao getAddressBookDAO() {
        return addressBookDAO;
    }
}
