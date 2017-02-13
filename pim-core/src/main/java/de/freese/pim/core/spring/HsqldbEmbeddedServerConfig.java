// Created: 10.02.2017
package de.freese.pim.core.spring;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.SocketUtils;
import de.freese.pim.common.utils.Utils;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbEmbeddedServer")
@PropertySource("classpath:tomcat-pool.properties")
public class HsqldbEmbeddedServerConfig extends AbstractHSQLDBConfig
{
    // static
    // {
    // // Der Port muss feststehen BEVOR die DataSourceAutoConfiguration anspringt !
    // // Siehe application-HsqldbEmbeddedServer.properties
    // int port = SocketUtils.findAvailableTcpPort();
    //
    // if (port <= 0)
    // {
    // port = Utils.getNextFreePort(49001);
    // }
    //
    // // Damit die Placeholder in Properties funktionieren: ${hsqldbPort}
    // System.setProperty("hsqldbPort", Integer.toString(port));
    // }

    /**
    *
    */
    @Resource
    private DataSource dataSource = null;

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

        // Der Port muss feststehen BEVOR die DataSourceAutoConfiguration anspringt !
        // Siehe application-HsqldbEmbeddedServer.properties
        int port = SocketUtils.findAvailableTcpPort();

        if (port <= 0)
        {
            port = Utils.getNextFreePort(49001);
        }

        // Damit die Placeholder in Properties funktionieren: ${hsqldbPort}
        System.setProperty("hsqldbPort", Integer.toString(port));
    }

    /**
     * @see de.freese.pim.core.spring.AbstractDBConfig#preDestroy()
     */
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
