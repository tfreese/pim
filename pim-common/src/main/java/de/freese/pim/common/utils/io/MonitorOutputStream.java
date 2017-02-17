// Created: 11.01.2017
package de.freese.pim.common.utils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * {@link OutputStream} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitorOutputStream extends OutputStream
{
    /**
    *
    */
    private final OutputStream delegate;

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
    private long sizeWritten = 0;

    /**
     * Erzeugt eine neue Instanz von {@link MonitorOutputStream}
     *
     * @param delegate {@link OutputStream}
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param monitor {@link IOMonitor}
     */
    public MonitorOutputStream(final OutputStream delegate, final long size, final IOMonitor monitor)
    {
        super();

        Objects.requireNonNull(delegate, () -> "delegate required");
        Objects.requireNonNull(monitor, () -> "monitor required");

        this.delegate = delegate;
        this.size = size;
        this.monitor = monitor;
    }

    /**
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.delegate.close();
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException
    {
        this.delegate.flush();
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException
    {
        this.delegate.write(b);

        this.sizeWritten += b.length;

        this.monitor.monitor(this.sizeWritten, this.size);
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        this.delegate.write(b, off, len);

        this.sizeWritten += len;

        this.monitor.monitor(this.sizeWritten, this.size);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        this.delegate.write(b);

        this.sizeWritten++;

        this.monitor.monitor(this.sizeWritten, this.size);
    }
}
