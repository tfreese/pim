// Created: 11.01.2017
package de.freese.pim.common.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

/**
 * {@link ReadableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringReadableByteChannel implements ReadableByteChannel
{
    /**
    *
    */
    private final ReadableByteChannel delegate;

    /**
    *
    */
    private final IOMonitor monitor;

    /**
     * Anzahl Bytes (Größe) des gesamten Channels.
     */
    private final long size;

    /**
     * Anzahl gelesener Bytes.
     */
    private long sizeRead = 0;

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringReadableByteChannel}
     *
     * @param delegate {@link ReadableByteChannel}
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param monitor {@link IOMonitor}
     */
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final long size, final IOMonitor monitor)
    {
        super();

        Objects.requireNonNull(delegate, () -> "delegate required");
        Objects.requireNonNull(monitor, () -> "monitor required");

        this.delegate = delegate;
        this.size = size;
        this.monitor = monitor;
    }

    /**
     * @see java.nio.channels.Channel#close()
     */
    @Override
    public void close() throws IOException
    {
        this.delegate.close();
    }

    /**
     * @see java.nio.channels.Channel#isOpen()
     */
    @Override
    public boolean isOpen()
    {
        return this.delegate.isOpen();
    }

    /**
     * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException
    {
        int readCount = this.delegate.read(dst);

        if (readCount > 0)
        {
            this.sizeRead += readCount;

            this.monitor.monitor(this.sizeRead, this.size);
        }

        return readCount;
    }
}
