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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Server Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("Server")
@ComponentScan(basePackages = {"de.freese.pim.server", "de.freese.pim.core"})
public class ServerConfig implements WebMvcConfigurer // extends WebMvcConfigurationSupport
{
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AsyncTaskExecutor taskExecutor;

    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        // Executer für die Verarbeitung der HTTP-Requests.
        // Verlagert die asynchrone Ausführung von Server-Requests (Callable, WebAsyncTask) in diesen ThreadPool.
        // Ansonsten würde für jeden Request immer ein neuer Thread erzeugt, siehe TaskExecutor des RequestMappingHandlerAdapter.
        configurer.setTaskExecutor(taskExecutor);
    }

    /**
     * Note that use of this method turns off default converter registration.
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.clear();
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        // Make sure dates are serialised in ISO-8601 format instead as timestamps.
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jsonMessageConverter) {
                // Only required with default converter registration.
                // See: configureMessageConverters
                if (!jsonMessageConverter.getObjectMapper().equals(objectMapper)) {
                    jsonMessageConverter.setObjectMapper(objectMapper);
                }

                break;
            }
        }
    }

    // @Bean(destroyMethod = "disconnectAccounts")
    // public MailService mailService(final DataSource dataSource, final ExecutorService executorService) {
    //     final DefaultMailService bean = new DefaultMailService();
    //     bean.setMailDAO(new DefaultMailDao().dataSource(dataSource));
    //     bean.setExecutorService(executorService);
    //     //
    //     // return (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[] {
    //     // IMailService.class
    //     // }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));
    // }

    // @Bean(initMethod = "migrate")
    // // @DependsOn("dataSource")
    // public Flyway flyway(final DataSource dataSource) {
    //     final Flyway flyway = new Flyway();
    //     flyway.setEncoding("UTF-8");
    //     flyway.setBaselineOnMigrate(true);
    //     flyway.setDataSource(dataSource);
    //     // flyway.setLocations("filesystem:/path/to/migrations/");
    //     flyway.setLocations("classpath:db/hsqldb");
    //
    //     return flyway;
    // }

    // @Bean
    // public EmbeddedServletContainerCustomizer tomcatCustomizer() {
    //     return container -> {
    //         if (container instanceof TomcatEmbeddedServletContainerFactory) {
    //             ((TomcatEmbeddedServletContainerFactory) container).addConnectorCustomizers(gracefulShutdown());
    //         }
    //     };
    // }

    // @Bean
    // public EmbeddedServletContainerCustomizer jettyCustomizer() {
    //     return container -> {
    //         if (container instanceof JettyEmbeddedServletContainerFactory) {
    //             ((JettyEmbeddedServletContainerFactory) container).setThreadPool(threadPool);
    //         }
    //     };
    // }
}
