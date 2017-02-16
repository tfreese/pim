// Created: 16.02.2017
package de.freese.pim.common.model.mail.datasource;

import java.io.IOException;

import javax.activation.DataSource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link DataSource} für ein Mail-Inline.
 *
 * @author Thomas Freese
 */
public class InlineDataSource extends AbstractDataSource
{
    /**
     * Erzeugt eine neue Instanz von {@link InlineDataSource}
     *
     * @param source {@link DataSource}
     * @throws IOException Falls was schief geht.
     */
    public InlineDataSource(final DataSource source) throws IOException
    {
        super(source);
    }

    /**
     * Erzeugt eine neue Instanz von {@link InlineDataSource}
     *
     * @param name String
     * @param contentType String
     * @param data byte[]
     */
    @JsonCreator
    public InlineDataSource(@JsonProperty("name") final String name, @JsonProperty("contentType") final String contentType,
            @JsonProperty("data") final byte[] data)
    {
        super(name, contentType, data);
    }
}
