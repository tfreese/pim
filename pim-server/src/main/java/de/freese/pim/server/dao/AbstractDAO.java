/**
 * Created: 20.01.2017
 */

package de.freese.pim.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Basis-Implementierung eines DAOs.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDAO implements InitializingBean
{
    /**
     *
     */
    private JdbcTemplate jdbcTemplate;

    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    // /**
    // *
    // */
    // private SequenceQueryExecutor sequenceQueryExecutor;

    /**
     *
     */
    private Function<String, String> sequenceQuery;

    /**
     * Erstellt ein neues {@link AbstractDAO} Object.
     */
    protected AbstractDAO()
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
        Objects.requireNonNull(this.sequenceQuery, "sequenceQuery required");

        // try (Connection connection = this.jdbcTemplate.getDataSource().getConnection())
        // {
        // SequenceQuery sequenceQuery = SequenceQuery.determineQuery(connection);
        // this.sequenceQueryExecutor = new SequenceQueryExecutor(sequenceQuery);
        // }
        //
        // Objects.requireNonNull(this.sequenceQueryExecutor, "sequenceQueryExecutor required");
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

    // /**
    // * Erstellt einen {@link Semaphore} im {@link JdbcTemplate}, der den Zugriff auf die {@link DataSource} reguliert.
    // *
    // * @param maxConnections int
    // */
    // @Value("${spring.datasource.tomcat.maxActive:1}")
    // public void setMaxConnections(final int maxConnections)
    // {
    // this.maxConnections = maxConnections;
    // }

    /**
     * Liefert die nächste ID/PK der Sequence/Tabelle.
     *
     * @param sequence String
     * @return long
     */
    protected long getNextID(final String sequence)
    {
        String sql = this.sequenceQuery.apply(sequence);

        try (Connection connection = getJdbcTemplate().getDataSource().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            rs.next();
            long id = rs.getLong(1);

            return id;
        }
        catch (SQLException sex)
        {
            throw new DataRetrievalFailureException("", sex);
        }
    }

    /**
     * Ersetzt ggf. ein vorhandenes {@link JdbcTemplate}.
     *
     * @param dataSource {@link DataSource}
     */
    @Resource
    public void setDataSource(final DataSource dataSource)
    {
        Objects.requireNonNull(dataSource, "dataSource required");

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * SQL für Sequenz-Abfragen.
     *
     * @param sequenceQuery {@link UnaryOperator}
     */
    @Resource
    public void setSequenceQuery(final UnaryOperator<String> sequenceQuery)
    {
        this.sequenceQuery = Objects.requireNonNull(sequenceQuery, "sequenceQuery required");
    }
}
