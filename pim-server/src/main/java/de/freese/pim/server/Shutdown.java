// Created: 11.08.2016
package de.freese.pim.server;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import java.util.Properties;

/**
 * Sendet das shutdown-Signal.
 *
 * @author Thomas Freese
 */
public class Shutdown
{
    /**
     * Erzeugt eine neue Instanz von {@link Shutdown}
     */
    public Shutdown()
    {
        super();
    }

    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:application-Server.properties");

        Properties props = new Properties();

        if (resource.isReadable())
        {
            props.load(resource.getInputStream());
        }

        int port = Integer.parseInt(Optional.ofNullable(props.getProperty("server.port")).orElse("61222"));
        Optional<String> contextPath = Optional.ofNullable(props.getProperty("server.servlet.context-path"));

        URI uri = URI.create("http://localhost:" + port + contextPath.orElse("") + "/actuator/shutdown");

        // RestTemplate restTemplate = new RestTemplate();
        // restTemplate.exchange(repository, HttpMethod.POST, null, Void.class);
        // restTemplate.postForLocation(repository, null);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.getResponseCode();
        connection.disconnect();
    }
}
