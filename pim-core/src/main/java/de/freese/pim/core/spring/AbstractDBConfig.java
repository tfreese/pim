/**
 * Created: 10.02.2017
 */

package de.freese.pim.core.spring;

import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import de.freese.pim.core.jdbc.SimpleDataSource;

/**
 * Basis-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDBConfig
{
    /**
    *
    */
    protected static String HSQLDB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    /**
     *
     */
    protected static String HSQLDB_VALIDATION_QUERY = "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";

    /**
     * Erstellt ein neues {@link AbstractDBConfig} Object.
     */
    public AbstractDBConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     */
    protected void close(final DataSource dataSource)
    {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource)
        {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close(true);
        }
        else if (dataSource instanceof SingleConnectionDataSource)
        {
            ((SingleConnectionDataSource) dataSource).destroy();
        }
        else if (dataSource instanceof SimpleDataSource)
        {
            ((SimpleDataSource) dataSource).destroy();
        }
    }

    /**
     * Erzeugt die {@link DataSource}.<br>
     * <a href="http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html#Common_Attributes">Tomcat Common Attributes</a>
     *
     * @param driver String
     * @param url String
     * @param username String
     * @param password String
     * @param maxActive int
     * @param validationQuery String
     * @return {@link DataSource}
     */
    protected DataSource createDataSource(final String driver, final String url, final String username, final String password, final int maxActive,
                                          final String validationQuery)
    {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(driver);
        poolProperties.setUrl(url);
        poolProperties.setUsername(username);
        poolProperties.setPassword(password);

        poolProperties.setMaxActive(maxActive);
        poolProperties.setMaxIdle(1);
        poolProperties.setMinIdle(1);
        poolProperties.setInitialSize(1);
        poolProperties.setMaxWait(10 * 1000); // max. 10 Sekunden warten auf Connection

        poolProperties.setDefaultAutoCommit(Boolean.TRUE);
        poolProperties.setDefaultReadOnly(Boolean.FALSE);

        if (StringUtils.isNotBlank(validationQuery))
        {
            poolProperties.setValidationQuery(validationQuery);
            poolProperties.setValidationInterval(30 * 1000L); // Wurde eine Connection vor 30 Sekunden validiert, nicht nochmal validieren

            poolProperties.setTestWhileIdle(true); // Idle-Connections prüfen
            poolProperties.setTestOnBorrow(true); // Validation für Connections die geholt werden
            poolProperties.setTestOnReturn(false); // Validation für Connections die zurückgegeben werden
        }

        poolProperties.setMinEvictableIdleTimeMillis(60 * 1000); // 60 Sekunden: Zeit nach der eine Connection als "Idle" markiert wird
        poolProperties.setTimeBetweenEvictionRunsMillis(60 * 1000); // Alle 60 Sekunden auf Idle-Connections prüfen
        poolProperties.setMaxAge(1 * 60 * 60 * 1000); // Eine Connection darf max. 1 Stunde alt werden

        poolProperties.setRemoveAbandoned(true); // Entfernen von verwaisten (Timeout) Connections/Langläufern
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(10 * 60); // Nach 10 Minuten Connections/Langläufer als verwaist markieren
        // poolProperties.setAbandonWhenPercentageFull(50); // Entfernen von verwaisten (Timeout) Connections/Langläufer erst ab Poolstand

        String jdbcInterceptors = "";

        // Caching für die Attribute autoCommit, readOnly, transactionIsolation und catalog.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;";

        // Jede Query bei Langläufern setzt den Abandon-Timer zurück.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;";

        // Schliesst alle Statments, die durch createStatement, prepareStatement oder prepareCall erzeugt wurden, falls nötig.
        jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

        poolProperties.setJdbcInterceptors(jdbcInterceptors);

        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);

        return dataSource;
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link Flyway}
     */
    @Bean(initMethod = "migrate")
    @DependsOn("dataSource")
    public Flyway flyway(final DataSource dataSource)
    {
        Flyway flyway = new Flyway();
        flyway.setEncoding("UTF-8");
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource);
        // flyway.setLocations("filesystem:/path/to/migrations/");
        flyway.setLocations("classpath:db/hsqldb");

        return flyway;
    }

    /**
     * Liefert den Namen der Datenbank.
     *
     * @return String
     */
    protected String getDatabaseName()
    {
        return "pimdb";
    }

    /**
     * Liefert den DB-Treiber.
     *
     * @return String
     */
    protected abstract String getDriver();

    /**
     * Liefert die ValidationQuery.
     *
     * @return String; optional
     */
    protected abstract String getValidationQuery();

    // /**
    // * Befüllt die Datenbank, wenn diese noch leer ist.
    // *
    // * @param dataSource {@link DataSource}
    // * @param populateCallback {@link Runnable}; optional, wird vor dem populate aufgerufen
    // * @param scripts String[]
    // * @throws Exception Falls was schief geht.
    // */
    // protected void populateIfEmpty(final DataSource dataSource, final Runnable populateCallback, final String...scripts) throws Exception
    // {
    // String[] types = new String[]
    // {
    // "TABLE"
    // }; // "VIEW"
    //
    // try (Connection connection = dataSource.getConnection();
    // ResultSet resultSet = connection.getMetaData().getTables(null, null, "SETTINGS", types))
    // {
    // if (!resultSet.next())
    // {
    // // Tabelle nicht vorhanden.
    // // String tableName = resultSet.getString("TABLE_NAME");
    //
    // if (populateCallback != null)
    // {
    // populateCallback.run();
    // }
    //
    // DatabasePopulator populator = new DatabasePopulator();
    //
    // for (String script : scripts)
    // {
    // populator.addScript(script);
    // }
    //
    // populator.populate(dataSource);
    // }
    // }
    // }

    /**
     * Beendet alle Verbindungen und schliesst die {@link DataSource}.
     *
     * @throws Exception Falls was schief geht.
     */
    public abstract void preDestroy() throws Exception;

}
