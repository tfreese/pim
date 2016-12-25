/**
 * Created: 10.07.2016
 */

package de.freese.pim.core.addressbook.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import de.freese.pim.core.addressbook.model.Kontakt;

/**
 * DAO-Implementierung für das Addressbuch mit Connection- und Transaction-Steuerung durch das Spring-Framework.
 *
 * @author Thomas Freese
 */
public class SpringAddressBookDAO extends AbstractAddressBookDAO
{
    /**
    *
    */
    private final DataSource dataSource;

    /**
     * Erstellt ein neues {@link SpringAddressBookDAO} Object.
     *
     * @param dataSource {@link DataSource}
     */
    public SpringAddressBookDAO(final DataSource dataSource)
    {
        super();

        this.dataSource = dataSource;
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#deleteAttribut(long, java.lang.String)
     */
    @Override
    @Transactional
    public boolean deleteAttribut(final long kontakt_id, final String attribut) throws Exception
    {
        return super.deleteAttribut(kontakt_id, attribut);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#deleteKontakt(long)
     */
    @Override
    @Transactional
    public boolean deleteKontakt(final long id) throws Exception
    {
        return super.deleteKontakt(id);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#getKontaktDetails(long[])
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontaktDetails(final long... ids) throws Exception
    {
        return super.getKontaktDetails(ids);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#getKontakte()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> getKontakte() throws Exception
    {
        return super.getKontakte();
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#insertAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean insertAttribut(final long kontakt_id, final String attribut, final String wert) throws Exception
    {
        return super.insertAttribut(kontakt_id, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#insertKontakt(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public long insertKontakt(final String nachname, final String vorname) throws Exception
    {
        return super.insertKontakt(nachname, vorname);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#searchKontakte(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kontakt> searchKontakte(final String name) throws Exception
    {
        return super.searchKontakte(name);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#updateAttribut(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean updateAttribut(final long kontakt_id, final String attribut, final String wert) throws Exception
    {
        return super.updateAttribut(kontakt_id, attribut, wert);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#updateKontakt(long, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean updateKontakt(final long id, final String nachname, final String vorname) throws Exception
    {
        return super.updateKontakt(id, nachname, vorname);
    }

    /**
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#getConnection()
     */
    @Override
    protected Connection getConnection() throws SQLException
    {
        return DataSourceUtils.getConnection(this.dataSource);
    }
}
