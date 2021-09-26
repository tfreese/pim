// Created: 26.10.2016
package de.freese.pim.common.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

/**
 * {@link WritableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringWritableByteChannel implements WritableByteChannel
{
    /**
     *
     */
    private final WritableByteChannel delegate;
    /**
     *
     */
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
     * Erzeugt eine neue Instanz von {@link MonitoringWritableByteChannel}
     *
     * @param delegate {@link WritableByteChannel}
     * @param monitor {@link IOMonitor}
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     */
    public MonitoringWritableByteChannel(final WritableByteChannel delegate, final IOMonitor monitor, final long size)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, () -> "delegate required");
        this.monitor = Objects.requireNonNull(monitor, () -> "monitor required");
        this.size = size;
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
     * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
     */
    @Override
    public int write(final ByteBuffer src) throws IOException
    {
        int writeCount = this.delegate.write(src);

        if (writeCount > 0)
        {
            this.sizeWritten += writeCount;

            this.monitor.monitor(this.sizeWritten, this.size);
        }

        return writeCount;
    }
}
