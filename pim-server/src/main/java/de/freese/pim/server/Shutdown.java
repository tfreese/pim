// Created: 11.08.2016
package de.freese.pim.server;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import java.util.Properties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * Sendet das shutdown-Signal.
 *
 * @author Thomas Freese
 */
public class Shutdown
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:application-Server.properties");

        Properties props = new Properties();

        if (resource.isReadable())
        {
            try (InputStream inputStream = resource.getInputStream())
            {
                props.load(inputStream);
            }
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

    /**
     * Erzeugt eine neue Instanz von {@link Shutdown}
     */
    public Shutdown()
    {
        super();
    }
}
