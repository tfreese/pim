/**
 * Created: 10.02.2017
 */

package de.freese.pim.server.mail;

import java.util.function.Function;

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

import de.freese.pim.server.mail.dao.DefaultMailDAO;
import de.freese.pim.server.mail.dao.MailDAO;

/**
 * @author Thomas Freese
 */
@Configuration
@EnableTransactionManagement
@Profile("test")
public class TestMailConfig
{
    /**
     * Erstellt ein neues {@link TestMailConfig} Object.
     */
    public TestMailConfig()
    {
        super();
    }

    /**
     * @return {@link DataSource}
     * @throws Exception Falls was schief geht.
     */
    @Bean(destroyMethod = "destroy")
    public DataSource dataSource() throws Exception
    {
        // DataSource dataSource = new JndiDataSourceLookup().getDataSource("jdbc/spring/manualTX"); // Wird in AllTests definiert.

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:mail_" + System.currentTimeMillis());
        dataSource.setAutoCommit(false);
        dataSource.setSuppressClose(true);

        try
        {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/hsqldb/V3__pim_mail_schema.sql"));
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
     * @return {@link MailDAO}
     */
    @Bean
    public MailDAO mailDAO(final DataSource dataSource)
    {
        DefaultMailDAO dao = new DefaultMailDAO();
        dao.setDataSource(dataSource);

        return dao;
    }

    /**
     * SQL für Sequenz-Abfragen.
     *
     * @return {@link Function}
     */
    @Bean
    public Function<String, String> sequenceQuery()
    {
        Function<String, String> query = seq -> "call next value for " + seq;

        return query;
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
