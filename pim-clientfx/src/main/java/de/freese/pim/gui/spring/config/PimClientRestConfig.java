package de.freese.pim.gui.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 * @since 10.02.2017
 */
@Configuration
@Profile("ClientRest")
@ComponentScan(basePackages = {"de.freese.pim.gui", "de.freese.pim.core"})
public class PimClientRestConfig extends AbstractPimClientConfig {
    public PimClientRestConfig() {
        super();

        System.setProperty("spring.main.web-application-type", "NONE");
        System.setProperty("spring.flyway.enabled", Boolean.toString(false));
    }

    @Bean
    public RestClientCustomizer restClientCustomizer(@Value("${server.host}") final String serverHost, @Value("${server.port}") final int serverPort) {
        final String url = String.format("http://%s:%d/pim", serverHost, serverPort);

        return builder -> builder.baseUrl(url);
    }
}
