/**
 * Created: 10.02.2017
 */

package de.freese.pim.server.spring.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.DisposableBean;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Basis-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDBConfig
{
    /**
     * Erstellt ein neues {@link AbstractDBConfig} Object.
     */
    protected AbstractDBConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     * @throws Exception Falls was schief geht.
     * @deprecated Siehe misc-jsensors: HsqldbServerAutoConfiguration#hsqldbServer
     */
    @Deprecated
    protected void close(final DataSource dataSource) throws Exception
    {
        if (dataSource instanceof DisposableBean)
        {
            ((DisposableBean) dataSource).destroy();
        }
        else if (dataSource instanceof HikariDataSource)
        {
            ((HikariDataSource) dataSource).close();
        }
        // else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource)
        // {
        // ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close(true);
        // }
    }

    // /**
    // * Erzeugt die {@link DataSource}.<br>
    // * <a href="http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html#Common_Attributes">Tomcat Common Attributes</a>
    // *
    // * @param driver String
    // * @param url String
    // * @param username String
    // * @param password String
    // * @param maxActive int
    // * @param validationQuery String
    // * @return {@link DataSource}
    // */
    // protected DataSource createDataSource(final String driver, final String url, final String username, final String password, final int maxActive,
    // final String validationQuery)
    // {
    // PoolProperties poolProperties = new PoolProperties();
    // poolProperties.setDriverClassName(driver);
    // poolProperties.setUrl(url);
    // poolProperties.setUsername(username);
    // poolProperties.setPassword(password);
    //
    // poolProperties.setMaxActive(maxActive);
    // poolProperties.setMaxIdle(1);
    // poolProperties.setMinIdle(1);
    // poolProperties.setInitialSize(1);
    // poolProperties.setMaxWait(10 * 1000); // max. 10 Sekunden warten auf Connection.
    //
    // poolProperties.setDefaultAutoCommit(Boolean.TRUE);
    // poolProperties.setDefaultReadOnly(Boolean.FALSE);
    //
    // if (StringUtils.isNotBlank(validationQuery))
    // {
    // poolProperties.setValidationQuery(validationQuery);
    // poolProperties.setValidationQueryTimeout(2); // Nach 2 Sekunden wird die ValidationQuery als ungültig interpretiert.
    // poolProperties.setValidationInterval(30 * 1000L); // Wurde eine Connection vor 30 Sekunden validiert, nicht nochmal validieren.
    //
    // poolProperties.setTestWhileIdle(true); // Connections prüfen, die IDLE sind.
    // poolProperties.setTestOnBorrow(true); // Connections prüfen, die geholt werden.
    // poolProperties.setTestOnReturn(false); // Connections prüfen, die zurückgegeben werden.
    // }
    //
    // poolProperties.setMinEvictableIdleTimeMillis(60 * 1000); // Nach 60 Sekunden eine Connection als "Idle" markieren.
    // poolProperties.setTimeBetweenEvictionRunsMillis(60 * 1000); // Alle 60 Sekunden auf Idle-Connections prüfen.
    // poolProperties.setMaxAge(1 * 60 * 60 * 1000); // Eine Connection darf max. 1 Stunde alt werden, 0 = keine Alterung.
    //
    // poolProperties.setRemoveAbandoned(true); // Entfernen von verwaisten (Timeout) Connections/Langläufern.
    // poolProperties.setLogAbandoned(true);
    // poolProperties.setRemoveAbandonedTimeout(10 * 60); // Nach 10 Minuten Connections/Langläufer als verwaist markieren.
    // // poolProperties.setAbandonWhenPercentageFull(50); // Entfernen von verwaisten (Timeout) Connections/Langläufer erst ab Poolstand.
    //
    // String jdbcInterceptors = "";
    //
    // // Caching für die Attribute autoCommit, readOnly, transactionIsolation und catalog.
    // jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;";
    //
    // // Jede Query bei Langläufern setzt den Abandon-Timer zurück.
    // jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;";
    //
    // // Schliesst alle Statments, die durch createStatement, prepareStatement oder prepareCall erzeugt wurden, falls nötig.
    // jdbcInterceptors += "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";
    //
    // poolProperties.setJdbcInterceptors(jdbcInterceptors);
    //
    // DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
    //
    // return dataSource;
    // }

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
