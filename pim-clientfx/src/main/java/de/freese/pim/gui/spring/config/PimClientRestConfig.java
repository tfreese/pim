// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("ClientRest")
@ComponentScan(basePackages =
        {
                "de.freese.pim.gui", "de.freese.pim.core"
        })
public class PimClientRestConfig extends AbstractPimClientConfig
{
    public PimClientRestConfig()
    {
        super();

        System.setProperty("spring.main.web-application-type", "NONE");
        System.setProperty("flyway.enabled", Boolean.toString(false));
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(@Value("${server.host}") final String serverHost, @Value("${server.port}") final int serverPort)
    {
        // RestTemplateBuilder bean = new RestTemplateBuilder().rootUri(rootUri).basicAuthorization(username, password);
        String url = String.format("http://%s:%d/pim", serverHost, serverPort);

        // RestTemplate rt = new RestTemplate();
        // rt.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        // rt.getMessageConverters().add(new StringHttpMessageConverter());

        return new RestTemplateBuilder().rootUri(url);
    }
}
