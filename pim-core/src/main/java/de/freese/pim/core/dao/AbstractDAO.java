/**
 * Created: 20.01.2017
 */

package de.freese.pim.core.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.pim.core.persistence.JdbcTemplate;

/**
 * Basis-Implementierung eines DAOs.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDAO
{
    /**
    *
    */
    private JdbcTemplate jdbcTemplate = null;

    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private Function<String, String> sequenceFunction = null;

    /**
     * Erstellt ein neues {@link AbstractDAO} Object.
     */
    public AbstractDAO()
    {
        super();
    }

    /**
     * Erkennt den DB-Typ für die Definition der Sequence-Function.<br>
     * see org.springframework.data.jdbc.support.DatabaseType
     *
     * @param dataSource {@link DataSource}
     */
    protected void detectDatabaseType(final DataSource dataSource)
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            String product = dbmd.getDatabaseProductName().toLowerCase();
            product = product.split(" ")[0];
            // int majorVersion = dbmd.getDatabaseMajorVersion();
            // int minorVersion = dbmd.getDatabaseMinorVersion();

            switch (product)
            {
                case "oracle":
                    this.sequenceFunction = seq -> "select " + seq + ".nextval from dual";
                    break;
                case "hsql":
                    this.sequenceFunction = seq -> "call next value for " + seq;
                    break;
                // case "mysql":
                // // CREATE TABLE sequence (id INT NOT NULL);
                // // INSERT INTO sequence VALUES (0);
                //
                // this.sequenceFunction = seq -> "UPDATE sequence SET id=LAST_INSERT_ID(id + 1); SELECT LAST_INSERT_ID();";
                // break;

                default:
                    this.sequenceFunction = seq -> "select nvl(max(id), 0) + 1 from " + seq;

                    String msg = String.format("%s: use following sql for sequence \"%s\"%n", this.sequenceFunction.apply("<SEQ/TABLE>"),
                            dbmd.getDatabaseProductName());
                    getLogger().warn(msg);
            }
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
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

    /**
     * Liefert die nächste ID/PK der Sequence/Tabelle.
     *
     * @param sequence String
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    protected long getNextID(final String sequence) throws SQLException
    {
        long id = getJdbcTemplate().query(this.sequenceFunction.apply(sequence), rs -> {
            rs.next();

            return rs.getLong(1);
        });

        return id;
    }

    /**
     * Ersetzt ggf. ein vorhandenes {@link JdbcTemplate}.
     *
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource)
    {
        Objects.requireNonNull(dataSource, "dataSource required");

        this.jdbcTemplate = new JdbcTemplate().setDataSource(dataSource);

        detectDatabaseType(dataSource);
    }

    /**
     * @param jdbcTemplate {@link JdbcTemplate}
     */
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate)
    {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate required");

        this.jdbcTemplate = jdbcTemplate;

        detectDatabaseType(jdbcTemplate.getDataSource());
    }
}
