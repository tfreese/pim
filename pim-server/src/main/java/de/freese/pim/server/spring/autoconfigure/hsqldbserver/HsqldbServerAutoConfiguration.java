/**
 * Created: 13.02.2017
 */

package de.freese.pim.server.spring.autoconfigure.hsqldbserver;

import java.util.List;
import javax.annotation.Resource;
import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.freese.pim.server.spring.autoconfigure.hsqldbserver.HsqldbServerProperties.DB;

/**
 * AutoConfiguration für ein HSQLDB-{@link Server}.<br>
 * Nur wenn noch kein {@link Server} vorhanden ist, wird ein {@link Server} erzeugt.<br>
 * Wird VOR der {@link DataSourceAutoConfiguration} ausgeführt.
 *
 * @author Thomas Freese
 */
@Configuration
@ConditionalOnClass(Server.class) // Nur wenn HSQLDB auch im Classpath ist.
@ConditionalOnMissingBean(Server.class) // Nur wenn Server noch nicht im SpringContext ist.
@ConditionalOnProperty(prefix = "hsqldb.server", name = "enabled", matchIfMissing = false) // Nur wenn auch enabled.
@EnableConfigurationProperties(HsqldbServerProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class HsqldbServerAutoConfiguration
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(HsqldbServerAutoConfiguration.class);

    /**
    *
    */
    @Resource
    private HsqldbServerProperties serverProperties = null;

    /**
     * Erstellt ein neues {@link HsqldbServerAutoConfiguration} Object.
     */
    public HsqldbServerAutoConfiguration()
    {
        super();
    }

    /**
     * @return {@link Server}
     * @throws Exception Falls was schief geht.
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    // @Scope(ConfigurableBeanFactory#SCOPE_SINGLETON)
    public Server hsqldbServer() throws Exception
    {
        int port = this.serverProperties.getPort();
        boolean noSystemExit = this.serverProperties.isNoSystemExit();
        boolean silent = this.serverProperties.isSilent();
        boolean trace = this.serverProperties.isTrace();
        List<DB> dbs = this.serverProperties.getDb();

        StringBuilder sb = new StringBuilder();
        sb.append("Create HsqldbServer with:");
        sb.append(" port={}");
        sb.append(", noSystemExit={}");
        sb.append(", silent={}");
        sb.append(", trace={}");
        sb.append(", dataBases={}");

        LOGGER.info(sb.toString(), port, noSystemExit, silent, trace, dbs);

        Server server = new Server();
        server.setLogWriter(null); // can use custom writer
        server.setErrWriter(null); // can use custom writer
        // server.setLogWriter(new PrintWriter(System.out)); // can use custom writer
        // server.setErrWriter(new PrintWriter(System.err)); // can use custom writer
        server.setNoSystemExit(noSystemExit);
        server.setSilent(silent);
        server.setTrace(trace);

        // server.setAddress("0.0.0.0");
        server.setPort(port);

        for (int i = 0; i < dbs.size(); i++)
        {
            DB db = dbs.get(i);

            server.setDatabaseName(i, db.getName());
            server.setDatabasePath(i, db.getPath());
        }

        // HsqlProperties p = new HsqlProperties();
        // p.setProperty("server.database.0", "file:/" + dbPath);
        // p.setProperty("server.dbname.0", getDBName());
        // p.setProperty("server.port", DB_PORT);
        // server.setProperties(p);

        // CREATE USER EFREEST PASSWORD 'EFREEST'
        // CREATE USER SA PASSWORD DIGEST 'd41d8cd98f00b204e9800998ecf8427e'
        // ALTER USER SA SET LOCAL TRUE
        // GRANT DBA TO SA

        return server;
    }
}
