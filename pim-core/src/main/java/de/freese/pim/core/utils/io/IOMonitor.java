// Created: 16.02.2017
package de.freese.pim.core.utils.io;

import java.util.function.BiConsumer;

/**
 * Interface für die Möglichkeit zur Streams/Channels zu monitoren.<br>
 * Der Monitor empfängt die Anzahl geschriebener/gelesener Bytes (Parameter 1) und die Gesamtgröße (Parameter 2).<br>
 * Er implementiert ebenfalls BiConsumer(Long, Long).
 *
 * @author Thomas Freese
 *
 * @see MonitoringReadableByteChannel
 * @see MonitoringWritableByteChannel
 * @see MonitorInputStream
 * @see MonitorOutputStream
 */
public interface IOMonitor extends BiConsumer<Long, Long>
{
    /**
     * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
     */
    @Override
    default void accept(final Long t, final Long u)
    {
        monitor(t, u);
    }

    /**
     * Monitor benachrichtigen.
     *
     * @param current long; Anzahl geschriebener/gelesener Bytes
     * @param size long; Gesamtgröße
     */
    void monitor(long current, long size);
}
