// Created: 12.01.2017
package de.freese.pim.server.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.PreparedStatementSetter<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 */
public interface PreparedStatementSetter
{
    /**
     * Setzt die Values des {@link PreparedStatement}.
     *
     * @param ps {@link PreparedStatement}
     * @throws SQLException Falls was schief geht.
     */
    public void setValues(PreparedStatement ps) throws SQLException;
}
