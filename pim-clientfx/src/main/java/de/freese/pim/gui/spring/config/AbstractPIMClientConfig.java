// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
public abstract class AbstractPIMClientConfig
{
    /**
     * @param pimHome String
     *
     * @return {@link Path}
     */
    @Bean
    @Primary
    public Path pimHomePath(@Value("${pim.home}") final String pimHome)
    {
        return Paths.get(pimHome);
    }
}
