// Created: 06.02.2017
package de.freese.pim.core.mail.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.activation.DataSource;

import de.freese.pim.core.mail.model.MailContent;

/**
 * {@link URLConnection} für ein Inline einer HTML-Mail.
 *
 * @author Thomas Freese
 */
public class InlineUrlConnection extends URLConnection
{
    /**
     *
     */
    private final DataSource dataSource;

    /**
     *
     */
    private final MailContent mailContent;

    /**
     * Erzeugt eine neue Instanz von {@link InlineUrlConnection}
     *
     * @param mailContent {@link MailContent}
     * @param url {@link URL}
     * @throws IOException Falls was schief geht.
     */
    public InlineUrlConnection(final MailContent mailContent, final URL url) throws IOException
    {
        super(url);

        this.mailContent = mailContent;
        this.dataSource = mailContent.getInlineDataSource(null);
    }

    /**
     * @see java.net.URLConnection#connect()
     */
    @Override
    public void connect() throws IOException
    {
        this.connected = true;
    }

    /**
     * @see java.net.URLConnection#getContentEncoding()
     */
    @Override
    public String getContentEncoding()
    {
        return this.mailContent.getEncoding();
    }

    /**
     * @see java.net.URLConnection#getContentType()
     */
    @Override
    public String getContentType()
    {
        return this.dataSource.getContentType();
    }

    /**
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException
    {
        return this.dataSource.getInputStream();
    }
}
