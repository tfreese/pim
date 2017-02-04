// Created: 12.01.2017
package de.freese.pim.core.persistence;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.mail.iap.ConnectionException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.JdbcTemplate<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 */
public class JdbcTemplate
{
    /**
     * @author Thomas Freese
     */
    private static class ColumnMapResultSetExtractor implements ResultSetExtractor<List<Map<String, Object>>>
    {
        /**
         * Erzeugt eine neue Instanz von {@link ColumnMapResultSetExtractor}
         */
        public ColumnMapResultSetExtractor()
        {
            super();
        }

        /**
         * @see de.freese.pim.core.persistence.ResultSetExtractor#extract(java.sql.ResultSet)
         */
        @Override
        public List<Map<String, Object>> extract(final ResultSet rs) throws SQLException
        {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Keys aufbauen
            String[] keys = new String[columnCount + 1];

            for (int i = 1; i <= columnCount; i++)
            {
                String key = getColumnName(rsmd, i);

                keys[i] = key;
            }

            List<Map<String, Object>> results = new ArrayList<>();

            while (rs.next())
            {
                Map<String, Object> map = new LinkedHashMap<>(columnCount);

                for (int i = 1; i <= columnCount; i++)
                {
                    String key = keys[i];
                    Object obj = getColumnValue(rs, i);

                    map.put(key, obj);
                }

                results.add(map);
            }

            return results;
        }

        /**
         * Ermittelt den Namen der Spalte am Index.
         *
         * @param resultSetMetaData {@link ResultSetMetaData}
         * @param index int
         * @return String
         * @throws SQLException Falls was schief geht.
         */
        private String getColumnName(final ResultSetMetaData resultSetMetaData, final int index) throws SQLException
        {
            String name = resultSetMetaData.getColumnLabel(index);

            if ((name == null) || (name.length() < 1))
            {
                name = resultSetMetaData.getColumnName(index);
            }

            return name.toUpperCase();
        }

