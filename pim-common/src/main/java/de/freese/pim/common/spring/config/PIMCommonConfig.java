// Created: 16.02.2017
package de.freese.pim.common.spring.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

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
        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", "de.freese.pim.common.concurrent.PIMForkJoinWorkerThreadFactory");
    }

    // /**
    // *
    // */
    // @Resource
    // private ExecutorService executorService = null;
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

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // df.setTimeZone(timeZone);
        // jsonMapper.setDateFormat(df);
        return jsonMapper;
    }

    /**
     * @return {@link ScheduledExecutorFactoryBean}
     */
    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorFactoryBean scheduledExecutorService()
    {
        int poolSize = Runtime.getRuntime().availableProcessors();

        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(poolSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("scheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @param executorService          {@link ExecutorService}
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     *
     * @return {@link TaskScheduler}
     */
    @Bean
    @ConditionalOnMissingBean(TaskScheduler.class)
    public TaskScheduler taskScheduler(final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService)
    {
        ConcurrentTaskScheduler bean = new ConcurrentTaskScheduler(executorService, scheduledExecutorService);

        return bean;
    }

//    @Bean
//    @ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
//    public ApplicationInformation cloudFoundryApplicationInformation(Environment environment)
//    {
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(ApplicationInformation.class)
//    public ApplicationInformation defaultApplicationInformation()
//    {
//    }
}
