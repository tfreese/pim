// Created: 06.02.2017
package de.freese.pim.core.mail.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import de.freese.pim.core.mail.api.IMailContent;
import de.freese.pim.core.mail.impl.JavaMailContent;

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
    private static IMailContent mailContent = null;

    /**
     * @return {@link JavaMailContent}
     */
    public static IMailContent getMailContent()
    {
        return mailContent;
    }

    /**
     * @param mailContent {@link IMailContent}
     */
    public static void setMailContent(final IMailContent mailContent)
    {
        InlineUrlStreamHandler.mailContent = mailContent;
    }

    /**
     * Erzeugt eine neue Instanz von {@link InlineUrlStreamHandler}
     */
    public InlineUrlStreamHandler()
    {
        super();
    }

    /**
     * @see java.net.URLStreamHandler#openConnection(java.net.URL)
     */
    @Override
    protected URLConnection openConnection(final URL url) throws IOException
    {
        return new InlineUrlConnection(InlineUrlStreamHandler.mailContent, url);
    }
}
