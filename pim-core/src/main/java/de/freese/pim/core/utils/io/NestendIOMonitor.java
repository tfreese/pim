// Created: 16.02.2017
package de.freese.pim.core.utils.io;

import java.util.Objects;

/**
 * IO-Monitor für verschiedene Streams, der die gesamt gelesene/geschiebene Anzahl von Bytes aufsummiert und an den Delegate weiterleitet.<br>
 *
 * @author Thomas Freese
 */
public class NestendIOMonitor implements IOMonitor
{
    /**
    *
    */
    private long currentyAccepted;
    /**
     *
     */
    private final IOMonitor delegate;
    /**
     *
     */
    private final long size;

    /**
     * Erstellt ein neues {@link NestendIOMonitor} Object.
     *
     * @param delegate {@link IOMonitor}
     * @param size long
     */
    public NestendIOMonitor(final IOMonitor delegate, final long size)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.size = size;
    }

    /**
     * @see de.freese.pim.core.utils.io.IOMonitor#monitor(long, long)
     */
    @Override
    public void monitor(final long current, final long size)
    {
        this.currentyAccepted += current;
        // System.out.printf("%d / %d%n", current, this.size);

        this.delegate.monitor(this.currentyAccepted, this.size);
    }
}
