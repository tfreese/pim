// Created: 10.02.2017
package de.freese.pim.gui.spring;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.freese.pim.server.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.server.addressbook.service.DefaultAddressBookService;
import de.freese.pim.server.addressbook.service.AddressBookService;
import de.freese.pim.server.mail.dao.DefaultMailDAO;
import de.freese.pim.server.mail.service.DefaultMailService;
import de.freese.pim.server.mail.service.MailService;

/**
 * Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@ComponentScan(basePackages =
{
        "de.freese.pim.common.spring", "de.freese.pim.server.spring"
})
public class PIMConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link PIMConfig}
     */
    public PIMConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link AddressBookService}
     */
    @Bean
    public AddressBookService addressBookService(final DataSource dataSource)
    {
        DefaultAddressBookService defaultAddressBookService = new DefaultAddressBookService();
        defaultAddressBookService.setAddressBookDAO(new DefaultAddressBookDAO().dataSource(dataSource));

        // IAddressBookService addressBookService = (IAddressBookService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new
        // Class<?>[]
        // {
        // IAddressBookService.class
        // }, new TransactionalInvocationHandler(dataSource, new DefaultAddressBookService(new
        // DefaultAddressBookDAO().dataSource(dataSource))));

        return defaultAddressBookService;
    }

    /**
     * @param dataSource {@link DataSource}
     * @param executorService {@link ExecutorService}
     * @param basePath {@link Path}
     * @return {@link MailService}
     */
    @Bean(destroyMethod = "disconnectAccounts")
    public MailService mailService(final DataSource dataSource, final ExecutorService executorService, final Path basePath)
    {
        DefaultMailService defaultMailService = new DefaultMailService();
        defaultMailService.setBasePath(basePath);
        defaultMailService.setExecutorService(executorService);
        defaultMailService.setMailDAO(new DefaultMailDAO().dataSource(dataSource));
        //
        // IMailService mailService = (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
        // {
        // IMailService.class
        // }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));

        return defaultMailService;

    }

    // /**
    // * FlywayAutoConfiguration.class
    // * @param dataSource {@link DataSource}
    // * @return {@link Flyway}
    // */
    // @Bean(initMethod = "migrate")
    // // @DependsOn("dataSource")
    // public Flyway flyway(final DataSource dataSource)
    // {
    // Flyway flyway = new Flyway();
    // flyway.setEncoding("UTF-8");
    // flyway.setBaselineOnMigrate(true);
    // flyway.setDataSource(dataSource);
    // // flyway.setLocations("filesystem:/path/to/migrations/");
    // flyway.setLocations("classpath:db/hsqldb");
    //
    // return flyway;
    // }

    /**
     * @param pimHome String
     * @return {@link Path}
     */
    @Bean
    @Primary
    public Path pimHomePath(@Value("${pim.home}") final String pimHome)
    {
        Path path = Paths.get(pimHome);

        return path;
    }
}
