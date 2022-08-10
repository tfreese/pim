// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import java.util.function.UnaryOperator;

import org.springframework.context.annotation.Bean;

/**
 * Basis-Konfiguration der Datenbank für HSQLDB.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHsqldbConfig
{
    /**
     * Erstellt ein neues {@link AbstractHsqldbConfig} Object.
     */
    protected AbstractHsqldbConfig()
    {
        super();

        System.setProperty("spring.flyway.locations", "classpath:db/hsqldb");
    }

    /**
     * SQL für Sequenz-Abfragen.
     *
     * @return {@link UnaryOperator}
     */
    @Bean
    public UnaryOperator<String> sequenceQuery()
    {
        return seq -> "call next value for " + seq;
    }
}