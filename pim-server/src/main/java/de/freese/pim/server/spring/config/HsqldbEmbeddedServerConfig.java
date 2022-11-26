// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import org.hsqldb.Database;
import org.hsqldb.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring-Konfiguration der Datenbank.<br>
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbEmbeddedServer")
@PropertySource("classpath:hikari-pool.properties")
@PropertySource("classpath:database.properties")
public class HsqldbEmbeddedServerConfig extends AbstractHsqldbConfig
{
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server hsqldbServer(@Value("${pim.home}") final String pimHome, @Value("${pim.db-name}") final String pimDbName,
                               @Value("${hsqldbPort}") final int port)
    {
        Server server = new Server()
        {
            /**
             * @see org.hsqldb.server.Server#shutdown()
             */
            @Override
            public void shutdown()
            {
                // "SHUTDOWN COMPACT"
                super.shutdownWithCatalogs(Database.CLOSEMODE_COMPACT);
            }

        };
        server.setLogWriter(null);
        server.setErrWriter(null);
        // server.setLogWriter(new PrintWriter(System.out)); // can use custom writer
        // server.setErrWriter(new PrintWriter(System.err)); // can use custom writer
        server.setNoSystemExit(true);
        server.setSilent(true);
        server.setTrace(false);
        server.setPort(port);

        server.setDatabaseName(0, pimDbName);
        server.setDatabasePath(0, "file:/" + pimHome + "/" + pimDbName);

        return server;
    }
}
