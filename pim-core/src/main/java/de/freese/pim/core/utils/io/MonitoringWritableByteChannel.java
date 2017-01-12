// Created: 26.10.2016
package de.freese.pim.core.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * {@link WritableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 * Der Monitor empfängt die Gesamtgröße (Parameter 1) und Anzahl geschriebener Bytes (Parameter 2).<br>
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
    private final BiConsumer<Long, Long> monitor;

    /**
     * Anzahl Bytes (Größe) des gesamten Channels.
     */
    private final long size;

    /**
     * Anzahl geschriebene Bytes.
     */
    private long sizeWritten = 0;

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringWritableByteChannel}
     *
     * @param delegate {@link WritableByteChannel}
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param monitor {@link BiConsumer}; Erster Parameter = Gesamtgröße, zweiter Parameter = Anzahl geschriebene Bytes
     */
    public MonitoringWritableByteChannel(final WritableByteChannel delegate, final long size, final BiConsumer<Long, Long> monitor)
    {
        super();

        Objects.requireNonNull(delegate, "delegate required");
        Objects.requireNonNull(monitor, "monitor required");

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
     * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
     */
    @Override
    public int write(final ByteBuffer src) throws IOException
    {
        int writeCount = this.delegate.write(src);

        if (writeCount > 0)
        {
            this.sizeWritten += writeCount;

            this.monitor.accept(this.size, this.sizeWritten);
        }

        return writeCount;
    }
}
