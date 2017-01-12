/**
 * Created: 11.01.2017
 */

package de.freese.pim.core.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * {@link ThreadLocal} für eine {@link Connection}.
 *
 * @author Thomas Freese
 */
public final class ConnectionHolder
{
    /**
     *
     */
    private static final ThreadLocal<Connection> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Setzt autoCommit = false auf der aktuellen {@link Connection}.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void beginTX() throws SQLException
    {
        get().setAutoCommit(false);
    }

    /**
     * Ruft die Methode {@link Connection#close()} auf der aktuellen {@link Connection} auf.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void close() throws SQLException
    {
        try (Connection connection = get())
        {
            connection.setAutoCommit(true);
            connection.setReadOnly(false);
        }
    }

    /**
     * Ruft die Methode {@link Connection#close()} auf der aktuellen {@link Connection} auf.<br>
     * Die {@link Connection} wird anschliessend aus der {@link ThreadLocal} entfernt.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void closeAndRemove() throws SQLException
    {
        close();
        remove();
    }

    /**
     * Ruft die Methode {@link Connection#commit()} auf der aktuellen {@link Connection} auf.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void commitTX() throws SQLException
    {
        get().commit();
    }

    /**
     * Liefert die {@link Connection} für den aktuellen Thread.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @return {@link Connection}
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static final Connection get() throws SQLException
    {
        Connection connection = THREAD_LOCAL.get();

        Objects.requireNonNull(connection, "connection required, call #set(Connection) first");

        // if (connection.isReadOnly())
        // {
        // connection.setReadOnly(false);
        // }

        return connection;
    }

    /**
     * Liefert true, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @return boolean
     */
    public static boolean isEmpty()
    {
        return THREAD_LOCAL.get() == null;
    }

    /**
     * Entfernt die {@link Connection} für den aktuellen Thread.
     */
    public static final void remove()
    {
        THREAD_LOCAL.remove();
    }

    /**
     * Ruft die Methode {@link Connection#rollback()} auf der aktuellen {@link Connection} auf.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void rollbackTX() throws SQLException
    {
        get().rollback();
    }

    /**
     * Setzt die {@link Connection} für den aktuellen Thread.<br>
     * Wirft eine {@link IllegalStateException}, wenn der aktuelle Thread bereits eine {@link Connection} hat.
     *
     * @param connection {@link Connection}
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static final void set(final Connection connection)
    {
        if (THREAD_LOCAL.get() != null)
        {
            throw new IllegalStateException("connection already set, call #remove() first");
        }

        Objects.requireNonNull(connection, "connection required");

        THREAD_LOCAL.set(connection);
    }
}