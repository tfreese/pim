// Created: 12.01.2017
package de.freese.pim.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.RowMapper<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <R> Konkreter Row-Typ
 */
public interface RowMapper<R>
{
    /**
     * Mapped die aktuelle Zeile des {@link ResultSet} in ein Objekt.
     *
     * @param rs {@link ResultSet}
     * @param rowNum int
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public R map(ResultSet rs, int rowNum) throws SQLException;
}
