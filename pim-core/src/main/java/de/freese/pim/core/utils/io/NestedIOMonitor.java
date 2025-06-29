// Created: 16.02.2017
package de.freese.pim.core.utils.io;

import java.util.Objects;

/**
 * IO-Monitor für verschiedene Streams, der die gesamt gelesene/geschriebene Anzahl von Bytes aufsummiert und an den Delegate weiterleitet.<br>
 *
 * @author Thomas Freese
 */
public class NestedIOMonitor implements IOMonitor {
    private final IOMonitor delegate;
    private final long size;
    private long currentAccepted;

    public NestedIOMonitor(final IOMonitor delegate, final long size) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.size = size;
    }

    @Override
    public void monitor(final long current, final long size) {
        currentAccepted += current;
        // System.out.printf("%d / %d%n", current, size);

        delegate.monitor(currentAccepted, size);
    }
}
