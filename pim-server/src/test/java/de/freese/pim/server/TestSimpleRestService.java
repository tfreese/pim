// Created: 14.02.2017
package de.freese.pim.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import de.freese.pim.server.spring.TestService;

/**
 * @author Thomas Freese
 */
@Configuration
// @Profile("SimpleRestService")
class Config extends WebMvcConfigurationSupport
{
    // static{
    // System.setProperty("spring.main.banner-mode", "OFF");
    // System.setProperty("logging.config", "logback-test.xml");
    // }

    /**
     * Erzeugt eine neue Instanz von {@link Config}
     */
    Config()
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
        // Ansonsten würde für jeden Request immer ein neuer Thread erzeugt.
        configurer.setTaskExecutor(taskScheduler());
    }

    // /**
    // * @return {@link AsyncTaskExecutor}
    // */
    // @Bean
    // public AsyncTaskExecutor taskExecutor()
    // {
    // ThreadPoolTaskExecutor bean = new ThreadPoolTaskExecutor();
    // bean.setCorePoolSize(8);
    // bean.setMaxPoolSize(8);
    // bean.setQueueCapacity(100);
    // bean.setKeepAliveSeconds(0);
    // bean.setThreadNamePrefix("test-");
    // bean.setThreadPriority(Thread.NORM_PRIORITY);
    // bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    // bean.setAllowCoreThreadTimeOut(false);
    //
    // return bean;
    // }

    // /**
    // * @param executorService {@link ExecutorService}
    // * @return {@link AsyncTaskExecutor}
    // */
    // @Bean
    // public AsyncTaskExecutor taskExecutor(final ExecutorService executorService)
    // {
    // AsyncTaskExecutor bean = new ConcurrentTaskExecutor(executorService);
    //
    // return bean;
    // }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    protected ThreadPoolExecutorFactoryBean executorService()
    {
        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(8);
        bean.setMaxPoolSize(8);
        bean.setKeepAliveSeconds(0);
        bean.setQueueCapacity(100);
        bean.setThreadPriority(5);
        bean.setThreadNamePrefix("test-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @return {@link ScheduledExecutorFactoryBean}
     */
    @Bean
    protected ScheduledExecutorFactoryBean scheduledExecutorService()
    {
        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(4);
        bean.setThreadPriority(5);
        bean.setThreadNamePrefix("testscheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @return {@link TaskScheduler}
     */
    @Bean(
    {
            "taskScheduler", "taskExecutor"
    })
    public ConcurrentTaskScheduler taskScheduler()
    {
        ConcurrentTaskScheduler bean = new ConcurrentTaskScheduler(executorService().getObject(), scheduledExecutorService().getObject());

        return bean;
    }
}

/**
 * @author Thomas Freese
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TestService.class) // Nur für diesen einen Service, ohne weitere Abhängigkeiten.
@Import(Config.class)
@ActiveProfiles("SimpleRestService")
@DirtiesContext
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSimpleRestService
{
    /**
     *
     */
    @Resource
    private MockMvc mockMvc = null;

    /**
     * Erzeugt eine neue Instanz von {@link TestSimpleRestService}
     */
    public TestSimpleRestService()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010NoParamGreetingShouldReturnDefaultMessage() throws Exception
    {
        // .andDo(print()).andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        // @formatter:off
        this.mockMvc.perform(get("/test/greeting"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().json("{\"hello\":\"World\"}"));
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020ParamGreetingShouldReturnTailoredMessage() throws Exception
    {
        // .andDo(print()).andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        // @formatter:off
        this.mockMvc.perform(get("/test/greeting").param("name", "Spring Community"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("{\"hello\":\"Spring Community\"}"));
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030AsyncDateDeferredResult() throws Exception
    {
        testAsync("/test/asyncDateDeferredResult");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040AsyncDateCallable() throws Exception
    {
        testAsync("/test/asyncDateCallable");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040AsyncDateWebAsyncTask() throws Exception
    {
        testAsync("/test/asyncDateWebAsyncTask");
    }

    /**
     * Testet die asynchrone Ausführung einer bestimmten URL.
     *
     * @param url String
     * @throws Exception Falls was schief geht.
     */
    private void testAsync(final String url) throws Exception
    {
        List<MvcResult> results = new ArrayList<>();

        for (int i = 0; i < 20; i++)
        {
            // @formatter:off
            MvcResult mvcResult = this.mockMvc.perform(get(url))
                    .andExpect(MockMvcResultMatchers.request().asyncStarted())
                    .andReturn();
            // @formatter:on

            results.add(mvcResult);
        }

        for (MvcResult mvcResult : results)
        {
            // @formatter:off
            this.mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().is2xxSuccessful());
            // @formatter:on

            Assert.assertTrue(mvcResult.getAsyncResult().toString().startsWith("201"));
        }
    }
}
