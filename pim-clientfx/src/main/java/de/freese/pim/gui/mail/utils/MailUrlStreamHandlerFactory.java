// Created: 06.02.2017
package de.freese.pim.gui.mail.utils;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * {@link URLStreamHandlerFactory} für eine Mail.<br>
 *
 * <pre>
 * URL.setURLStreamHandlerFactory(new MailUrlStreamHandlerFactory());
 *
 * URLConnection connection = new URL("cid:blabla").openConnection();
 * connection.connect();
 * </pre>
 *
 * @author Thomas Freese
 * @see InlineUrlStreamHandler
 * @see InlineUrlConnection
 */
public class MailUrlStreamHandlerFactory implements URLStreamHandlerFactory
{
    // /**
    // *
    // */
    // private final static Logger LOGGER = LoggerFactory.getLogger(MailUrlStreamHandlerFactory.class);

    /**
     * Erzeugt eine neue Instanz von {@link MailUrlStreamHandlerFactory}
     */
    public MailUrlStreamHandlerFactory()
    {
        super();
    }

    /**
     * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol)
    {
        // LOGGER.info(protocol);

        if ("cid".equals(protocol))
        {
            // Find Inline
            return new InlineUrlStreamHandler();
        }

        // Kopie von sun.misc.Launcher$Factory
        String name = "sun.net.www.protocol." + protocol + ".Handler";

        try
        {
            Class<?> clazz = Class.forName(name);

            return (URLStreamHandler) clazz.newInstance();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        throw new InternalError("could not load " + protocol + "system protocol handler");
    }
}
