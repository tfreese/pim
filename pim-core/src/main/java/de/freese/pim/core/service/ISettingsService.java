// Created: 30.11.2016
package de.freese.pim.core.service;

import java.nio.file.Path;
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
     * Setzt die {@link DataSource}.
     *
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource);
}
