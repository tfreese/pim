// Created: 14.02.2017
package de.freese.pim.server.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * <pre>{@code
 * @Profile("SimpleRestService")
 * }</pre>
 *
 * @author Thomas Freese
 */
@Configuration
class Config extends WebMvcConfigurationSupport {
    // static{
    // System.setProperty("spring.main.banner-mode", "OFF");
    // System.setProperty("logging.config", "logback-test.xml");
    // }

    @Bean({"taskScheduler", "taskExecutor"})
    public ConcurrentTaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(executorService().getObject(), scheduledExecutorService().getObject());
    }

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

    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        // Verlagert die asynchrone Ausführung von Server-Requests (Callable, WebAsyncTask) in diesen ThreadPool.
        // Ansonsten würde für jeden Request immer ein neuer Thread erzeugt, siehe TaskExecutor des RequestMappingHandlerAdapter.
        configurer.setTaskExecutor(taskScheduler());
    }

    @Bean
    protected ThreadPoolExecutorFactoryBean executorService() {
        final ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
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

    @Bean
    protected ScheduledExecutorFactoryBean scheduledExecutorService() {
        final ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(4);
        bean.setThreadPriority(5);
        bean.setThreadNamePrefix("testScheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }
}

/**
 * @author Thomas Freese
 */
// @SpringBootTest // Kollidiert mit @WebMvcTest
@WebMvcTest(TestService.class) // Nur für diesen einen Service, ohne weitere Abhängigkeiten.
@Import(Config.class)
@ActiveProfiles("SimpleRestService")
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSimpleRestService {
    @Resource
    private MockMvc mockMvc;

    @Test
    void test010NoParamGreetingShouldReturnDefaultMessage() throws Exception {
        // .andDo(print()).andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        this.mockMvc.perform(get("/test/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{\"hello\":\"World\"}"));
    }

    @Test
    void test020ParamGreetingShouldReturnTailoredMessage() throws Exception {
        // .andDo(print()).andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        this.mockMvc.perform(get("/test/greeting").param("name", "Spring Community"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{\"hello\":\"Spring Community\"}"));
    }

    @Test
    void test030AsyncDateDeferredResult() throws Exception {
        testAsync("/test/asyncDateDeferredResult");
    }

    @Test
    void test040AsyncDateCallable() throws Exception {
        testAsync("/test/asyncDateCallable");
    }

    @Test
    void test040AsyncDateWebAsyncTask() throws Exception {
        testAsync("/test/asyncDateWebAsyncTask");
    }

    private void testAsync(final String url) throws Exception {
        final List<MvcResult> results = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            final MvcResult mvcResult = this.mockMvc.perform(get(url))
                    .andExpect(MockMvcResultMatchers.request().asyncStarted())
                    .andReturn();

            results.add(mvcResult);
        }

        for (MvcResult mvcResult : results) {
            this.mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                    .andExpect(status().is2xxSuccessful());

            Assertions.assertTrue(mvcResult.getAsyncResult().toString().startsWith("20"));
        }
    }
}
