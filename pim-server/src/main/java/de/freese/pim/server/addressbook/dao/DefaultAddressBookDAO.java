// Created: 11.01.2017
package de.freese.pim.server.addressbook.dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

/**
 * Default-Implementierung für das Addressbuch DAO.<br>
 *
 * @author Thomas Freese
 */
@Repository("addressBookDAO")
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
     * @see de.freese.pim.server.dao.AbstractDAO#setDataSource(javax.sql.DataSource)
     */
    @Override
    @Resource
    public void setDataSource(final DataSource dataSource)
    {
        super.setDataSource(dataSource);
    }
}
