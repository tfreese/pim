// Created: 16.02.2017
package de.freese.pim.core.spring.config;

import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Common Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@PropertySource("classpath:application-common.properties")
public class CommonConfig {
    /**
     * <a href="https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#howto-customize-the-jackson-objectmapper">howto-customize-the-jackson-objectmapper</a>
     *
     * @return {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper jsonMapper() {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        // jsonMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        // jsonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);

        jsonMapper.setLocale(Locale.GERMANY);

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");
        jsonMapper.setTimeZone(timeZone);

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // df.setTimeZone(timeZone);
        // jsonMapper.setDateFormat(df);
        return jsonMapper;
    }
}
