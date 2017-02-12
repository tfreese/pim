/**
 * Created: 20.01.2017
 */

package de.freese.pim.core.dao;

import java.util.Objects;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.common.jdbc.JdbcTemplate;

/**
 * Basis-Implementierung eines DAOs.
 *
 * @author Thomas Freese
 * @param <D> Konkreter DAO-Typ für Builder-Pattern
 */
public abstract class AbstractDAO<D>
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
     * Erstellt ein neues {@link AbstractDAO} Object.
     */
    public AbstractDAO()
    {
        super();
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
        Objects.requireNonNull(this.jdbcTemplate, "jdbcTemplate required");

        return this.jdbcTemplate;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
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

        setJdbcTemplate(new JdbcTemplate().dataSource(dataSource));
    }

    /**
     * Setzt das {@link JdbcTemplate}.
     *
     * @param jdbcTemplate {@link JdbcTemplate}
     */
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate)
    {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate required");

        this.jdbcTemplate = jdbcTemplate;

        // detectDatabaseType(jdbcTemplate.getDataSource());
    }
}
