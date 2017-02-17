// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
// @ComponentScan(basePackages =
// {
// "de.freese.pim.common.spring", "de.freese.pim.server.spring"
// })
@ComponentScan(basePackages =
{
        "de.freese.pim"
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

    // /**
    // * @param addressBookService {@link AddressBookService}
    // * @param executorService {@link ExecutorService}
    // * @param jsonMapper {@link ObjectMapper}
    // * @return {@link FXAddressbookService}
    // */
    // @Bean
    // public FXAddressbookService clientAddressBookService(final AddressBookService addressBookService, final ExecutorService
    // executorService,
    // final ObjectMapper jsonMapper)
    // {
    // DefaultEmbeddedFXAddressbookService fxAddressbookService = new DefaultEmbeddedFXAddressbookService();
    // fxAddressbookService.setJsonMapper(jsonMapper);
    // fxAddressbookService.setExecutorService(executorService);
    // fxAddressbookService.setAddressBookService(addressBookService);
    //
    // return fxAddressbookService;
    // }

    // /**
    // * @param mailService {@link MailService}
    // * @param executorService {@link ExecutorService}
    // * @param pimHomePath {@link Path}
    // * @param jsonMapper {@link ObjectMapper}
    // * @return {@link FXMailService}
    // */
    // @Bean(destroyMethod = "disconnectAccounts")
    // public FXMailService clientMailService(final MailService mailService, final ExecutorService executorService, final Path pimHomePath,
    // final ObjectMapper jsonMapper)
    // {
    // DefaultEmbeddedFXMailService fxMailService = new DefaultEmbeddedFXMailService();
    // fxMailService.setMailService(mailService);
    // fxMailService.setBasePath(pimHomePath);
    // fxMailService.setJsonMapper(jsonMapper);
    // fxMailService.setExecutorService(executorService);
    //
    // return fxMailService;
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
