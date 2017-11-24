// Created: 12.01.2017
package de.freese.pim.server.jdbc;

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
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceUtils;
import de.freese.pim.server.jdbc.sequence.SequenceProvider;
import de.freese.pim.server.jdbc.sequence.SequenceQuery;
import de.freese.pim.server.jdbc.sequence.SequenceQueryExecutor;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.JdbcTemplate.<br>
 *
 * @author Thomas Freese
 */
public class JdbcTemplate implements InitializingBean
{
    /**
     * @author Thomas Freese
     */
    public static class ColumnMapResultSetExtractor implements ResultSetExtractor<List<Map<String, Object>>>
    {
        /**
         * Erzeugt eine neue Instanz von {@link ColumnMapResultSetExtractor}
         */
        public ColumnMapResultSetExtractor()
        {
            super();
        }

        /**
         * @see de.freese.pim.server.jdbc.ResultSetExtractor#extract(java.sql.ResultSet)
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
    public static class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>>
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

            this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        }

        /**
         * @see de.freese.pim.server.jdbc.ResultSetExtractor#extract(java.sql.ResultSet)
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
    private Semaphore connectionSemaphore = null;

    /**
     *
     */
    private DataSource dataSource = null;

    /**
    *
    */
    private int maxConnections = 0;

    /**
    *
    */
    private final ReentrantLock reentrantLockSequence = new ReentrantLock();

    /**
     *
     */
    private SequenceQueryExecutor sequenceQueryExecutor = null;

    /**
     * Erzeugt eine neue Instanz von {@link JdbcTemplate}
     */
    public JdbcTemplate()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link JdbcTemplate}
     *
     * @param dataSource {@link DataSource}
     */
    public JdbcTemplate(final DataSource dataSource)
    {
        super();

        setDataSource(dataSource);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Objects.requireNonNull(this.dataSource, "dataSource required");

        if (this.maxConnections > 0)
        {
            this.connectionSemaphore = new Semaphore(this.maxConnections, true);
        }
    }

    /**
     * Schliesst die {@link Connection}.
     *
     * @param connection {@link Connection}
     */
    protected void closeConnection(final Connection connection)
    {
        try
        {
            DataSourceUtils.releaseConnection(connection, getDataSource());
        }
        finally
        {
            connectionSemaphoreRelease();
        }
    }

