// Created: 12.01.2017
package de.freese.pim.server.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.freese.pim.server.jdbc.sequence.SequenceProvider;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ParameterizedPreparedStatementSetter<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Row-Typ
 */
@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T>
{
    /**
     * Setzt die Values des {@link PreparedStatement}.
     *
     * @param ps {@link PreparedStatement}
     * @param argument Object
     * @param sequenceProvider {@link SequenceProvider}
     * @throws SQLException Falls was schief geht.
     */
    public void setValues(PreparedStatement ps, T argument, SequenceProvider sequenceProvider) throws SQLException;
}
