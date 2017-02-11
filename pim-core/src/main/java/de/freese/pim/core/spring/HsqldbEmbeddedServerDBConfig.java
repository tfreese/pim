// Created: 10.02.2017
package de.freese.pim.core.spring;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hsqldb.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.util.SocketUtils;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.core.utils.Utils;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbEmbeddedServer")
public class HsqldbEmbeddedServerDBConfig extends AbstractHSQLDBConfig
{
    /**
    *
    */
    @Resource
    private DataSource dataSource = null;

    /**
     *
     */
    @Resource
    private Server server = null;

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbEmbeddedServerDBConfig}
     */
    public HsqldbEmbeddedServerDBConfig()
    {
        super();
    }

    /**
     * Die {@link DataSource} wird in {@link #preDestroy()} geschlossen.
     *
     * @param server {@link Server}
     * @return {@link DataSource}
     */
    @Bean(destroyMethod = "")
    @DependsOn("hsqldbServer")
    public DataSource dataSource(final Server server)
    {
        String userName = "sa";

        // CREATE USER EFREEST PASSWORD 'EFREEST'
        // CREATE USER SA PASSWORD DIGEST 'd41d8cd98f00b204e9800998ecf8427e'
        // ALTER USER SA SET LOCAL TRUE
        // GRANT DBA TO SA
        String password = null;
        int port = server.getPort();

        // ;hsqldb.tx=mvcc
        String url = String.format("jdbc:hsqldb:hsql://localhost:%d/%s", port, getDatabaseName());

        DataSource dataSource = createDataSource(getDriver(), url, userName, password, ISettingsService.MAX_ACTIVE_CONNECTIONS.get(), getValidationQuery());

        return dataSource;
    }

    /**
     * @param pimHome String
     * @return {@link Server}
     * @throws Exception Falls was schief geht.
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    // @Scope(ConfigurableBeanFactory#SCOPE_SINGLETON)
    public Server hsqldbServer(@Value("${pim.home}") final String pimHome) throws Exception
    {
        Path dbPath = Paths.get(pimHome).resolve(getDatabaseName());

        String dbName = getDatabaseName();
        int port = SocketUtils.findAvailableTcpPort();

        if (port <= 0)
        {
            port = Utils.getNextFreePort(49001);
        }

        Server server = new Server();
        server.setLogWriter(null); // can use custom writer
        server.setErrWriter(null); // can use custom writer
        server.setNoSystemExit(true);
        server.setSilent(true);
        server.setTrace(false);
        // HsqlProperties p = new HsqlProperties();
        // p.setProperty("server.database.0", "file:/" + dbPath);
        // p.setProperty("server.dbname.0", getDBName());
        // p.setProperty("server.port", DB_PORT);
        // this.server.setProperties(p);

        // this.server.setAddress("0.0.0.0");
        server.setPort(port);
        server.setDatabaseName(0, dbName);
        server.setDatabasePath(0, "file:/" + dbPath);

        return server;
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
