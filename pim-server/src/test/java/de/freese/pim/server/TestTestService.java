// Created: 14.02.2017
package de.freese.pim.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Vollständiger Test der gesamten Server-Anwendung.
 *
 * @author Thomas Freese
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes =
{
        PIMServerApplication.class
}, properties =
{
        "spring.main.banner-mode=OFF", "logging.config=classpath:logback-server.xml" // , "spring.config.name=application-Server"
})
@ActiveProfiles(
{
        "Server", "HsqldbMemory"
})
@AutoConfigureMockMvc
@DirtiesContext
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTestService
{
    /**
     *
     */
    @LocalServerPort
    private String localServerPort = null;

    /**
     *
     */
    @Resource
    private MockMvc mockMvc = null;

    /**
     * Erzeugt eine neue Instanz von {@link TestTestService}
     */
    public TestTestService()
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
            //  @formatter:on

            results.add(mvcResult);
        }

        for (MvcResult mvcResult : results)
        {
            // @formatter:off
            this.mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().is2xxSuccessful());
            // @formatter:off

            Assert.assertTrue(mvcResult.getAsyncResult().toString().startsWith("201"));
        }
    }
}
