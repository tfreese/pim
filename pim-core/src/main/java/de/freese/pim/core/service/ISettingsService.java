// Created: 30.11.2016
package de.freese.pim.core.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface für den Service der Einstellungen.<br>
 * Sämtliche DB-Parameter werden in einer lokalen Property-Datei abgelegt (~/.pim/pim.properties).<br>
 * Alle anderen Parameter werden aus der Datenbank gelesen.
 *
 * @author Thomas Freese
 */
public interface ISettingsService
{
    /**
     * Anzahl aktiver Connections im ConnectionPool.
     */
    public static final AtomicInteger MAX_ACTIVE_CONNECTIONS = new AtomicInteger(10);
    // public static final IntegerProperty MAX_ACTIVE_CONNECTIONS = new SimpleIntegerProperty(10);

    // /**
    // * Liefert die Anzahl aktiver Connections im ConnectionPool.
    // *
    // * @return int
    // */
    // public static default int getMaxActiveConnections()
    // {
    // return MAX_ACTIVE_CONNECTIONS.get();
    // }
    //
    // /**
    // * Setzt die Anzahl aktiver Connections im ConnectionPool.
    // *
    // * @param maxActive int
    // */
    // public static void setMaxActiveConnections(final int maxActive)
    // {
    // MAX_ACTIVE_CONNECTIONS.set(maxActive);
    // }
}
