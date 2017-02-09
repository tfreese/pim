// Created: 15.12.2016
package de.freese.pim.core.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import de.freese.pim.core.jdbc.SimpleDataSource;
import de.freese.pim.core.service.ISettingsService;

/**
 * Basis-implementierung einer {@link IDataSourceBean}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDataSourceBean implements IDataSourceBean
{
    /**
    *
    */
    private static final String DB_NAME = "pimdb";

    /**
    *
    */
    protected static String HSQLDB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    /**
     *
     */
    protected static String HSQLDB_VALIDATION_QUERY = "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";

    /**
     *
     */
    private DataSource dataSource = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractDataSourceBean}
     */
    public AbstractDataSourceBean()
    {
        super();
    }

    /**
     * Erzeugt die {@link DataSource}.<br>
     * <a href="http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html#Common_Attributes">Tomcat Common Attributes</a>
     *
     * @param driver String
     * @param url String
     * @param username String
     * @param password String
     * @param validationQuery String
     * @return {@link DataSource}
     */
    protected DataSource createDataSource(final String driver, final String url, final String username, final String password, final String validationQuery)
    {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(driver);
        poolProperties.setUrl(url);
        poolProperties.setUsername(username);
        poolProperties.setPassword(password);

        poolProperties.setMaxActive(ISettingsService.MAX_ACTIVE_CONNECTIONS.get());
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
     * @see de.freese.pim.core.db.IDataSourceBean#disconnect()
     */
    @Override
    public void disconnect() throws Exception
    {
        DataSource dataSource = getDataSource();

        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource)
        {
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close(true);
        }
        else if (dataSource instanceof SimpleDataSource)
        {
            ((SimpleDataSource) dataSource).close();
        }

        dataSource = null;
        setDataSource(null);
    }

    /**
     * @see de.freese.pim.core.db.IDataSourceBean#getDataSource()
     */
    @Override
    public DataSource getDataSource()
    {
        return this.dataSource;
    }

    /**
     * Liefert den DB-Namen.
     *
     * @return String
     */
    protected String getDBName()
    {
        return DB_NAME;
    }

    /**
     * Liefert den Pfad zur Datenbank.
     *
     * @param home {@link Path}
     * @return {@link Path}
     */
    protected Path getDBPath(final Path home)
    {
        return home.resolve(getDBName());
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

    /**
     * Befüllt die Datenbank, wenn diese noch leer ist.
     *
     * @param dataSource {@link DataSource}
     * @param populateCallback {@link Runnable}; optional, wird vor dem populate aufgerufen
     * @param scripts String[]
     * @throws Exception Falls was schief geht.
     */
    protected void populateIfEmpty(final DataSource dataSource, final Runnable populateCallback, final String...scripts) throws Exception
    {
        String[] types = new String[]
        {
                "TABLE"
        }; // "VIEW"

        try (Connection connection = dataSource.getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, "SETTINGS", types))
        {
            if (!resultSet.next())
            {
                // Tabelle nicht vorhanden.
                // String tableName = resultSet.getString("TABLE_NAME");

                if (populateCallback != null)
                {
                    populateCallback.run();
                }

                DatabasePopulator populator = new DatabasePopulator();

                for (String script : scripts)
                {
                    populator.addScript(script);
                }

                populator.populate(dataSource);
            }
        }
    }

    /**
     * Setzt die {@link DataSource}.
     *
     * @param dataSource {@link DataSource}
     */
    protected void setDataSource(final DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * @see de.freese.pim.core.db.IDataSourceBean#testConnection()
     */
    @Override
    public void testConnection() throws Exception
    {
        testConnection(getDataSource(), getValidationQuery());
    }

    /**
     * Test der DB-Connection.
     *
     * @param dataSource {@link DataSource}
     * @param validationQuery String
     * @throws Exception Falls was schief geht.
     */
    protected void testConnection(final DataSource dataSource, final String validationQuery) throws Exception
    {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(validationQuery))
        {
            boolean valid = resultSet.next();

            if (!valid)
            {
                throw new IllegalStateException("Test failed: result not valid");
            }
            // int i = resultSet.getInt(1);
            //
            // if (i != 1)
            // {
            // throw new IllegalStateException("Test failed: i != 1: " + i);
            // }
        }
    }
}
