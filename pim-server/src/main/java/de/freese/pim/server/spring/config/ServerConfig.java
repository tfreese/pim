// Created: 17.02.2017
package de.freese.pim.server.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
public class ServerConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link ServerConfig}
     */
    public ServerConfig()
    {
        super();
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
