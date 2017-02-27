// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("ClientREST")
public class PIMClientRESTConfig extends AbstractPIMClientConfig
{
    // static
    // {
    // System.setProperty("flyway.enabled", Boolean.toString(false));
    // }

    /**
     * Erzeugt eine neue Instanz von {@link PIMClientRESTConfig}
     */
    public PIMClientRESTConfig()
    {
        super();
    }

    /**
     * @return {@link RestTemplateBuilder}
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder()
    {
        // RestTemplateBuilder bean = new RestTemplateBuilder().rootUri(rootUri).basicAuthorization(username, password);
        RestTemplateBuilder bean = new RestTemplateBuilder().rootUri("http://localhost:61222/pim");

        return bean;

        // RestTemplate rt = new RestTemplate();
        // rt.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        // rt.getMessageConverters().add(new StringHttpMessageConverter());
    }
}