        /**
         * Liefert das Value der Spalte am Index.
         *
         * @param rs {@link ResultSet}
         * @param index int
         * @return Object
         * @throws SQLException Falls was schief geht.
         */
        private Object getColumnValue(final ResultSet rs, final int index) throws SQLException
        {
            Object obj = rs.getObject(index);
            String className = null;

            if (obj != null)
            {
                className = obj.getClass().getName();
            }

            if (obj instanceof Blob)
            {
                Blob blob = (Blob) obj;
                obj = blob.getBytes(1, (int) blob.length());
            }
            else if (obj instanceof Clob)
            {
                Clob clob = (Clob) obj;
                obj = clob.getSubString(1, (int) clob.length());
            }
            else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))
            {
                obj = rs.getTimestamp(index);
            }
            else if ((className != null) && className.startsWith("oracle.sql.DATE"))
            {
                String metaDataClassName = rs.getMetaData().getColumnClassName(index);

                if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName))
                {
                    obj = rs.getTimestamp(index);
                }
                else
                {
                    obj = rs.getDate(index);
                }
            }
            else if (obj instanceof java.sql.Date)
            {
                if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index)))
                {
                    obj = rs.getTimestamp(index);
                }
            }

            return obj;
        }
    }

    /**
     * @author Thomas Freese
     * @param <T> Konkreter Row-Typ
     */
    private static class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>>
    {
        /**
         *
         */
        private final RowMapper<T> rowMapper;

        /**
         * Erzeugt eine neue Instanz von {@link RowMapperResultSetExtractor}
         *
         * @param rowMapper {@link RowMapper}
         */
        public RowMapperResultSetExtractor(final RowMapper<T> rowMapper)
        {
            super();

            Objects.requireNonNull(rowMapper, "rowMapper required");

            this.rowMapper = rowMapper;
        }

        /**
         * @see de.freese.pim.core.persistence.ResultSetExtractor#extract(java.sql.ResultSet)
         */
        @Override
        public List<T> extract(final ResultSet rs) throws SQLException
        {
            List<T> results = new ArrayList<>();
            int rowNum = 0;

            while (rs.next())
            {
                results.add(this.rowMapper.map(rs, rowNum++));
            }

            return results;
        }
    }

    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTemplate.class);

    /**
     *
     */
    private DataSource dataSource = null;

    /**
    *
    */
    private Function<String, String> sequenceFunction = null;

    /**
     * Erzeugt eine neue Instanz von {@link JdbcTemplate}
     */
    public JdbcTemplate()
    {
        super();
    }

    /**
     * Schliesst die {@link Connection}.
     *
     * @param connection {@link Connection}
     * @throws SQLException Falls was schief geht.
     */
    protected void closeConnection(final Connection connection) throws SQLException
    {
        if (ConnectionHolder.isEmpty())
        {
            // Kein Transaction-Context.
            // connection.setReadOnly(false);
            connection.close();
        }
        else
        {
            // Transaction-Context, nichts tun.
            // Wird vom TransactionalInvocationHandler erledigt.
        }
    }

    /**
     * Konvertiert bei Bedarf eine Exception.<br>
     * Default: Bei RuntimeException und SQLException wird jeweils der Cause geliefert.
     *
     * @param ex {@link Exception}
     * @return {@link Exception}
     */
    protected Exception convertException(final Exception ex)
    {
        Throwable th = ex;

        if (th instanceof RuntimeException)
        {
            th = th.getCause();
        }

        // if (th.getCause() instanceof SQLException)
        // {
        // th = th.getCause();
        // }

        return (Exception) th;
    }

    /**
     * Führt ein einfaches {@link Statement#execute(String)} aus.
     *
     * @param sql String
     * @throws SQLException Falls was schief geht.
     */
    public void execute(final String sql) throws SQLException
    {
        Connection connection = null;

        try
        {
            connection = getConnection();

            try (Statement stmt = connection.createStatement())
            {
                stmt.execute(sql);
            }
        }
        // catch (Exception ex)
        // {
        // throw convertException(ex);
        // }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * @return {@link Connection}
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected Connection getConnection() throws SQLException
    {
        Connection connection = null;

        if (ConnectionHolder.isEmpty())
        {
            // Kein Transaction-Context -> ReadOnly Connection
            connection = getDataSource().getConnection();
            // connection.setReadOnly(true);
        }
        else
        {
            // Transaction-Context
            connection = ConnectionHolder.get();

            if (connection.isReadOnly())
            {
                LoggerFactory.getLogger(getClass()).warn("connection is read-only");
            }
        }

        return connection;
    }

    /**
     * @return {@link DataSource}
     */
    public DataSource getDataSource()
    {
        Objects.requireNonNull(this.dataSource, "dataSource required");

        return this.dataSource;
    }

    /**
     * Liefert die nächste ID/PK der Sequence/Tabelle.<br>
     * Unterstützte Datenbanken:<br>
     *
     * <pre>
     * - Oracle: select SEQ.nextval from dual
     * - HSQLDB: call next value for SEQ
     * - Default: select count(*) + 1 from SEQ (Tabelle)
     * </pre>
     *
     * @param sequence String
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public synchronized long getNextSequenceID(final String sequence) throws SQLException
    {
        Connection connection = null;

        try
        {
            connection = getConnection();

            return getNextSequenceID(sequence, connection);
        }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Liefert die nächste ID/PK der Sequence/Tabelle.<br>
     * Unterstützte Datenbanken:<br>
     *
     * <pre>
     * - Oracle: select SEQ.nextval from dual
     * - HSQLDB: call next value for SEQ
     * - Default: select count(*) + 1 from SEQ (Tabelle)
     * </pre>
     *
     * @param sequence String
     * @param connection {@link Connection}
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    public synchronized long getNextSequenceID(final String sequence, final Connection connection) throws SQLException
    {
        if (this.sequenceFunction == null)
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
                    // this.sequenceFunction = seq -> "select nvl(max(id), 0) + 1 from " + seq;
                    this.sequenceFunction = seq -> "select count(*) + 1 from " + seq;

                    String msg = String.format("%s: use following sql for sequence \"%s\"%n", this.sequenceFunction.apply("<SEQ/TABLE>"),
                            dbmd.getDatabaseProductName());
                    LOGGER.warn(msg);
            }
        }

        long id = 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(this.sequenceFunction.apply(sequence)))
        {
            rs.next();
            id = rs.getLong(1);
        }

        return id;
    }

    /**
     * Abfrage der {@link DatabaseMetaData}.
     *
     * @return boolean
     * @throws SQLException Falls was schief geht.
     */
    protected boolean isBatchSupported() throws SQLException
    {
        try (Connection connection = getDataSource().getConnection())
        {
            return isBatchSupported(connection);
        }
    }

    /**
     * Abfrage der {@link DatabaseMetaData}.
     *
     * @param connection {@link ConnectionException}
     * @return boolean
     * @throws SQLException Falls was schief geht.
     */
    protected boolean isBatchSupported(final Connection connection) throws SQLException
    {
        DatabaseMetaData dbmd = connection.getMetaData();

        return dbmd.supportsBatchUpdates();
    }

    /**
     * Extrahiert ein Objekt aus dem {@link ResultSet}.
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     * @param rse {@link ResultSetExtractor}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public <T> T query(final String sql, final PreparedStatementSetter setter, final ResultSetExtractor<T> rse) throws SQLException
    {
        Connection connection = null;

        try
        {
            connection = getConnection();
            T result = null;

            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.clearParameters();

                setter.setValues(ps);

                try (ResultSet rs = ps.executeQuery())
                {
                    result = rse.extract(rs);
                }
            }

            return result;
        }
        // catch (Exception ex)
        // {
        // throw convertException(ex);
        // }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     * @param rowMapper {@link RowMapper}
     * @return {@link List}
     * @throws SQLException Falls was schief geht.
     */
    public <T> List<T> query(final String sql, final PreparedStatementSetter setter, final RowMapper<T> rowMapper) throws SQLException
    {
        return query(sql, setter, new RowMapperResultSetExtractor<>(rowMapper));
    }

    /**
     * Extrahiert ein Objekt aus dem {@link ResultSet}.
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rse {@link ResultSetExtractor}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws SQLException
    {
        Connection connection = null;

        try
        {
            connection = getConnection();
            T result = null;

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql))
            {
                result = rse.extract(rs);
            }

            return result;
        }
        // catch (Exception ex)
        // {
        // throw convertException(ex);
        // }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @return {@link List}
     * @throws SQLException Falls was schief geht.
     */
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) throws SQLException
    {
        return query(sql, new RowMapperResultSetExtractor<>(rowMapper));
    }

    /**
     * Liefert eine Liste aus Maps.<br>
     *
     * @param sql String
     * @return {@link List}
     * @throws SQLException Falls was schief geht.
     */
    public List<Map<String, Object>> queryForList(final String sql) throws SQLException
    {
        return query(sql, new ColumnMapResultSetExtractor());
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link JdbcTemplate}
     */
    public JdbcTemplate setDataSource(final DataSource dataSource)
    {
        Objects.requireNonNull(dataSource, "dataSource required");

        this.dataSource = dataSource;

        return this;
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @return int; affectedRows
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public int update(final String sql) throws SQLException
    {
        Connection connection = null;

        try
        {
            connection = getConnection();
            int affectedRows = 0;

            try (Statement stmt = connection.createStatement())
            {
                affectedRows = stmt.executeUpdate(sql);
            }

            return affectedRows;
        }
        // catch (Exception ex)
        // {
        // throw convertException(ex);
        // }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     * @return int; affectedRows
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public int update(final String sql, final PreparedStatementSetter setter) throws SQLException
    {
        Connection connection = null;

        try
        {
            connection = getConnection();
            int affectedRows = 0;

            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.clearParameters();

                setter.setValues(ps);

                affectedRows = ps.executeUpdate();
            }

            return affectedRows;
        }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * Führt ein {@link Statement#executeBatch()} aus (INSERT, UPDATE, DELETE).
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param batchArgs {@link Collection}
     * @param setter {@link ParameterizedPreparedStatementSetter}
     * @return int[]; affectedRows
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public <T> int[] updateBatch(final String sql, final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> setter) throws SQLException
    {
        List<int[]> rowsAffected = new ArrayList<>();
        Connection connection = null;
        boolean supportsBatch = isBatchSupported();

        try
        {
            connection = getConnection();

            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                for (T arg : batchArgs)
                {
                    ps.clearParameters();

                    setter.setValues(ps, arg);

                    if (supportsBatch)
                    {
                        ps.addBatch();
                    }
                    else
                    {
                        // Batch nicht möglich -> direkt ausführen.
                        int i = ps.executeUpdate();
                        rowsAffected.add(new int[]
                        {
                                i
                        });
                    }
                }

                if (supportsBatch)
                {
                    rowsAffected.add(ps.executeBatch());
                }
            }

            return rowsAffected.stream().flatMapToInt(af -> IntStream.of(af)).toArray();
        }
        finally
        {
            closeConnection(connection);
        }
    }
}
