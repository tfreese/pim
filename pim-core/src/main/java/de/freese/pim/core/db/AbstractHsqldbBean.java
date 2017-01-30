// Created: 15.12.2016
package de.freese.pim.core.db;

import java.sql.Connection;
import java.sql.Statement;
import org.flywaydb.core.Flyway;

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
        if (getDataSource() != null)
        {
            try (Connection con = getDataSource().getConnection();
                 Statement stmt = con.createStatement())
            {
                stmt.execute("SHUTDOWN COMPACT");
            }
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
//        // @formatter:off
//        String[] scripts = new String[]
//        {
//                "db/hsqldb/pim_schema.sql",
//                "db/hsqldb/pim_data.sql",
//                "db/hsqldb/pim_addressbook_schema.sql",
//                "db/hsqldb/pim_addressbook_data.sql",
//                "db/hsqldb/pim_mail_schema.sql"
//        };
//        // @formatter:on

        Flyway flyway = new Flyway();
        flyway.setEncoding("UTF-8");
        flyway.setLocations("classpath:db/hsqldb");
        flyway.setDataSource(getDataSource());

        // Workaround für HSQLDB 2.3.4 Bug, geht aber nicht.
        // try (Connection con = getDataSource().getConnection();
        // Statement stmt = con.createStatement())
        // {
        // stmt.execute("SET DATABASE TRANSACTION CONTROL LOCKS");
        // }

        flyway.migrate();

        // Workaround für HSQLDB 2.3.4 Bug, geht aber nicht.
        // try (Connection con = getDataSource().getConnection();
        // Statement stmt = con.createStatement())
        // {
        // stmt.execute("SET DATABASE TRANSACTION CONTROL MVCC");
        // }

        // populateIfEmpty(getDataSource(), populateCallback, scripts);
    }
}
