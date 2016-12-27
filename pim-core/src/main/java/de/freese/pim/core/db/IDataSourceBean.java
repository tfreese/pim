// Created: 15.12.2016
package de.freese.pim.core.db;

import javax.sql.DataSource;
import de.freese.pim.core.service.ISettingsService;

/**
 * Intreface für eine {@link DataSource}-Bean.
 *
 * @author Thomas Freese
 */
public interface IDataSourceBean extends AutoCloseable
{
    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public default void close() throws Exception
    {
        disconnect();
    }

    /**
     * Konfiguriert die {@link DataSource}.
     *
     * @param settingsService {@link ISettingsService}
     * @throws Exception Falls was schief geht.
     */
    public void configure(ISettingsService settingsService) throws Exception;

    /**
     * Beendet alle Verbindungen und schliesst die {@link DataSource}.
     *
     * @throws Exception Falls was schief geht.
     */
    public void disconnect() throws Exception;

    /**
     * Liefert die {@link DataSource}.
     *
     * @return {@link DataSource}
     */
    public DataSource getDataSource();

    /**
     * Befüllt die Datenbank, wenn diese noch leer ist.<br>
     * Als Trigger wird die Existenz der Tabelle SETTINGS verwendet.
     *
     * @param populateCallback {@link Runnable}; optional, wird vor dem populate aufgerufen
     * @throws Exception Falls was schief geht.
     */
    public void populateIfEmpty(final Runnable populateCallback) throws Exception;

    /**
     * Test der DB-Connection.
     *
     * @throws Exception Falls was schief geht.
     */
    public void testConnection() throws Exception;
}
