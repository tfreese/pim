/**
 * Created: 10.02.2017
 */

package de.freese.pim.core.spring;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 * Basis-Konfiguration der Datenbank für HSQLDB.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHSQLDBConfig extends AbstractDBConfig
{
    /**
     * Erstellt ein neues {@link AbstractHSQLDBConfig} Object.
     */
    public AbstractHSQLDBConfig()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.spring.AbstractDBConfig#getDriver()
     */
    @Override
    protected String getDriver()
    {
        return HSQLDB_DRIVER;
    }

    /**
     * @see de.freese.pim.core.spring.AbstractDBConfig#getValidationQuery()
     */
    @Override
    protected String getValidationQuery()
    {
        return HSQLDB_VALIDATION_QUERY;
    }

    /**
     * Führt das Statement "SHUTDOWN COMPACT" aus.
     *
     * @param dataSource {@link DataSource}
     * @throws SQLException Falls was schief geht.
     */
    protected void shutdownCompact(final DataSource dataSource) throws SQLException
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("SHUTDOWN COMPACT");
        }
    }
}
