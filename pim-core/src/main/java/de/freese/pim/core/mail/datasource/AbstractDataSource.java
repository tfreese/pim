// Created: 16.02.2017
package de.freese.pim.core.mail.datasource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataSource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StreamUtils;

import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.core.utils.io.MonitorOutputStream;

/**
 * Basis-Implementierung einer {@link DataSource}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDataSource implements DataSource {
    private final String contentType;

    private final byte[] data;

    private final String name;

    protected AbstractDataSource(final DataSource source) throws IOException {
        this(source, null);
    }

    protected AbstractDataSource(final DataSource source, final IOMonitor monitor) throws IOException {
        super();

        name = source.getName();
        contentType = source.getContentType();

        try (InputStream inputStream = source.getInputStream()) {
            if (monitor == null) {
                data = StreamUtils.copyToByteArray(inputStream);
            }
            else {
                try (FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1024);
                     // try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                     OutputStream mos = new MonitorOutputStream(baos, monitor, 0)) {
                    StreamUtils.copy(inputStream, mos);
                    mos.flush();
                    data = baos.toByteArray();
                }
            }
        }
    }

    protected AbstractDataSource(final String name, final String contentType, final byte[] data) {
        super();

        this.name = name;
        this.contentType = contentType;
        this.data = data;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    @JsonIgnore
    public InputStream getInputStream() throws IOException {
        if (data == null) {
            throw new IOException("no data");
        }

        return new ByteArrayInputStream(data);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @JsonIgnore
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("not implemented");
    }
}
