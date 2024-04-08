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

    @Override
    public void connect() throws IOException {
        this.connected = true;
    }

    @Override
    public String getContentEncoding() {
        return this.mailContent.getEncoding();
    }

    @Override
    public String getContentType() {
        if (this.dataSource == null) {
            return "";
        }

        return this.dataSource.getContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.dataSource == null) {
            return InputStream.nullInputStream();
        }

        return this.dataSource.getInputStream();
    }
}
