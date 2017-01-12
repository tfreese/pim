// Created: 11.01.2017
package de.freese.pim.core.addressbook.dao;

import java.sql.Connection;
import java.sql.SQLException;

import de.freese.pim.core.persistence.ConnectionHolder;

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
     * @see de.freese.pim.core.addressbook.dao.AbstractAddressBookDAO#getConnection()
     */
    @Override
    protected Connection getConnection() throws SQLException
    {
        return ConnectionHolder.get();
    }
}
