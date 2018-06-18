// Created: 10.02.2017
package de.freese.pim.gui.spring.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.util.SocketUtils;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Client Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("ClientEmbeddedServer")
@ComponentScan(basePackages =
{
        "de.freese.pim"
})
public class PIMClientEmbeddedServerConfig extends WebMvcConfigurationSupport
{
    static
    {
        // @LocalServerPort oder @Value("${local.server.port}") funktionieren nur in Tests.
        int port = SocketUtils.findAvailableTcpPort();

        // Damit die Placeholder in Properties funktionieren: ${server.port}
        System.setProperty("server.port", Integer.toString(port));
    }

    /**
    *
    */
    @Resource
    private ObjectMapper jsonMapper = null;

    /**
     * Erzeugt eine neue Instanz von {@link PIMClientEmbeddedServerConfig}
     */
    public PIMClientEmbeddedServerConfig()
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
        configurer.setTaskExecutor(taskExecutor(executorService().getObject()));
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorService()
    {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        int maxSize = coreSize;
        int queueSize = maxSize * 5;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setKeepAliveSeconds(0);
        bean.setQueueCapacity(queueSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("client-");
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
     * @param pimHome String
     * @return {@link Path}
     */
    @Bean
    @Primary
    public Path pimHomePath(@Value("${pim.home}") final String pimHome)
    {
        Path path = Paths.get(pimHome);

        return path;
    }

    /**
     * @Value("${server.port}") final int serverPort
     *
     * @param serverPort int
     * @return {@link RestTemplateBuilder}
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder(@Value("${server.port}") final int serverPort)
    {
        // RestTemplateBuilder bean = new RestTemplateBuilder().rootUri(rootUri).basicAuthorization(username, password);
        String url = String.format("http://localhost:%d/pim", serverPort);
        RestTemplateBuilder bean = new RestTemplateBuilder().rootUri(url);

        return bean;

        // RestTemplate rt = new RestTemplate();
        // rt.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        // rt.getMessageConverters().add(new StringHttpMessageConverter());
    }

    /**
     * @param executorService {@link ExecutorService}
     * @return {@link AsyncTaskExecutor}
     */
    @Bean(
    {
            "taskExecutor", "serverTaskExecutor"
    })
    public AsyncTaskExecutor taskExecutor(final ExecutorService executorService)
    {
        AsyncTaskExecutor bean = new ConcurrentTaskExecutor(executorService);

        return bean;
    }

    // @Component
    // public class MyListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    //
    // @Override
    // public void onApplicationEvent(final EmbeddedServletContainerInitializedEvent event) {
    // int thePort = event.getEmbeddedServletContainer().getPort();
    // }
    // }

    // /**
    // * @return {@link EmbeddedServletContainerCustomizer}
    // */
    // @Bean
    // public EmbeddedServletContainerCustomizer tomcatCustomizer()
    // {
    // return container ->
    // {
    // container.setPort(this.nextFreePort);
    // // if (container instanceof TomcatEmbeddedServletContainerFactory)
    // // {
    // // ((TomcatEmbeddedServletContainerFactory) container).setPort(this.nextFreePort);
    // // }
    // // else if(container instanceof JettyEmbeddedServletContainerFactory)
    // // {
    // // ((JettyEmbeddedServletContainerFactory) container).setPort(this.nextFreePort);
    // // }
    // };
    // }
}
