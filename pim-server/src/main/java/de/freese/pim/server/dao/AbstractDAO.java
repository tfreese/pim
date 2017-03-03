/**
 * Created: 20.01.2017
 */

package de.freese.pim.server.dao;

import java.util.Objects;
import java.util.concurrent.Semaphore;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import de.freese.pim.server.jdbc.JdbcTemplate;

/**
 * Basis-Implementierung eines DAOs.
 *
 * @author Thomas Freese
 * @param <D> Konkreter DAO-Typ für Builder-Pattern
 */
public abstract class AbstractDAO<D> implements InitializingBean
{
    /**
     *
     */
    private JdbcTemplate jdbcTemplate = null;

    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    // /**
    // *
    // */
    // private Function<String, String> sequenceFunction = null;

    /**
     *
     */
    private int maxConnections = 0;

    /**
     * Erstellt ein neues {@link AbstractDAO} Object.
     */
    public AbstractDAO()
    {
        super();
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Objects.requireNonNull(this.jdbcTemplate, "jdbcTemplate required");

        if (this.maxConnections > 0)
        {
            this.jdbcTemplate.setMaxConnections(this.maxConnections);
        }

        this.jdbcTemplate.afterPropertiesSet();
    }

    /**
     * Ersetzt ggf. ein vorhandenes {@link JdbcTemplate}.
     *
     * @param dataSource {@link DataSource}
     * @return {@link AbstractDAO}: Konkretes DAO für Builder-Pattern
     */
    @SuppressWarnings("unchecked")
    public D dataSource(final DataSource dataSource)
    {
        setDataSource(dataSource);

        return (D) this;
    }

    /**
     * Setzt das {@link JdbcTemplate}.
     *
     * @param jdbcTemplate {@link JdbcTemplate}
     * @return {@link AbstractDAO}: Konkretes DAO für Builder-Pattern
     */
    @SuppressWarnings("unchecked")
    public D jdbcTemplate(final JdbcTemplate jdbcTemplate)
    {
        setJdbcTemplate(jdbcTemplate);

        return (D) this;
    }

    /**
     * Ersetzt ggf. ein vorhandenes {@link JdbcTemplate}.
     *
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource)
    {
        Objects.requireNonNull(dataSource, "dataSource required");

        setJdbcTemplate(new JdbcTemplate(dataSource));
    }

    /**
     * Setzt das {@link JdbcTemplate}.
     *
     * @param jdbcTemplate {@link JdbcTemplate}
     */
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Erstellt einen {@link Semaphore} im {@link JdbcTemplate}, der den Zugriff auf die {@link DataSource} reguliert.
     *
     * @param maxConnections int
     */
    @Value("${spring.datasource.tomcat.maxActive:1}")
    public void setMaxConnections(final int maxConnections)
    {
        this.maxConnections = maxConnections;
    }

    // /**
    // * Liefert die nächste ID/PK der Sequence/Tabelle.
    // *
    // * @param sequence String
    // * @return long
    // * @throws SQLException Falls was schief geht.
    // */
    // protected long getNextID(final String sequence) throws SQLException
    // {
    // long id = getJdbcTemplate().query(this.sequenceFunction.apply(sequence), rs -> {
    // rs.next();
    //
    // return rs.getLong(1);
    // });
    //
    // return id;
    // }

    /**
     * @return {@link DataSource}
     */
    protected DataSource getDataSource()
    {
        return getJdbcTemplate().getDataSource();
    }

    /**
     * @return {@link JdbcTemplate}
     */
    protected JdbcTemplate getJdbcTemplate()
    {
        return this.jdbcTemplate;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
