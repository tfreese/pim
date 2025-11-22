// Created: 14.02.2017
package de.freese.pim.server.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import de.freese.pim.server.PimServerApplication;

/**
 * Vollständiger Test der gesamten Server-Anwendung.
 *
 * @author Thomas Freese
 */
// @ExtendWith(SpringExtension.class) // Ist bereits in SpringBootTest enthalten
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {PimServerApplication.class},
        properties = {"spring.main.banner-mode=OFF", "logging.config=classpath:logback-server.xml" // , "spring.config.name=application-Server"
        })
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles({"Server", "HsqldbMemory"})
@AutoConfigureMockMvc
class TestRestServer {
    @LocalServerPort
    private String localServerPort;

    @Resource
    private MockMvc mockMvc;

    @Test
    void test010NoParamGreetingShouldReturnDefaultMessage() throws Exception {
        // .andDo(print()).andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        mockMvc.perform(get("/test/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{\"hello\":\"World\"}"));
    }

    @Test
    void test020ParamGreetingShouldReturnTailoredMessage() throws Exception {
        // .andDo(print()).andExpect(jsonPath("$.content").value("Hello, Spring Community!"));

        mockMvc.perform(get("/test/greeting").param("name", "Spring Community"))
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
            final MvcResult mvcResult = mockMvc.perform(get(url))
                    .andExpect(MockMvcResultMatchers.request().asyncStarted())
                    .andReturn();

            results.add(mvcResult);
        }

        for (MvcResult mvcResult : results) {
            mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                    .andExpect(status().is2xxSuccessful());

            Assertions.assertTrue(mvcResult.getAsyncResult().toString().startsWith("20"));
        }
    }
}
