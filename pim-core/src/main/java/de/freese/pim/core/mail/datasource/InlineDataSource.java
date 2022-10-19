// Created: 16.02.2017
package de.freese.pim.core.mail.datasource;

import java.io.IOException;

import jakarta.activation.DataSource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.freese.pim.core.utils.io.IOMonitor;

/**
 * {@link DataSource} für ein Mail-Inline.
 *
 * @author Thomas Freese
 */
public class InlineDataSource extends AbstractDataSource
{
    public InlineDataSource(final DataSource source) throws IOException
    {
        super(source);
    }

    public InlineDataSource(final DataSource source, final IOMonitor monitor) throws IOException
    {
        super(source, monitor);
    }

    @JsonCreator
    public InlineDataSource(@JsonProperty("name") final String name, @JsonProperty("contentType") final String contentType,
                            @JsonProperty("data") final byte[] data)
    {
        super(name, contentType, data);
    }
}
