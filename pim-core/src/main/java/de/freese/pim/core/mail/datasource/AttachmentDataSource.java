// Created: 16.02.2017
package de.freese.pim.core.mail.datasource;

import java.io.IOException;

import javax.activation.DataSource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.freese.pim.core.utils.io.IOMonitor;

/**
 * {@link DataSource} für ein Mail-Attachment.
 *
 * @author Thomas Freese
 */
public class AttachmentDataSource extends AbstractDataSource
{
    /**
     * Erzeugt eine neue Instanz von {@link AttachmentDataSource}
     *
     * @param source {@link DataSource}
     * @throws IOException Falls was schief geht.
     */
    public AttachmentDataSource(final DataSource source) throws IOException
    {
        super(source);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AttachmentDataSource}
     *
     * @param source {@link DataSource}
     * @param monitor {@link IOMonitor}
     * @throws IOException Falls was schief geht.
     */
    public AttachmentDataSource(final DataSource source, final IOMonitor monitor) throws IOException
    {
        super(source, monitor);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AttachmentDataSource}
     *
     * @param name String
     * @param contentType String
     * @param data byte[]
     */
    @JsonCreator
    public AttachmentDataSource(@JsonProperty("name") final String name, @JsonProperty("contentType") final String contentType,
            @JsonProperty("data") final byte[] data)
    {
        super(name, contentType, data);
    }
}
