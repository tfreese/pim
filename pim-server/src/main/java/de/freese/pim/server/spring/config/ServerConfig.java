// Created: 17.02.2017
package de.freese.pim.server.spring.config;

import java.util.List;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Server Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("Server")
@ComponentScan(basePackages = {"de.freese.pim.server", "de.freese.pim.core"})
public class ServerConfig extends WebMvcConfigurationSupport // implements WebMvcConfigurer
{
    @Resource
    private ObjectMapper jsonMapper;

    @Resource
    private AsyncTaskExecutor taskExecutor;

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        // Make sure dates are serialised in ISO-8601 format instead as timestamps
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jsonMessageConverter) {
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

    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        // Executer für die Verarbeitung der HTTP-Requests.
        // Verlagert die asynchrone Ausführung von Server-Requests (Callable, WebAsyncTask) in diesen ThreadPool.
        // Ansonsten würde für jeden Request immer ein neuer Thread erzeugt, siehe TaskExecutor des RequestMappingHandlerAdapter.
        configurer.setTaskExecutor(this.taskExecutor);
    }

    // @Bean(destroyMethod = "disconnectAccounts")
    // public MailService mailService(final DataSource dataSource, final ExecutorService executorService)
    // {
    // DefaultMailService bean = new DefaultMailService();
    // bean.setMailDAO(new DefaultMailDao().dataSource(dataSource));
    // bean.setExecutorService(executorService);
    // //
    // // return (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
    // // {
    // // IMailService.class
    // // }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));
    // }

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
