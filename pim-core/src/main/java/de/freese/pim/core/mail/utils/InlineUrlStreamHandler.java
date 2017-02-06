// Created: 06.02.2017
package de.freese.pim.core.mail.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import de.freese.pim.core.mail.model.MailContent;

/**
 * {@link URLStreamHandler} für ein Inline einer HTML-Mail.
 *
 * @author Thomas Freese
 * @see InlineUrlConnection
 */
public class InlineUrlStreamHandler extends URLStreamHandler
{
    /**
     *
     */
    private final MailContent mailContent;

    /**
     * Erzeugt eine neue Instanz von {@link InlineUrlStreamHandler}
     *
     * @param mailContent {@link MailContent}
     */
    public InlineUrlStreamHandler(final MailContent mailContent)
    {
        super();

        this.mailContent = mailContent;
    }

    /**
     * @see java.net.URLStreamHandler#openConnection(java.net.URL)
     */
    @Override
    protected URLConnection openConnection(final URL url) throws IOException
    {
        return new InlineUrlConnection(this.mailContent, url);
    }
}
