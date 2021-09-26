// Created: 10.07.2016
package de.freese.pim.server.addressbook;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.freese.pim.server.addressbook.dao.AddressBookDAO;
import de.freese.pim.server.addressbook.dao.DefaultAddressBookDAO;

/**
 * @author Thomas Freese
 */
@Configuration
@EnableTransactionManagement
@Profile("test")
public class TestAddressbookConfig
{
    /**
     * @param dataSource {@link DataSource}
     *
     * @return {@link AddressBookDAO}
     */
    @Bean
    // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AddressBookDAO addressBookDAO(final DataSource dataSource)
    {
        DefaultAddressBookDAO dao = new DefaultAddressBookDAO();
        dao.setDataSource(dataSource);

        return dao;
    }

    /**
     * @return {@link DataSource}
     */
    @Bean(destroyMethod = "destroy")
    // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataSource dataSource()
    {
        // DataSource dataSource = new JndiDataSourceLookup().getDataSource("jdbc/spring/manualTX"); // Wird in AllTests definiert.

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:" + System.currentTimeMillis());
        dataSource.setAutoCommit(true);
        dataSource.setSuppressClose(true);

        try
        {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/hsqldb/V2__pim_addressbook_schema.sql"));
            // populator.addScript(new ClassPathResource("db/hsqldb/pim_addressbook_data.sql"));
            populator.execute(dataSource);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return dataSource;
    }

    /**
     * SQL für Sequenz-Abfragen.
     *
     * @return {@link Function}
     */
    @Bean
    public UnaryOperator<String> sequenceQuery()
    {
        UnaryOperator<String> query = seq -> "call next value for " + seq;

        return query;
    }

    /**
     * @param dataSource {@link DataSource}
     *
     * @return {@link PlatformTransactionManager}
     */
    @Bean
    public PlatformTransactionManager transactionManager(final DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }
}