// Created: 15.12.2016
package de.freese.pim.core.db;

import java.nio.file.Path;
import javax.sql.DataSource;
import org.hsqldb.Database;
import org.hsqldb.server.Server;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.core.utils.Utils;

/**
 * {@link IDataSourceBean} für einen HSQLDB-Server.
 *
 * @author Thomas Freese
 */
public class HsqldbEmbeddedServer extends AbstractHsqldbBean
{
    /**
    *
    */
    private Server server = null;

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbEmbeddedServer}
     */
    public HsqldbEmbeddedServer()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#configure(de.freese.pim.core.service.ISettingsService)
     */
    @Override
    public void configure(final ISettingsService settingsService) throws Exception
    {
        Path home = settingsService.getHome();
        Path dbPath = getDBPath(home);

        String driver = getDriver();
        String dbName = getDBName();
        String dbHost = settingsService.getDBHost();
        String userName = settingsService.getDBUser();
        String password = settingsService.getDBPassword();
        int port = settingsService.getDBPort();
        String validationQuery = getValidationQuery();

        if (port <= 0)
        {
            port = Utils.getNextFreePort(49001);
        }

        // ;hsqldb.tx=mvcc
        String url = String.format("jdbc:hsqldb:hsql://%s:%d/%s", dbHost, port, dbName);

        this.server = new Server();
        this.server.setLogWriter(null); // can use custom writer
        this.server.setErrWriter(null); // can use custom writer
        this.server.setSilent(true);
        this.server.setNoSystemExit(true);
        // HsqlProperties p = new HsqlProperties();
        // p.setProperty("server.database.0", "file:/" + dbPath);
        // p.setProperty("server.dbname.0", getDBName());
        // p.setProperty("server.port", DB_PORT);
        // this.server.setProperties(p);

        // this.server.setAddress("0.0.0.0");
        this.server.setPort(port);
        this.server.setDatabaseName(0, dbName);
        this.server.setDatabasePath(0, "file:/" + dbPath);

        this.server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                disconnect();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }));

        DataSource dataSource = createDataSource(driver, url, userName, password, validationQuery);
        setDataSource(dataSource);
    }

    // /**
    // * @see de.freese.pim.core.db.AbstractDataSourceBean#createDataSource(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    // * java.lang.String)
    // */
    // @Override
    // protected DataSource createDataSource(final String driver, final String url, final String username, final String password, final String validationQuery)
    // {
    // SimpleDataSource ds = new SimpleDataSource();
    // ds.setDriverClassName(driver);
    // ds.setUrl(url);
    // ds.setUsername(username);
    // ds.setPassword(password);
    // ds.setAutoCommit(false);
    //
    // return ds;
    // }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#disconnect()
     */
    @Override
    public void disconnect() throws Exception
    {
        super.disconnect();

        // this.server.shutdownWithCatalogs(Database.CLOSEMODE_COMPACT);
        this.server.shutdownWithCatalogs(Database.CLOSEMODE_IMMEDIATELY);
        // this.server.stop();
    }
}
