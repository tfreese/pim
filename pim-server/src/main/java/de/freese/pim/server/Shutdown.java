// Created: 11.08.2016
package de.freese.pim.server;

import org.springframework.web.client.RestTemplate;

/**
 * Sendet das shutdown-Signal.
 *
 * @author Thomas Freese
 */
public class Shutdown
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        RestTemplate restTemplate = new RestTemplate();
        // restTemplate.exchange("http://localhost:61222/pim/shutdown", HttpMethod.POST, null, Void.class);
        restTemplate.postForLocation("http://localhost:61223/pim/shutdown", null);

        // URL url = new URL("http://localhost:9000/shutdown");
        // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // connection.setRequestMethod("POST");
        // connection.getResponseCode();
        // connection.disconnect();
    }

    /**
     * Erzeugt eine neue Instanz von {@link Shutdown}
     */
    public Shutdown()
    {
        super();
    }
}
