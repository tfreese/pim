// Created: 16.02.2017
package de.freese.pim.common.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Common Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
public class PIMCommonConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link PIMCommonConfig}
     */
    public PIMCommonConfig()
    {
        super();
    }

    /**
     * @return {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper jsonMapper()
    {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        // jsonMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        // jsonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);

        return jsonMapper;
    }
}
