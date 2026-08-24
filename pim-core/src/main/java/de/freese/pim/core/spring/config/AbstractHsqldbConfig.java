package de.freese.pim.core.spring.config;

import java.util.function.UnaryOperator;

import org.springframework.context.annotation.Bean;

/**
 * Basis-Konfiguration der Datenbank für HSQLDB.
 *
 * @author Thomas Freese
 * @since 10.02.2017
 */
public abstract class AbstractHsqldbConfig {
    protected AbstractHsqldbConfig() {
        super();

        System.setProperty("spring.flyway.locations", "classpath:db/hsqldb");
    }

    @Bean
    public UnaryOperator<String> sequenceQuery() {
        return seq -> "call next value for " + seq;
    }
}
