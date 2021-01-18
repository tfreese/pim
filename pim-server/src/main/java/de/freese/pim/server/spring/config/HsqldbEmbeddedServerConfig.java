// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.util.SocketUtils;
import de.freese.pim.common.utils.Utils;
import de.freese.spring.autoconfigure.hsqldbserver.HsqldbServerAutoConfiguration;

/**
 * Spring-Konfiguration der Datenbank.<br>
 *
 * @see HsqldbServerAutoConfiguration
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbEmbeddedServer")
@PropertySources(
{
        @PropertySource("classpath:hikari-pool.properties"), @PropertySource("classpath:database.properties")
})
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

    // /**
    // *
    // */
    // @Resource
    // private Server server = null;

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbEmbeddedServerConfig}
     */
    public HsqldbEmbeddedServerConfig()
    {
        super();

        // int port = getNextFreePort();
        //
        // // Damit die Placeholder in Properties funktionieren: ${hsqldbPort}
        // System.setProperty("hsqldbPort", Integer.toString(port));
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
