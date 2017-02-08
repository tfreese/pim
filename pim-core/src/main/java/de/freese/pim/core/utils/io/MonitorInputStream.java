// Created: 11.01.2017
package de.freese.pim.core.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.swing.ProgressMonitorInputStream;

/**
 * {@link InputStream} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 * Der Monitor empfängt die Anzahl gelesener Bytes (Parameter 1) und die Gesamtgröße (Parameter 2).<br>
 *
 * @see ProgressMonitorInputStream
 * @author Thomas Freese
 */
public class MonitorInputStream extends InputStream
{
    /**
    *
    */
    private final InputStream delegate;

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
     * Erzeugt eine neue Instanz von {@link MonitorInputStream}
     *
     * @param delegate {@link InputStream}
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param monitor {@link BiConsumer}; Erster Parameter = Anzahl gelesener Bytes, zweiter Parameter = Gesamtgröße
     */
    public MonitorInputStream(final InputStream delegate, final long size, final BiConsumer<Long, Long> monitor)
    {
        super();

        Objects.requireNonNull(delegate, () -> "delegate required");
        Objects.requireNonNull(monitor, () -> "monitor required");

        this.delegate = delegate;
        this.size = size;
        this.monitor = monitor;
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException
    {
        return this.delegate.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.delegate.close();
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(final int readlimit)
    {
        this.delegate.mark(readlimit);
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported()
    {
        return this.delegate.markSupported();
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        int read = this.delegate.read();

        this.sizeRead++;

        this.monitor.accept(this.sizeRead, this.size);

        return read;
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(final byte[] b) throws IOException
    {
        int readCount = this.delegate.read(b);

        if (readCount > 0)
        {
            this.sizeRead += readCount;

            this.monitor.accept(this.sizeRead, this.size);
        }

        return readCount;
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        int readCount = this.delegate.read(b, off, len);

        if (readCount > 0)
        {
            this.sizeRead += readCount;

            this.monitor.accept(this.sizeRead, this.size);
        }

        return readCount;
    }

    /**
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException
    {
        this.delegate.reset();

        this.sizeRead = this.size - this.delegate.available();

        this.monitor.accept(this.sizeRead, this.size);
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(final long n) throws IOException
    {
        long readCount = this.delegate.skip(n);

        if (readCount > 0)
        {
            this.sizeRead += readCount;

            this.monitor.accept(this.sizeRead, this.size);
        }

        return readCount;
    }
}
