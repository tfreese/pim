// Created: 16.02.2017
package de.freese.pim.core.mail.datasource;

import java.io.IOException;

import jakarta.activation.DataSource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.freese.pim.core.utils.io.IOMonitor;

/**
 * {@link DataSource} für ein Mail-Attachment.
 *
 * @author Thomas Freese
 */
public class AttachmentDataSource extends AbstractDataSource {
    public AttachmentDataSource(final DataSource source) throws IOException {
        super(source);
    }

    public AttachmentDataSource(final DataSource source, final IOMonitor monitor) throws IOException {
        super(source, monitor);
    }

    @JsonCreator
    public AttachmentDataSource(@JsonProperty("name") final String name, @JsonProperty("contentType") final String contentType, @JsonProperty("data") final byte[] data) {
        super(name, contentType, data);
    }
}
