// Created: 11.01.2017
package de.freese.pim.core.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

/**
 * {@link ReadableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringReadableByteChannel implements ReadableByteChannel {
    private final ReadableByteChannel delegate;
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
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final IOMonitor monitor, final long size) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.monitor = Objects.requireNonNull(monitor, "monitor required");
        this.size = size;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
        final int readCount = delegate.read(dst);

        if (readCount > 0) {
            sizeRead += readCount;

            monitor.monitor(sizeRead, size);
        }

        return readCount;
    }
}
