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
@Profile("SqliteLocalFile")
@PropertySources(
{
        @PropertySource("classpath:tomcat-pool.properties"), @PropertySource("classpath:database.properties")
})
public class SqliteLocalFileConfig extends AbstractDBConfig
{
    static
    {
        System.setProperty("flyway.locations", "classpath:db/sqlite");
    }

    /**
    *
    */
    @Resource
    private DataSource dataSource = null;

    /**
     * Erzeugt eine neue Instanz von {@link SqliteLocalFileConfig}
     */
    public SqliteLocalFileConfig()
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
        close(this.dataSource);
    }
}
