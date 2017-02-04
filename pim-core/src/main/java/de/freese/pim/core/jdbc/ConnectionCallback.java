/**
 * Created: 04.02.2017
 */

package de.freese.pim.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ConnectionCallback<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
public interface ConnectionCallback<T>
{
    /**
     * Ausführung von Code in einer {@link Connection}.
     *
     * @param connection {@link Connection}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T doInConnection(Connection connection) throws SQLException;
}
