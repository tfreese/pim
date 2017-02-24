// Created: 17.02.2017
package de.freese.pim.server.spring.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import de.freese.pim.common.spring.autoconfigure.taskexcecutor.TaskExcecutorAutoConfiguration;

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
     * @see ConcurrentTaskExecutor
     * @see TaskExcecutorAutoConfiguration
     */
    @Resource
    private AsyncTaskExecutor taskExecutor = null;

    /**
     * Erzeugt eine neue Instanz von {@link ServerConfig}
     */
    public ServerConfig()
    {
        super();
    }

    // /**
    // * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#extendMessageConverters(java.util.List)
    // */
    // @Override
    // public void extendMessageConverters(final List<HttpMessageConverter<?>> converters)
    // {
    // // Make sure dates are serialised in ISO-8601 format instead as timestamps
    // for (HttpMessageConverter<?> converter : converters)
    // {
    // if (converter instanceof MappingJackson2HttpMessageConverter)
    // {
    // MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) converter;
    // ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
    // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //
    // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // df.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
    // objectMapper.setDateFormat(df);
    //
    // break;
    // }
    // }
    // }

    /**
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#configureAsyncSupport(org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer)
     */
    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer)
    {
        // Verlagert die asynchrone Ausführung von Server-Requests (Callable, WebAsyncTask) in diesen ThreadPool.
        // Ansonsten würde für jeden Request immer ein neuer Thread erzeugt.
        configurer.setTaskExecutor(this.taskExecutor);
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
