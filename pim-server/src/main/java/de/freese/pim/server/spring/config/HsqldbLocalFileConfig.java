// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbLocalFile")
@PropertySources(
{
        @PropertySource("classpath:hikari-pool.properties"), @PropertySource("classpath:database.properties")
})
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
     * @see de.freese.pim.server.spring.config.AbstractDBConfig#preDestroy()
     */
    @Override
    @PreDestroy
    public void preDestroy() throws Exception
    {
        shutdownCompact(this.dataSource);
        close(this.dataSource);
    }
}
