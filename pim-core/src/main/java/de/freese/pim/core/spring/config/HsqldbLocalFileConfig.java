package de.freese.pim.core.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 * @since 10.02.2017
 */
@Configuration
@Profile("HsqldbLocalFile")
@PropertySource("classpath:hikari-pool.properties")
@PropertySource("classpath:database.properties")
public class HsqldbLocalFileConfig extends AbstractHsqldbConfig {
    public HsqldbLocalFileConfig() {
        super();
    }
}
