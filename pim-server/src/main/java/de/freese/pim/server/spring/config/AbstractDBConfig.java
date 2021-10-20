// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Basis-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDBConfig
{
    /**
     * @param dataSource {@link DataSource}
     *
     * @throws Exception Falls was schief geht.
     *
     * @deprecated Entfällt
     */
    @Deprecated
    protected void close(final DataSource dataSource) throws Exception
    {
        if (dataSource instanceof DisposableBean db)
        {
            db.destroy();
        }
        else if (dataSource instanceof HikariDataSource ds)
        {
            ds.close();
        }
        // else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource ds)
        // {
        // ds.close(true);
        // }
    }

    /**
     * Beendet alle Verbindungen und schliesst die {@link DataSource}.
     *
     * @throws Exception Falls was schief geht.
     */
    public abstract void preDestroy() throws Exception;
}
