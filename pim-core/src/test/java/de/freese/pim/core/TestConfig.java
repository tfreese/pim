// Created: 10.07.2016
package de.freese.pim.core;

import java.util.UUID;
import java.util.function.UnaryOperator;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.freese.pim.core.dao.AddressBookDAO;
import de.freese.pim.core.dao.DefaultAddressBookDAO;
import de.freese.pim.core.dao.DefaultMailDAO;
import de.freese.pim.core.dao.MailDAO;

/**
 * @author Thomas Freese
 */
@Configuration
@EnableTransactionManagement
@Profile("test")
public class TestConfig {
    @Bean
    // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AddressBookDAO addressBookDAO(final DataSource dataSource) {
        DefaultAddressBookDAO dao = new DefaultAddressBookDAO();
        dao.setDataSource(dataSource);

        return dao;
    }

    @Bean(destroyMethod = "close")
    // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataSource dataSource() {
        // DataSource dataSource = new JndiDataSourceLookup().getDataSource("jdbc/spring/manualTX"); // Wird in AllTests definiert.

        String id = UUID.randomUUID().toString();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        hikariConfig.setJdbcUrl("jdbc:hsqldb:mem:" + id + ";shutdown=true");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setPoolName("hikari-" + id);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(8);
        hikariConfig.setAutoCommit(false);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/hsqldb/V2__pim_addressbook_schema.sql"));
            populator.addScript(new ClassPathResource("db/hsqldb/V3__pim_mail_schema.sql"));
            populator.execute(dataSource);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return dataSource;
    }

    @Bean
    // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MailDAO mailDAO(final DataSource dataSource) {
        DefaultMailDAO dao = new DefaultMailDAO();
        dao.setDataSource(dataSource);

        return dao;
    }

    @Bean
    public UnaryOperator<String> sequenceQuery() {
        return seq -> "call next value for " + seq;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
