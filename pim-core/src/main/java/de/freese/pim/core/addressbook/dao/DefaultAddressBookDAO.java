// Created: 11.01.2017
package de.freese.pim.core.addressbook.dao;

import javax.sql.DataSource;
import de.freese.pim.core.persistence.JdbcTemplate;

/**
 * Default-Implementierung für das Addressbuch DAO.<br>
 *
 * @author Thomas Freese
 */
public class DefaultAddressBookDAO extends AbstractAddressBookDAO
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultAddressBookDAO}
     */
    public DefaultAddressBookDAO()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link DefaultAddressBookDAO}
     *
     * @param dataSource {@link DataSource}
     */
    public DefaultAddressBookDAO(final DataSource dataSource)
    {
        super();

        setJdbcTemplate(new JdbcTemplate().setDataSource(dataSource));
    }
}
