// Created: 26.10.2016
package de.freese.pim.core.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

/**
 * {@link WritableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringWritableByteChannel implements WritableByteChannel {
    private final WritableByteChannel delegate;
    private final IOMonitor monitor;
    /**
     * Anzahl Bytes (Größe) des gesamten Channels.
     */
    private final long size;
    /**
     * Anzahl geschriebene Bytes.
     */
    private long sizeWritten;

    /**
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     */
    public MonitoringWritableByteChannel(final WritableByteChannel delegate, final IOMonitor monitor, final long size) {
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
    public int write(final ByteBuffer src) throws IOException {
        final int writeCount = delegate.write(src);

        if (writeCount > 0) {
            sizeWritten += writeCount;

            monitor.monitor(sizeWritten, size);
        }

        return writeCount;
    }
}
