// Created: 10.02.2017
package de.freese.pim.server.spring.config;

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
public class HsqldbMemoryConfig extends AbstractHsqldbConfig
{
    /**
     * Erstellt ein neues {@link HsqldbMemoryConfig} Object.
     */
    public HsqldbMemoryConfig()
    {
        super();
    }
}