    /**
     * @see Semaphore#acquire()
     */
    protected void connectionSemaphoreAcquire()
    {
        if (getConnectionSemaphore() != null)
        {
            try
            {
                getConnectionSemaphore().acquire();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }
    }

    /**
     * @see Semaphore#acquire()
     */
    protected void connectionSemaphoreRelease()
    {
        if (getConnectionSemaphore() != null)
        {
            getConnectionSemaphore().release();
        }
    }

    /**
     * Konvertiert bei Bedarf eine Exception.<br>
     * Default: Bei RuntimeException und SQLException wird jeweils der Cause geliefert.
     *
     * @param ex {@link Exception}
     * @return {@link RuntimeException}
     */
    protected RuntimeException convertException(final Exception ex)
    {
        // if (ex instanceof RuntimeException)
        // {
        // return (RuntimeException) ex;
        // }

        Throwable th = ex;

        if (th instanceof RuntimeException)
        {
            th = th.getCause();
        }

        if (th.getCause() instanceof SQLException)
        {
            th = th.getCause();
        }

        return new RuntimeException(th);
    }

    /**
     * @param <T> Konkreter Return-Typ.
     * @param action {@link ConnectionCallback}
     * @return Object
     */
    @SuppressWarnings("resource")
    public <T> T execute(final ConnectionCallback<T> action)
    {
        Connection connection = null;

        try
        {
            connection = getConnection();

            T result = action.doInConnection(connection);

            return result;
        }
        catch (SQLException sex)
        {
            throw convertException(sex);
        }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * @param <T> Konkreter Return-Typ.
     * @param psc {@link PreparedStatementCreator}
     * @param action {@link PreparedStatementCallback}
     * @return Object
     */
    public <T> T execute(final PreparedStatementCreator psc, final PreparedStatementCallback<T> action)
    {
        return execute((ConnectionCallback<T>) con -> {
            try (PreparedStatement ps = psc.createPreparedStatement(con))
            {
                T result = action.doInPreparedStatement(ps);

                return result;
            }
        });
    }

    /**
     * @param <T> Konkreter Return-Typ.
     * @param action {@link StatementCallback}
     * @return Object
     */
    public <T> T execute(final StatementCallback<T> action)
    {
        return execute((ConnectionCallback<T>) con -> {
            try (Statement stmt = con.createStatement())
            {
                T result = action.doInStatement(stmt);

                return result;
            }
        });
    }

    /**
     * Führt ein einfaches {@link Statement#execute(String)} aus.
     *
     * @param sql String
     */
    public void execute(final String sql)
    {
        execute((StatementCallback<?>) stmt -> stmt.execute(sql));
    }

    /**
     * @return {@link Connection}
     */
    protected Connection getConnection()
    {
        connectionSemaphoreAcquire();

        Connection connection = null;

        connection = DataSourceUtils.getConnection(getDataSource());

        return connection;
    }

    /**
     * Liefert den {@link Semaphore} für die parallele Nutzung der {@link DataSource}.
     *
     * @return {@link Semaphore}
     */
    protected Semaphore getConnectionSemaphore()
    {
        return this.connectionSemaphore;
    }

    /**
     * @return {@link DataSource}
     */
    public DataSource getDataSource()
    {
        return this.dataSource;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
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
     * @see SequenceQuery
     * @see SequenceProvider
     * @see SequenceQueryExecutor
     */
    public long getNextID(final String sequence)
    {
        return execute((ConnectionCallback<Long>) con -> {
            try (Statement stmt = con.createStatement())
            {
                long result = getNextID(sequence, con);

                return result;
            }
        });
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
     * @see SequenceQuery
     * @see SequenceProvider
     * @see SequenceQueryExecutor
     */
    public long getNextID(final String sequence, final Connection connection) throws SQLException
    {
        if (this.sequenceQueryExecutor == null)
        {
            this.reentrantLockSequence.lock();

            try
            {
                if (this.sequenceQueryExecutor == null)
                {
                    SequenceQuery sequenceQuery = SequenceQuery.determineQuery(connection);
                    this.sequenceQueryExecutor = new SequenceQueryExecutor(sequenceQuery);
                }
            }
            finally
            {
                this.reentrantLockSequence.unlock();
            }
        }

        long id = this.sequenceQueryExecutor.getNextID(sequence, connection);

        return id;
    }

    /**
     * Abfrage der {@link DatabaseMetaData}.
     *
     * @return boolean
     */
    protected boolean isBatchSupported()
    {
        return execute((ConnectionCallback<Boolean>) con -> {
            try (Statement stmt = con.createStatement())
            {
                boolean result = isBatchSupported(con);

                return result;
            }
        });
    }

    /**
     * Abfrage der {@link DatabaseMetaData}.
     *
     * @param connection {@link Connection}
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
     */
    public <T> T query(final String sql, final PreparedStatementSetter setter, final ResultSetExtractor<T> rse)
    {
        return execute(con -> con.prepareStatement(sql), ps -> {
            getLogger().debug(() -> String.format("execute: %s", sql));

            ps.clearParameters();
            setter.setValues(ps);

            try (ResultSet rs = ps.executeQuery())
            {
                return rse.extract(rs);
            }
        });
    }

    /**
     * Erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     * @param rowMapper {@link RowMapper}
     * @return {@link List}
     */
    public <T> List<T> query(final String sql, final PreparedStatementSetter setter, final RowMapper<T> rowMapper)
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
     */
    public <T> T query(final String sql, final ResultSetExtractor<T> rse)
    {
        return execute((StatementCallback<T>) stmt -> {
            getLogger().debug(() -> String.format("execute: %s", sql));

            try (ResultSet rs = stmt.executeQuery(sql))
            {
                return rse.extract(rs);
            }
        });
    }

    /**
     * Erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @return {@link List}
     */
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper)
    {
        return query(sql, new RowMapperResultSetExtractor<>(rowMapper));
    }

    /**
     * Liefert eine Liste aus Maps.<br>
     *
     * @param sql String
     * @return {@link List}
     */
    public List<Map<String, Object>> queryForList(final String sql)
    {
        return query(sql, new ColumnMapResultSetExtractor());
    }

    /**
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * Erstellt einen {@link Semaphore} im {@link JdbcTemplate}, der den Zugriff auf die {@link DataSource} reguliert.
     *
     * @param maxConnections int
     */
    public void setMaxConnections(final int maxConnections)
    {
        this.maxConnections = maxConnections;
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @return int; affectedRows
     */
    public int update(final String sql)
    {
        return execute((StatementCallback<Integer>) stmt -> {
            getLogger().debug(() -> String.format("execute: %s", sql));

            return stmt.executeUpdate(sql);
        });
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     * @return int; affectedRows
     */
    public int update(final String sql, final PreparedStatementSetter setter)
    {
        return execute(con -> con.prepareStatement(sql), ps -> {
            getLogger().debug(() -> String.format("execute: %s", sql));

            ps.clearParameters();
            setter.setValues(ps);

            return ps.executeUpdate();
        });
    }

    /**
     * Führt ein {@link Statement#executeBatch()} aus (INSERT, UPDATE, DELETE).<br>
     * Die Default Batch-Size beträgt 100.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param batchArgs {@link Collection}
     * @param setter {@link ParameterizedPreparedStatementSetter}
     * @return int[]; affectedRows
     */
    public <T> int[] updateBatch(final String sql, final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> setter)
    {
        return updateBatch(sql, batchArgs, setter, 100);
    }

    /**
     * Führt ein {@link Statement#executeBatch()} aus (INSERT, UPDATE, DELETE).
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param batchArgs {@link Collection}
     * @param setter {@link ParameterizedPreparedStatementSetter}
     * @param batchSize int
     * @return int[]; affectedRows
     */
    public <T> int[] updateBatch(final String sql, final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> setter, final int batchSize)
    {
        return execute(con -> con.prepareStatement(sql), ps -> {
            getLogger().debug(() -> String.format("execute batch: size=%d; %s", batchArgs.size(), sql));

            boolean supportsBatch = isBatchSupported(ps.getConnection());
            SequenceProvider sequenceProvider = sequence -> getNextID(sequence, ps.getConnection());

            List<int[]> affectedRows = new ArrayList<>();
            int n = 0;

            for (T arg : batchArgs)
            {
                ps.clearParameters();
                setter.setValues(ps, arg, sequenceProvider);
                n++;

                if (supportsBatch)
                {
                    ps.addBatch();

                    if (((n % batchSize) == 0) || (n == batchArgs.size()))
                    {
                        if (getLogger().isDebugEnabled())
                        {
                            int batchIndex = ((n % batchSize) == 0) ? n / batchSize : (n / batchSize) + 1;
                            int items = n - ((((n % batchSize) == 0) ? (n / batchSize) - 1 : (n / batchSize)) * batchSize);
                            getLogger().debug("Sending SQL batch update #{} with {} items", batchIndex, items);
                        }

                        affectedRows.add(ps.executeBatch());
                        ps.clearBatch();
                    }
                }
                else
                {
                    // Batch nicht möglich -> direkt ausführen.
                    int affectedRow = ps.executeUpdate();

                    affectedRows.add(new int[]
                    {
                            affectedRow
                    });
                }
            }

            return affectedRows.stream().flatMapToInt(af -> IntStream.of(af)).toArray();
        });
    }
}
