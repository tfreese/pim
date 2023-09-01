// Created: 11.01.2017
package de.freese.pim.core.utils.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class MonitorInputStream extends FilterInputStream {

    private final IOMonitor monitor;

    /**
     * Anzahl Bytes (Größe) des gesamten Channels.
     */
    private final long size;

    /**
     * Anzahl gelesener Bytes.
     */
    private long sizeRead;

    /**
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     */
    public MonitorInputStream(final InputStream delegate, final IOMonitor monitor, final long size) {
        super(delegate);

        this.monitor = Objects.requireNonNull(monitor, "monitor required");
        this.size = size;
    }

    @Override
    public int read() throws IOException {
        int read = super.read();

        this.sizeRead++;

        this.monitor.monitor(this.sizeRead, this.size);

        return read;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int readCount = super.read(b, off, len);

        if (readCount > 0) {
            this.sizeRead += readCount;

            this.monitor.monitor(this.sizeRead, this.size);
        }

        return readCount;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();

        this.sizeRead = this.size - super.available();

        this.monitor.monitor(this.sizeRead, this.size);
    }

    @Override
    public long skip(final long n) throws IOException {
        long readCount = super.skip(n);

        if (readCount > 0) {
            this.sizeRead += readCount;

            this.monitor.monitor(this.sizeRead, this.size);
        }

        return readCount;
    }
}
