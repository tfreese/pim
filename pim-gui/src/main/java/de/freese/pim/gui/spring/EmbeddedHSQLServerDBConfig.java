// Created: 10.02.2017
package de.freese.pim.gui.spring;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import de.freese.pim.core.db.HsqldbEmbeddedServer;
import de.freese.pim.core.db.IDataSourceBean;
import de.freese.pim.core.service.ISettingsService;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("embeddedHSQLServer")
public class EmbeddedHSQLServerDBConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link EmbeddedHSQLServerDBConfig}
     */
    public EmbeddedHSQLServerDBConfig()
    {
        super();
    }

    /**
     * @param dataSourceBean {@link IDataSourceBean}
     * @return {@link DataSource}
     */
    @Bean
    public DataSource dataSource(final IDataSourceBean dataSourceBean)
    {
        DataSource dataSource = dataSourceBean.getDataSource();

        return dataSource;
    }

    /**
     * @param settingsService {@link ISettingsService}
     * @return {@link IDataSourceBean}
     * @throws Exception Falls was schief geht.
     */
    @Bean(destroyMethod = "disconnect")
    public IDataSourceBean dataSourceBean(final ISettingsService settingsService) throws Exception
    {
        IDataSourceBean dataSourceBean = new HsqldbEmbeddedServer();
        dataSourceBean.configure(settingsService);
        dataSourceBean.testConnection();
        dataSourceBean.populateIfEmpty(null);
        // dataSourceBean.populateIfEmpty(() ->
        // {
        // LOGGER.info("Populate Database");
        // notifyPreloader(new PIMPreloaderNotification("Populate Database"));
        // // Utils.sleep(1, TimeUnit.SECONDS);
        // });
        // registerCloseable(() ->
        // {
        // LOGGER.info("Stop Database");
        // PIMApplication.dataSourceBean.disconnect();
        // PIMApplication.dataSourceBean = null;
        // });

        return dataSourceBean;
    }

    /**
     *
     */
    @PreDestroy
    public void shutdown()
    {
        System.out.println("EmbeddedHSQLServerDBConfig.shutdown()");
    }
}
