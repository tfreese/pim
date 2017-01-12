// Created: 11.01.2017
package de.freese.pim.core.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * {@link ReadableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 * Der Monitor empfängt die Gesamtgröße (Parameter 1) und Anzahl gelesener Bytes (Parameter 2).<br>
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
    private final BiConsumer<Long, Long> monitor;

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
     * @param monitor {@link BiConsumer}; Erster Parameter = Gesamtgröße, zweiter Parameter = Anzahl gelesener Bytes
     */
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final long size, final BiConsumer<Long, Long> monitor)
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

            this.monitor.accept(this.size, this.sizeRead);
        }

        return readCount;
    }
}
