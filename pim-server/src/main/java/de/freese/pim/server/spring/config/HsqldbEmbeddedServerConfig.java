// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hsqldb.Database;
import org.hsqldb.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.SocketUtils;

import de.freese.pim.common.utils.Utils;

/**
 * Spring-Konfiguration der Datenbank.<br>
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbEmbeddedServer")
@PropertySource("classpath:hikari-pool.properties")
@PropertySource("classpath:database.properties")
public class HsqldbEmbeddedServerConfig extends AbstractHSQLDBConfig
{
    static
    {
        int port = getNextFreePort();

        // Damit die Placeholder in Properties funktionieren: ${hsqldbPort}
        System.setProperty("hsqldbPort", Integer.toString(port));
    }

    /**
     * Der Port muss feststehen BEVOR die DataSourceAutoConfiguration anspringt !<br>
     * Siehe application-HsqldbEmbeddedServer.properties
     *
     * @return int
     */
    private static int getNextFreePort()
    {
        int port = SocketUtils.findAvailableTcpPort();

        if (port <= 0)
        {
            port = Utils.getNextFreePort(49001);
        }

        return port;
    }

    /**
    *
    */
    @Resource
    private DataSource dataSource;

    /**
     * @param pimHome String
     * @param pimDbName String
     * @param port int
     *
     * @return {@link Server}
     */
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

    /**
     * @see de.freese.pim.server.spring.config.AbstractDBConfig#preDestroy()
     */
    @SuppressWarnings("deprecation")
    @Override
    @PreDestroy
    public void preDestroy() throws Exception
    {
        // shutdownCompact(this.dataSource);
        close(this.dataSource);
        //
        // // server.shutdownWithCatalogs(Database.CLOSEMODE_COMPACT);
        // this.server.shutdownWithCatalogs(Database.CLOSEMODE_IMMEDIATELY);
        // // server.stop();
    }
}
