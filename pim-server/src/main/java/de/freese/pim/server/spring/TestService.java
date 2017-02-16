// Created: 14.02.2017
package de.freese.pim.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * https://spring.io/guides/tutorials/bookmarks/<br>
 * <br>
 * https://twilblog.github.io/java/spring/rest/file/stream/2015/08/14/return-a-file-stream-from-spring-rest.html<br>
 * http://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers<br>
 * http://stackoverflow.com/questions/15781885/how-to-forward-large-files-with-resttemplate
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/greeter", produces =
{
        MediaType.APPLICATION_JSON_UTF8_VALUE
}, headers = "Accept=application/json")
// @MultipartConfig(fileSizeThreshold = 20971520)
public class TestService
{
    /**
     * Erzeugt eine neue Instanz von {@link TestService}
     */
    public TestService()
    {
        super();
    }

    /**
     * http://localhost:61222/greeter/test/?name=World
     *
     * @param name String
     * @return {@link Map}
     */
    @GetMapping(path = "/test")
    // @RequestMapping(path = "/test/{name}", method = RequestMethod.GET);
    // @PathVariable(value = "name")
    public Map<String, String> greeting(@RequestParam(value = "name", defaultValue = "World") final String name)
    {
        Map<String, String> map = new HashMap<>();
        map.put("hello", name);

        return map;
    }
}
