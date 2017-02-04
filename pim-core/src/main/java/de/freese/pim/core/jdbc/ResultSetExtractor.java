// Created: 12.01.2017
package de.freese.pim.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ResultSetExtractor<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
public interface ResultSetExtractor<T>
{
    /**
     * Konvertiert das {@link ResultSet} in eine andere Objektstruktur.
     *
     * @param rs ResultSet
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T extract(ResultSet rs) throws SQLException;
}
