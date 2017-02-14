// Created: 14.02.2017
package de.freese.pim.server.spring;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * https://spring.io/guides/tutorials/bookmarks/
 * 
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/greeter", produces =
{
        MediaType.APPLICATION_JSON_UTF8_VALUE
}, headers = "Accept=application/json")
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
    @RequestMapping(path = "/test", method = RequestMethod.GET)
    // @RequestMapping("/test/{name}"); @PathVariable(value = "name")
    public Map<String, String> greeting(@RequestParam(value = "name", defaultValue = "World") final String name)
    {
        Map<String, String> map = new HashMap<>();
        map.put("hello", name);

        return map;
    }
}
