// Created: 11.01.2017
package de.freese.pim.core.addressbook.dao;

import java.sql.Connection;

import javax.sql.DataSource;

import de.freese.pim.core.persistence.ConnectionHolder;
import de.freese.pim.core.persistence.JdbcTemplate;

/**
 * DAO-Implementierung für das Addressbuch, welches sich die {@link Connection} aus dem {@link ConnectionHolder} holt.<br>
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
