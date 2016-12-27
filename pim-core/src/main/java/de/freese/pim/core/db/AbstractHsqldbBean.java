// Created: 15.12.2016
package de.freese.pim.core.db;

import java.sql.Connection;
import java.sql.Statement;

/**
 * {@link IDataSourceBean} für HSQLDB-Datenbanken.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHsqldbBean extends AbstractDataSourceBean
{
    /**
     * Erzeugt eine neue Instanz von {@link AbstractHsqldbBean}
     */
    public AbstractHsqldbBean()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#disconnect()
     */
    @Override
    public void disconnect() throws Exception
    {
        try (Connection con = getDataSource().getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("SHUTDOWN COMPACT");
        }

        super.disconnect();
    }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#getDriver()
     */
    @Override
    protected String getDriver()
    {
        return HSQLDB_DRIVER;
    }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#getValidationQuery()
     */
    @Override
    protected String getValidationQuery()
    {
        return HSQLDB_VALIDATION_QUERY;
    }

    /**
     * @see de.freese.pim.core.db.IDataSourceBean#populateIfEmpty(java.lang.Runnable)
     */
    @Override
    public void populateIfEmpty(final Runnable populateCallback) throws Exception
    {
        // @formatter:off
        String[] scripts = new String[]
        {
                "db/pim_hsqldb_schema.sql",
                "db/pim_hsqldb_data.sql",
                "db/pim_hsqldb_addressbook_schema.sql",
                "db/pim_hsqldb_addressbook_data.sql"
        };
        // @formatter:on

        populateIfEmpty(getDataSource(), populateCallback, scripts);
    }
}
