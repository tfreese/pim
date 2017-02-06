// Created: 06.02.2017
package de.freese.pim.core.mail.utils;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.mail.model.MailContent;

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
    /**
     *
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(MailUrlStreamHandlerFactory.class);

    /**
     *
     */
    private final MailContent mailContent;

    /**
     * Erzeugt eine neue Instanz von {@link MailUrlStreamHandlerFactory}
     *
     * @param mailContent {@link MailContent}
     */
    public MailUrlStreamHandlerFactory(final MailContent mailContent)
    {
        super();

        Objects.requireNonNull(mailContent, "mailContent required");

        this.mailContent = mailContent;
    }

    /**
     * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol)
    {
        LOGGER.info(protocol);

        if ("cid".equals(protocol))
        {
            // TODO Find Inline
            return new InlineUrlStreamHandler(this.mailContent);
        }

        return null;
    }
}
