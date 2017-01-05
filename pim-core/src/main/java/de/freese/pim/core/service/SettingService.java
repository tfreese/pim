// Created: 01.12.2016
package de.freese.pim.core.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

/**
 * Service für die Einstellungen.
 *
 * @author Thomas Freese
 */
public class SettingService implements ISettingsService
{
    /**
    *
    */
    private static final Path HOME_DEFAULT = Paths.get(System.getProperty("user.home"), ".pim");

    /**
     *
     */
    private static final ISettingsService INSTANCE = new SettingService();

    /**
     * @return {@link ISettingsService}
     */
    public static ISettingsService getInstance()
    {
        return INSTANCE;
    }

    /**
     *
     */
    private DataSource dataSource = null;

    /**
     * Erzeugt eine neue Instanz von {@link SettingService}
     */
    private SettingService()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.service.ISettingsService#getDBHost()
     */
    @Override
    public String getDBHost()
    {
        return "localhost";
    }

    /**
     * @see de.freese.pim.core.service.ISettingsService#getDBPassword()
     */
    @Override
    public String getDBPassword()
    {
        // CREATE USER EFREEST PASSWORD 'EFREEST'
        // CREATE USER SA PASSWORD DIGEST 'd41d8cd98f00b204e9800998ecf8427e'
        // ALTER USER SA SET LOCAL TRUE
        // GRANT DBA TO SA
        return null;
    }

    /**
     * @see de.freese.pim.core.service.ISettingsService#getDBPort()
     */
    @Override
    public int getDBPort()
    {
        return 0;
    }

    /**
     * @see de.freese.pim.core.service.ISettingsService#getDBUser()
     */
    @Override
    public String getDBUser()
    {
        // return Utils.getSystemUserName();
        return "sa";
    }

    /**
     * @see de.freese.pim.core.service.ISettingsService#getHome()
     */
    @Override
    public Path getHome()
    {
        return HOME_DEFAULT;
    }

    /**
     * @see de.freese.pim.core.service.ISettingsService#setDataSource(javax.sql.DataSource)
     */
    @Override
    public void setDataSource(final DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
}
