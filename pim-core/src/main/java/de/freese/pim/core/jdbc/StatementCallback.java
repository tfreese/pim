/**
 * Created: 04.02.2017
 */

package de.freese.pim.core.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.StatementCallback<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
public interface StatementCallback<T>
{
    /**
     * Ausführung von Code für ein {@link Statement}.
     * 
     * @param stmt {@link Statement}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T doInStatement(Statement stmt) throws SQLException;
}
