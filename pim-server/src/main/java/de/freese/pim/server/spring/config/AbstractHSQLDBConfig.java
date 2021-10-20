// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.UnaryOperator;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;

/**
 * Basis-Konfiguration der Datenbank für HSQLDB.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHSQLDBConfig extends AbstractDBConfig
{
    static
    {
        System.setProperty("flyway.locations", "classpath:db/hsqldb");
    }

    /**
     * SQL für Sequenz-Abfragen.
     *
     * @return {@link UnaryOperator}
     */
    @Bean
    public UnaryOperator<String> sequenceQuery()
    {
        return seq -> "call next value for " + seq;
    }

    /**
     * Führt das Statement "SHUTDOWN COMPACT" aus.
     *
     * @param dataSource {@link DataSource}
     *
     * @throws SQLException Falls was schief geht.
     *
     * @deprecated Siehe misc-jsensors: HsqldbServerAutoConfiguration#hsqldbServer
     */
    @Deprecated
    protected void shutdownCompact(final DataSource dataSource) throws SQLException
    {
        if (dataSource == null)
        {
            return;
        }

        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("SHUTDOWN COMPACT");
        }
    }
}
