// Created: 10.02.2017
package de.freese.pim.core.spring;

import java.nio.file.Path;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import de.freese.pim.core.jdbc.SimpleDataSource;
import de.freese.pim.core.service.ISettingsService;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbLocalFile")
public class HsqldbLocalFileConfig extends AbstractHSQLDBConfig
{
    /**
    *
    */
    @Resource
    private DataSource dataSource = null;

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbLocalFileConfig}
     */
    public HsqldbLocalFileConfig()
    {
        super();
    }

    /**
     * Die {@link DataSource} wird in {@link #preDestroy()} geschlossen.
     *
     * @param basePath {@link Path}
     * @return {@link DataSource}
     */
    @Bean(destroyMethod = "")
    public DataSource dataSource(final Path basePath)
    {
        ISettingsService.MAX_ACTIVE_CONNECTIONS.set(1);

        Path dbPath = basePath.resolve(getDatabaseName());

        // ;hsqldb.tx=mvcc
        String url = String.format("jdbc:hsqldb:file:%s;shutdown=true", dbPath);

        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setDriverClassName(getDriver());
        dataSource.setUrl(url);
        dataSource.setReadOnly(true);
        dataSource.setAutoCommit(true);
        // dataSource.setSuppressClose(true);

        return dataSource;
    }

    /**
     * @see de.freese.pim.core.spring.AbstractDBConfig#preDestroy()
     */
    @Override
    @PreDestroy
    public void preDestroy() throws Exception
    {
        shutdownCompact(this.dataSource);
        close(this.dataSource);
    }
}
