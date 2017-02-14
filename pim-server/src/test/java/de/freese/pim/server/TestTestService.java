// Created: 14.02.2017
package de.freese.pim.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Thomas Freese
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties =
{
        "spring.config.name=application-server", "spring.main.banner-mode=OFF", "logging.config=classpath:logback-test.xml"
}, webEnvironment = WebEnvironment.RANDOM_PORT)
// @SpringBootTest(classes = PIMServerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties =
// {
// "spring.config.name=application-server"
// })
@AutoConfigureMockMvc
public class TestTestService
{
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
    public void noParamGreetingShouldReturnDefaultMessage() throws Exception
    {
        // this.mockMvc.perform(get("/greeter/test")).andDo(print()).andExpect(status().isOk())
        // .andExpect(jsonPath("$.content").value("Hello, World!"));

        // @formatter:off
        this.mockMvc.perform(get("/greeter/test"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().json("{\"hello\":\"World\"}"));
        // @formatter:off
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void paramGreetingShouldReturnTailoredMessage() throws Exception
    {
        // this.mockMvc.perform(get("/greeter/test").param("name", "Spring Community")).andDo(print()).andExpect(status().isOk())
        // .andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        // @formatter:off
        this.mockMvc.perform(get("/greeter/test")
                .param("name", "Spring Community"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("{\"hello\":\"Spring Community\"}"));
        // @formatter:on
    }
}
