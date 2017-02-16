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

import com.fasterxml.jackson.databind.ObjectMapper;

import de.freese.pim.gui.addressbook.service.DefaultEmbeddedFXAddressbookService;
import de.freese.pim.gui.addressbook.service.FXAddressbookService;
import de.freese.pim.gui.mail.service.DefaultEmbeddedFXMailService;
import de.freese.pim.gui.mail.service.FXMailService;
import de.freese.pim.server.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.server.addressbook.service.DefaultAddressBookService;
import de.freese.pim.server.mail.dao.DefaultMailDAO;
import de.freese.pim.server.mail.service.DefaultMailService;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@ComponentScan(basePackages =
{
        "de.freese.pim.common.spring", "de.freese.pim.server.spring"
})
public class PIMClientConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link PIMClientConfig}
     */
    public PIMClientConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     * @param jsonMapper {@link ObjectMapper}
     * @return {@link FXAddressbookService}
     */
    @Bean
    public FXAddressbookService addressBookService(final DataSource dataSource, final ObjectMapper jsonMapper)
    {
        DefaultAddressBookService defaultAddressBookService = new DefaultAddressBookService();
        defaultAddressBookService.setAddressBookDAO(new DefaultAddressBookDAO().dataSource(dataSource));

        // IAddressBookService addressBookService = (IAddressBookService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new
        // Class<?>[]
        // {
        // IAddressBookService.class
        // }, new TransactionalInvocationHandler(dataSource, new DefaultAddressBookService(new
        // DefaultAddressBookDAO().dataSource(dataSource))));

        DefaultEmbeddedFXAddressbookService fxAddressbookService = new DefaultEmbeddedFXAddressbookService();
        fxAddressbookService.setJsonMapper(jsonMapper);
        fxAddressbookService.setAddressBookService(defaultAddressBookService);

        return fxAddressbookService;
    }

    /**
     * @param dataSource {@link DataSource}
     * @param executorService {@link ExecutorService}
     * @param pimHomePath {@link Path}
     * @param jsonMapper {@link ObjectMapper}
     * @return {@link FXMailService}
     */
    @Bean(destroyMethod = "disconnectAccounts")
    public FXMailService mailService(final DataSource dataSource, final ExecutorService executorService, final Path pimHomePath,
            final ObjectMapper jsonMapper)
    {
        DefaultMailService defaultMailService = new DefaultMailService();
        defaultMailService.setExecutorService(executorService);
        defaultMailService.setMailDAO(new DefaultMailDAO().dataSource(dataSource));
        //
        // IMailService mailService = (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
        // {
        // IMailService.class
        // }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));

        DefaultEmbeddedFXMailService fxMailService = new DefaultEmbeddedFXMailService();
        fxMailService.setBasePath(pimHomePath);
        fxMailService.setJsonMapper(jsonMapper);
        fxMailService.setMailService(defaultMailService);

        return fxMailService;

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
