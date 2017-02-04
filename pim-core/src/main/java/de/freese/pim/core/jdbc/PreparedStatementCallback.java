/**
 * Created: 04.02.2017
 */

package de.freese.pim.core.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.PreparedStatementCallback<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
public interface PreparedStatementCallback<T>
{
    /**
     * Ausführung von Code für ein {@link PreparedStatement}.
     *
     * @param ps {@link PreparedStatement}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T doInPreparedStatement(PreparedStatement ps) throws SQLException;
}
