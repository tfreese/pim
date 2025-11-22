// Created: 17.02.2017
package de.freese.pim.server.spring.config;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;

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
    private JsonMapper jsonMapper;

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
    public void configureMessageConverters(final HttpMessageConverters.ServerBuilder builder) {
        builder.addCustomConverter(new JacksonJsonHttpMessageConverter(jsonMapper));
        // converters.clear();
        // converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
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
