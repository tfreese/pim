// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("ClientStandalone")
public class PIMClientStandaloneConfig extends AbstractPIMClientConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link PIMClientStandaloneConfig}
     */
    public PIMClientStandaloneConfig()
    {
        super();
    }
}
