// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("ClientStandalone")
@ComponentScan(basePackages = {"de.freese.pim.gui", "de.freese.pim.core"})
public class PimClientStandaloneConfig extends AbstractPimClientConfig {
    public PimClientStandaloneConfig() {
        super();
    }
}
