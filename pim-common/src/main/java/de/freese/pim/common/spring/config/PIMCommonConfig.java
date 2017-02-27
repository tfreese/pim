// Created: 16.02.2017
package de.freese.pim.common.spring.config;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Common Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@PropertySource("classpath:application-common.properties")
public class PIMCommonConfig
{
    static
    {
        // System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
        // Integer.toString(Runtime.getRuntime().availableProcessors()));
        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory",
                "de.freese.pim.common.concurrent.PIMForkJoinWorkerThreadFactory");
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMCommonConfig}
     */
    public PIMCommonConfig()
    {
        super();
    }

    // /**
    // * @return
    // */
    // @Bean
    // public Jackson2ObjectMapperBuilder jacksonBuilder()
    // {
    // Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
    // b.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    // return b;
    // }

    /**
     * https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
     *
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

        jsonMapper.setLocale(Locale.GERMANY);

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");
        jsonMapper.setTimeZone(timeZone);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(timeZone);
        jsonMapper.setDateFormat(df);

        return jsonMapper;
    }
}
