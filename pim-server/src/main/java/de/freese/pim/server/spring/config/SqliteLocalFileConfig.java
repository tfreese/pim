// Created: 10.02.2017
package de.freese.pim.server.spring.config;

import java.util.function.UnaryOperator;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

/**
 * Spring-Konfiguration der Datenbank.
 *
 * @author Thomas Freese
 */
@Configuration()
@Profile("SqliteLocalFile")
@PropertySource("classpath:hikari-pool.properties")
@PropertySource("classpath:database.properties")
public class SqliteLocalFileConfig {
    public SqliteLocalFileConfig() {
        super();

        System.setProperty("spring.flyway.locations", "classpath:db/sqlite");
    }

    /**
     * Nicht Hikari nehmen, sondern die SQLite-Implementierung.
     */
    @Bean
    public DataSource dataSource(@Value("${pim.home}") final String pimHome, @Value("${pim.db-name}") final String pimDbName) {
        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        System.setProperty("sqlite.purejava", "true");

        // Pfade für native Libraries.
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

        // DriverManager.setLogWriter(new PrintWriter(System.out, true));

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(false);
        config.setReadUncommited(true);

        SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl("jdbc:sqlite:" + pimHome + "/" + pimDbName + ".sqlite");

        return dataSource;
    }

    @Bean
    public UnaryOperator<String> sequenceQuery() {
        return seq -> "select random()";
    }
}
