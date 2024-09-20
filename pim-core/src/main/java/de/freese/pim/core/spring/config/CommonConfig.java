// Created: 16.02.2017
package de.freese.pim.core.spring.config;

import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
     * <a href="https://www.baeldung.com/spring-boot-customize-jackson-objectmapper">spring-boot-customize-jackson-objectmapper</a>
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        // module.addSerializer(LOCAL_DATETIME_SERIALIZER);

        final ObjectMapper jsonMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(javaTimeModule)
                // .setVisibility(PropertyAccessor.FIELD, Visibility.NONE)
                // .setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY)
                // .setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY)
                ;

        jsonMapper.setLocale(Locale.GERMANY);

        final TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");
        jsonMapper.setTimeZone(timeZone);

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // df.setTimeZone(timeZone);
        // jsonMapper.setDateFormat(df);
        return jsonMapper;
    }
}
