// Created: 30.11.2016
package de.freese.pim.core.service;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;

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

    /**
     * Liefert den DB-Host aus der lokalen Property-Datei.
     *
     * @return String
     */
    public String getDBHost();

    /**
     * Liefert das DB-Password aus der lokalen Property-Datei.
     *
     * @return String
     */
    public String getDBPassword();

    /**
     * Liefert den DB-Port aus der lokalen Property-Datei.
     *
     * @return int
     */
    public int getDBPort();

    /**
     * Liefert den DB-User aus der lokalen Property-Datei.
     *
     * @return String
     */
    public String getDBUser();

    /**
     * Liefert das fest definiert Home-Verzeichnis der PIM-Anwendung.
     *
     * @return {@link Path}
     */
    public Path getHome();

    /**
     * Liefert den Namen des angemeldeten Systemusers.
     *
     * @return String
     */
    public String getSystemUserName();

    /**
     * Setzt die {@link DataSource}.
     *
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource);
}
