// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbMemory")
@PropertySource("classpath:hikari-pool.properties")
@PropertySource("classpath:database.properties")
public class HsqldbMemoryConfig extends AbstractHSQLDBConfig
{
    /**
    *
    */
    @Resource
    private DataSource dataSource;

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbMemoryConfig}
     */
    public HsqldbMemoryConfig()
    {
        super();
    }

    /**
     * @see de.freese.pim.server.spring.config.AbstractDBConfig#preDestroy()
     */
    @SuppressWarnings("deprecation")
    @Override
    @PreDestroy
    public void preDestroy() throws Exception
    {
        close(this.dataSource);
    }
}
