// Created: 12.01.2017
package de.freese.pim.core.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ParameterizedPreparedStatementSetter<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Row-Typ
 */
public interface ParameterizedPreparedStatementSetter<T>
{
    /**
     * Setzt die Values des {@link PreparedStatement}.
     *
     * @param ps {@link PreparedStatement}
     * @param argument Object
     * @throws SQLException Falls was schief geht.
     */
    public void setValues(PreparedStatement ps, T argument) throws SQLException;
}
