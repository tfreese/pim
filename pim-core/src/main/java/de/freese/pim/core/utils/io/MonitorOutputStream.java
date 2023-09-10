// Created: 11.01.2017
package de.freese.pim.core.utils.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * {@link OutputStream} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitorOutputStream extends FilterOutputStream {

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
    public MonitorOutputStream(final OutputStream delegate, final IOMonitor monitor, final long size) {
        super(delegate);

        this.monitor = Objects.requireNonNull(monitor, "monitor required");
        this.size = size;
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        super.write(b, off, len);

        this.monitor.monitor(this.sizeWritten, this.size);
    }

    @Override
    public void write(final int b) throws IOException {
        super.write(b);

        this.sizeWritten++;

        this.monitor.monitor(this.sizeWritten, this.size);
    }
}
