// Created: 06.02.2017
package de.freese.pim.gui.mail.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import jakarta.activation.DataSource;

import de.freese.pim.core.mail.MailContent;

/**
 * {@link URLConnection} für ein Inline einer HTML-Mail.
 *
 * @author Thomas Freese
 */
public class InlineUrlConnection extends URLConnection {
    private final DataSource dataSource;

    private final MailContent mailContent;

    public InlineUrlConnection(final MailContent mailContent, final URL url) throws IOException {
        super(url);

        this.mailContent = mailContent;
        this.dataSource = mailContent.getInlines().get(url.getPath());
    }

    /**
     * @see java.net.URLConnection#connect()
     */
    @Override
    public void connect() throws IOException {
        this.connected = true;
    }

    /**
     * @see java.net.URLConnection#getContentEncoding()
     */
    @Override
    public String getContentEncoding() {
        return this.mailContent.getEncoding();
    }

    /**
     * @see java.net.URLConnection#getContentType()
     */
    @Override
    public String getContentType() {
        if (this.dataSource == null) {
            return "";
        }

        return this.dataSource.getContentType();
    }

    /**
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.dataSource == null) {
            return InputStream.nullInputStream();
        }

        return this.dataSource.getInputStream();
    }
}
