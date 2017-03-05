// Created: 17.02.2017
package de.freese.pim.server.spring.config;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Server Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("Server")
@ComponentScan(basePackages =
{
        "de.freese.pim"
})
public class ServerConfig extends WebMvcConfigurationSupport
{
    /**
     *
     */
    @Resource
    private ObjectMapper jsonMapper = null;

    /**
     * Erzeugt eine neue Instanz von {@link ServerConfig}
     */
    public ServerConfig()
    {
        super();
    }

    /**
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#configureAsyncSupport(org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer)
     */
    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer)
    {
        // Verlagert die asynchrone Ausführung von Server-Requests (Callable, WebAsyncTask) in diesen ThreadPool.
        // Ansonsten würde für jeden Request immer ein neuer Thread erzeugt, siehe TaskExecutor des RequestMappingHandlerAdapter.
        configurer.setTaskExecutor(taskExecutor());
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorService()
    {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        int maxSize = coreSize * 2;
        int queueSize = maxSize * 3;
        int keepAliveSeconds = 60;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setQueueCapacity(queueSize);
        bean.setKeepAliveSeconds(keepAliveSeconds);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("server-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#extendMessageConverters(java.util.List)
     */
    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters)
    {
        // Make sure dates are serialised in ISO-8601 format instead as timestamps
        for (HttpMessageConverter<?> converter : converters)
        {
            if (converter instanceof MappingJackson2HttpMessageConverter)
            {
                MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                jsonMessageConverter.setObjectMapper(this.jsonMapper);
                // ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                // objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                // objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                // // objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
                // // objectMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
                // // objectMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);
                //
                // objectMapper.setLocale(Locale.GERMANY);
                //
                // TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");
                // objectMapper.setTimeZone(timeZone);
                //
                // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // df.setTimeZone(timeZone);
                // objectMapper.setDateFormat(df);

                break;
            }
        }
    }

    /**
     * @return {@link AsyncTaskExecutor}
     */
    @Bean(
    {
            "taskExecutor", "serverTaskExecutor"
    })
    public AsyncTaskExecutor taskExecutor()
    {
        AsyncTaskExecutor bean = new ConcurrentTaskExecutor(executorService().getObject());

        return bean;
    }

    // /**
    // * @param dataSource {@link DataSource}
    // * @param executorService {@link ExecutorService}
    // * @return {@link MailService}
    // */
    // @Bean(destroyMethod = "disconnectAccounts")
    // public MailService mailService(final DataSource dataSource, final ExecutorService executorService)
    // {
    // DefaultMailService bean = new DefaultMailService();
    // bean.setMailDAO(new DefaultMailDAO().dataSource(dataSource));
    // bean.setExecutorService(executorService);
    // //
    // // IMailService mailService = (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
    // // {
    // // IMailService.class
    // // }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));
    //
    // return bean;
    // }

    // /**
    // * FlywayAutoConfiguration.class
    // * @param dataSource {@link DataSource}
    // * @return {@link Flyway}
    // */
    // @Bean(initMethod = "migrate")
    // // @DependsOn("dataSource")
    // public Flyway flyway(final DataSource dataSource)
    // {
    // Flyway flyway = new Flyway();
    // flyway.setEncoding("UTF-8");
    // flyway.setBaselineOnMigrate(true);
    // flyway.setDataSource(dataSource);
    // // flyway.setLocations("filesystem:/path/to/migrations/");
    // flyway.setLocations("classpath:db/hsqldb");
    //
    // return flyway;
    // }

    // /**
    // * @return {@link EmbeddedServletContainerCustomizer}
    // */
    // @Bean
    // public EmbeddedServletContainerCustomizer tomcatCustomizer()
    // {
    // return container -> {
    // if (container instanceof TomcatEmbeddedServletContainerFactory)
    // {
    // ((TomcatEmbeddedServletContainerFactory) container).addConnectorCustomizers(gracefulShutdown());
    // }
    // };
    // }

    // /**
    // * @return {@link EmbeddedServletContainerCustomizer}
    // */
    // @Bean
    // public EmbeddedServletContainerCustomizer jettyCustomizer()
    // {
    // return container -> {
    // if (container instanceof JettyEmbeddedServletContainerFactory)
    // {
    // ((JettyEmbeddedServletContainerFactory) container).setThreadPool(threadPool);
    // }
    // };
    // }
}
