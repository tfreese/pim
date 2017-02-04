/**
 * Created: 04.02.2017
 */

package de.freese.pim.core.jdbc.sequence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.sql.DataSource;
import com.sun.mail.iap.ConnectionException;

/**
 * Liefert den nächsten Wert einer Sequence.
 *
 * @author Thomas Freese
 */
public class SequenceQueryExecutor
{
    /**
     *
     */
    private final SequenceQuery sequenceQuery;

    /**
     * Erstellt ein neues {@link SequenceQueryExecutor} Object.
     *
     * @param sequenceQuery {@link SequenceQuery}
     */
    public SequenceQueryExecutor(final SequenceQuery sequenceQuery)
    {
        super();

        Objects.requireNonNull(sequenceQuery, "sequenceQuery required");

        this.sequenceQuery = sequenceQuery;
    }

    /**
     * @param sequence String
     * @param connection {@link ConnectionException}
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    public long getNextID(final String sequence, final Connection connection) throws SQLException
    {
        long id = 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(this.sequenceQuery.apply(sequence)))
        {
            rs.next();
            id = rs.getLong(1);
        }

        return id;
    }

    /**
     * @param sequence String
     * @param dataSource {@link DataSource}
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    public long getNextID(final String sequence, final DataSource dataSource) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            return getNextID(sequence, connection);
        }
    }
}
