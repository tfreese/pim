// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import org.springframework.beans.factory.annotation.Value;
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
     * @param serverHost String
     * @param serverPort int
     * @return {@link RestTemplateBuilder}
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder(@Value("${server.host}") final String serverHost,
            @Value("${server.port}") final int serverPort)
    {
        // RestTemplateBuilder bean = new RestTemplateBuilder().rootUri(rootUri).basicAuthorization(username, password);
        String url = String.format("http://%s:%d/pim", serverHost, serverPort);
        RestTemplateBuilder bean = new RestTemplateBuilder().rootUri(url);

        return bean;

        // RestTemplate rt = new RestTemplate();
        // rt.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        // rt.getMessageConverters().add(new StringHttpMessageConverter());
    }
}
