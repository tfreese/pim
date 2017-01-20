/**
 * Created: 10.07.2016
 */

package de.freese.pim.core.addressbook;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.freese.pim.core.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.core.addressbook.dao.IAddressBookDAO;
import de.freese.pim.core.persistence.JdbcTemplate;

/**
 * @author Thomas Freese
 */
@Configuration
@EnableTransactionManagement
public class TestConfig
{
    /**
     * Erstellt ein neues {@link TestConfig} Object.
     */
    public TestConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link IAddressBookDAO}
     */
    @Bean
    public IAddressBookDAO addressBookDAO(final DataSource dataSource)
    {
        DefaultAddressBookDAO dao = new DefaultAddressBookDAO();

        JdbcTemplate jdbcTemplate = new JdbcTemplate()
        {
            /**
             * @see de.freese.pim.core.persistence.JdbcTemplate#closeConnection(java.sql.Connection)
             */
            @Override
            protected void closeConnection(final Connection connection) throws SQLException
            {
                DataSourceUtils.releaseConnection(connection, getDataSource());
            }

            /**
             * @see de.freese.pim.core.persistence.JdbcTemplate#getConnection()
             */
            @Override
            protected Connection getConnection() throws SQLException
            {
                return DataSourceUtils.getConnection(getDataSource());
            }
        };

        dao.setJdbcTemplate(jdbcTemplate.setDataSource(dataSource));

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
        dataSource.setAutoCommit(false);
        dataSource.setSuppressClose(true);

        try
        {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/hsqldb/V2__pim_addressbook_schema.sql"));
            // populator.addScript(new ClassPathResource("db/hsqldb/pim_addressbook_data.sql"));
            populator.execute(dataSource);
        }
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Exception ex)
        {
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