/**
 * Created: 10.07.2016
 */

package de.freese.pim.server.addressbook;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class TestAddressbookConfig
{
    /**
     * Erstellt ein neues {@link TestAddressbookConfig} Object.
     */
    public TestAddressbookConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link AddressBookDAO}
     */
    @Bean
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
    public DataSource dataSource()
    {
        // DataSource dataSource = new JndiDataSourceLookup().getDataSource("jdbc/spring/manualTX"); // Wird in AllTests definiert.

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:addressbook_" + System.currentTimeMillis());
        dataSource.setAutoCommit(true);
        dataSource.setSuppressClose(true);

        try
        {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/hsqldb/V2__pim_addressbook_schema.sql"));
            // populator.addScript(new ClassPathResource("db/hsqldb/pim_addressbook_data.sql"));
            populator.execute(dataSource);
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }

        return dataSource;
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link PlatformTransactionManager}
     */
    @Bean
    public PlatformTransactionManager transactionManager(final DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }
}