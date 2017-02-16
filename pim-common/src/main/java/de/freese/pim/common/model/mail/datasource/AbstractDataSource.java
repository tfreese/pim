// Created: 16.02.2017
package de.freese.pim.common.model.mail.datasource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.springframework.util.StreamUtils;

/**
 * Basis-Implementierung einer {@link DataSource}.
 *
 * @author Thomas Freese
 */
public class AbstractDataSource implements DataSource
{
    /**
     *
     */
    private final String contentType;

    /**
     *
     */
    private final byte[] data;

    /**
     *
     */
    private final String name;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractDataSource}
     *
     * @param source {@link DataSource}
     * @throws IOException Falls was schief geht.
     */
    public AbstractDataSource(final DataSource source) throws IOException
    {
        super();

        this.name = source.getName();
        this.contentType = source.getContentType();
        this.data = StreamUtils.copyToByteArray(source.getInputStream());
    }

    /**
     * Erzeugt eine neue Instanz von {@link AbstractDataSource}
     *
     * @param name String
     * @param contentType String
     * @param data byte[]
     */
    public AbstractDataSource(final String name, final String contentType, final byte[] data)
    {
        super();

        this.name = name;
        this.contentType = contentType;
        this.data = data;
    }

    /**
     * @see javax.activation.DataSource#getContentType()
     */
    @Override
    public String getContentType()
    {
        return this.contentType;
    }

    /**
     * @see javax.activation.DataSource#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException
    {
        if (this.data == null)
        {
            throw new IOException("no data");
        }

        return new ByteArrayInputStream(this.data);
    }

    /**
     * @see javax.activation.DataSource#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see javax.activation.DataSource#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException
    {
        throw new UnsupportedOperationException("not implemented");
    }
}
