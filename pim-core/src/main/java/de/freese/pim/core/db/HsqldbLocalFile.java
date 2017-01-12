// Created: 15.12.2016
package de.freese.pim.core.db;

import java.nio.file.Path;

import javax.sql.DataSource;

import de.freese.pim.core.persistence.SimpleDataSource;
import de.freese.pim.core.service.ISettingsService;

/**
 * {@link IDataSourceBean} für eine HSQLDB im File-Mode.
 *
 * @author Thomas Freese
 */
public class HsqldbLocalFile extends AbstractHsqldbBean
{
    /**
     * Erzeugt eine neue Instanz von {@link HsqldbLocalFile}
     */
    public HsqldbLocalFile()
    {
        super();
    }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#configure(de.freese.pim.core.service.ISettingsService)
     */
    @Override
    public void configure(final ISettingsService settingsService) throws Exception
    {
        Path home = settingsService.getHome();
        Path dbPath = getDBPath(home);

        String driver = getDriver();
        String validationQuery = getValidationQuery();
        String url = String.format("jdbc:hsqldb:file:%s;shutdown=true", dbPath); // ;hsqldb.tx=mvcc

        DataSource dataSource = createDataSource(driver, url, null, null, validationQuery);
        setDataSource(dataSource);
    }

    /**
     * @see de.freese.pim.core.db.AbstractDataSourceBean#createDataSource(java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    protected DataSource createDataSource(final String driver, final String url, final String userName, final String password,
            final String validationQuery)
    {
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setAutoCommit(false);
        // dataSource.setSuppressClose(true);

        return dataSource;
    }
}
