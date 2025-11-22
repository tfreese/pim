// Created: 16.02.2017
package de.freese.pim.core.spring.config;

import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

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
    public JsonMapper jsonMapper() {
        // final JavaTimeModule javaTimeModule = new JavaTimeModule();
        // module.addSerializer(LOCAL_DATETIME_SERIALIZER);

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // df.setTimeZone(timeZone);
        // jsonMapper.setDateFormat(df);

        return JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .changeDefaultPropertyInclusion(value -> value.withContentInclusion(JsonInclude.Include.NON_NULL))
                .defaultLocale(Locale.GERMANY)
                .defaultTimeZone(TimeZone.getTimeZone("Europe/Berlin"))
                // .registerModule(javaTimeModule)
                // .setVisibility(PropertyAccessor.FIELD, Visibility.NONE)
                // .setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY)
                // .setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY)
                .build();
    }
}
