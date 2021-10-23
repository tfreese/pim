// Created: 11.01.2017
package de.freese.pim.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.pim.core.utils.io.IOMonitor;
import de.freese.pim.core.utils.io.MonitorInputStream;
import de.freese.pim.core.utils.io.MonitorOutputStream;
import de.freese.pim.core.utils.io.MonitoringReadableByteChannel;
import de.freese.pim.core.utils.io.MonitoringWritableByteChannel;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestMonitorIO
{
    /**
     * BiConsumer<Long, Long> monitor = (size, current) -> System.out.printf("\r%d / %d%n", current, size);
     *
     * @author Thomas Freese
     */
    private static class Monitor implements IOMonitor
    {
        /**
         *
         */
        private Long current;

        /**
         * @return long
         */
        public long getCurrent()
        {
            return Optional.ofNullable(this.current).orElse(0L);
        }

        /**
         * @see de.freese.pim.core.utils.io.IOMonitor#monitor(long, long)
         */
        @Override
        public void monitor(final long current, final long size)
        {
            this.current = current;
        }
    }

    /**
     *
     */
    private static final byte[] BYTES =
    {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9
    };

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test010InputStream() throws Exception
    {
        // BiConsumer<Long, Long> monitor = (size, current) -> System.out.printf("\r%d / %d%n", current, size);
        Monitor monitor = new Monitor();

        // Pro Byte
        InputStream is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, monitor, is.available()))
        {
            for (int i = 0; i < BYTES.length; i++)
            {
                mis.read();

                assertEquals(i + 1, monitor.getCurrent());
            }
        }

        // Pro Byte[]
        is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, monitor, is.available()))
        {
            mis.read(new byte[2]);
            assertEquals(2, monitor.getCurrent());

            mis.read(new byte[3]);
            assertEquals(5, monitor.getCurrent());

            mis.read(new byte[4]);
            assertEquals(9, monitor.getCurrent());
        }

        // Pro Byte[] Range
        is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, monitor, is.available()))
        {
            mis.read(new byte[10], 2, 8);
            assertEquals(8, monitor.getCurrent());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test020OutputStream() throws Exception
    {
        Monitor monitor = new Monitor();

        // Pro Byte
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), monitor, BYTES.length))
        {
            for (int i = 0; i < BYTES.length; i++)
            {
                mos.write(BYTES[i]);

                assertEquals(i + 1, monitor.getCurrent());
            }
        }

        // Pro Byte[]
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), monitor, BYTES.length))
        {
            mos.write(new byte[2]);
            assertEquals(2, monitor.getCurrent());

            mos.write(new byte[3]);
            assertEquals(5, monitor.getCurrent());

            mos.write(new byte[4]);
            assertEquals(9, monitor.getCurrent());
        }

        // Pro Byte[] Range
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), monitor, BYTES.length))
        {
            mos.write(BYTES, 2, 8);
            assertEquals(8, monitor.getCurrent());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test030ReadableByteChannel() throws Exception
    {
        Monitor monitor = new Monitor();

        // Pro Byte
        try (MonitoringReadableByteChannel mrc = new MonitoringReadableByteChannel(Channels.newChannel(new ByteArrayInputStream(BYTES)), monitor, BYTES.length))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);

            for (int i = 0; i < BYTES.length; i++)
            {
                mrc.read(buffer);

                assertEquals(i + 1, monitor.getCurrent());

                buffer.clear();
            }
        }

        // Pro Byte[]
        try (MonitoringReadableByteChannel mrc = new MonitoringReadableByteChannel(Channels.newChannel(new ByteArrayInputStream(BYTES)), monitor, BYTES.length))
        {
            mrc.read(ByteBuffer.allocateDirect(2));
            assertEquals(2, monitor.getCurrent());

            mrc.read(ByteBuffer.allocateDirect(3));
            assertEquals(5, monitor.getCurrent());

            mrc.read(ByteBuffer.allocateDirect(4));
            assertEquals(9, monitor.getCurrent());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test040WritableByteChannel() throws Exception
    {
        Monitor monitor = new Monitor();

        // Pro Byte
        try (MonitoringWritableByteChannel mwc = new MonitoringWritableByteChannel(Channels.newChannel(new ByteArrayOutputStream()), monitor, BYTES.length))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);

            for (int i = 0; i < BYTES.length; i++)
            {
                mwc.write(buffer);

                assertEquals(i + 1, monitor.getCurrent());

                buffer.clear();
            }
        }

        // Pro Byte[]
        try (MonitoringWritableByteChannel mwc = new MonitoringWritableByteChannel(Channels.newChannel(new ByteArrayOutputStream()), monitor, BYTES.length))
        {
            mwc.write(ByteBuffer.wrap(new byte[2]));
            assertEquals(2, monitor.getCurrent());

            mwc.write(ByteBuffer.wrap(new byte[3]));
            assertEquals(5, monitor.getCurrent());

            mwc.write(ByteBuffer.wrap(new byte[4]));
            assertEquals(9, monitor.getCurrent());
        }
    }
}
