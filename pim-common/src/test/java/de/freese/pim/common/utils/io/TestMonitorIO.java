// Created: 11.01.2017
package de.freese.pim.common.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Optional;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMonitorIO
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
        private Long current = null;

        /**
         * Erzeugt eine neue Instanz von {@link Monitor}
         */
        public Monitor()
        {
            super();
        }

        /**
         * @return long
         */
        public long getCurrent()
        {
            return Optional.ofNullable(this.current).orElse(0L);
        }

        /**
         * @see de.freese.pim.common.utils.io.IOMonitor#monitor(long, long)
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
    private static final byte[] BYTES = new byte[]
    {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9
    };

    /**
     * Erzeugt eine neue Instanz von {@link TestMonitorIO}
     */
    public TestMonitorIO()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010InputStream() throws Exception
    {
        // BiConsumer<Long, Long> monitor = (size, current) -> System.out.printf("\r%d / %d%n", current, size);
        Monitor monitor = new Monitor();

        // Pro Byte
        InputStream is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, is.available(), monitor))
        {
            for (int i = 0; i < BYTES.length; i++)
            {
                mis.read();

                Assert.assertEquals(i + 1, monitor.getCurrent());
            }
        }

        // Pro Byte[]
        is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, is.available(), monitor))
        {
            mis.read(new byte[2]);
            Assert.assertEquals(2, monitor.getCurrent());

            mis.read(new byte[3]);
            Assert.assertEquals(5, monitor.getCurrent());

            mis.read(new byte[4]);
            Assert.assertEquals(9, monitor.getCurrent());
        }

        // Pro Byte[] Range
        is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, is.available(), monitor))
        {
            mis.read(new byte[10], 2, 8);
            Assert.assertEquals(8, monitor.getCurrent());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020OutputStream() throws Exception
    {
        Monitor monitor = new Monitor();

        // Pro Byte
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), BYTES.length, monitor))
        {
            for (int i = 0; i < BYTES.length; i++)
            {
                mos.write(BYTES[i]);

                Assert.assertEquals(i + 1, monitor.getCurrent());
            }
        }

        // Pro Byte[]
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), BYTES.length, monitor))
        {
            mos.write(new byte[2]);
            Assert.assertEquals(2, monitor.getCurrent());

            mos.write(new byte[3]);
            Assert.assertEquals(5, monitor.getCurrent());

            mos.write(new byte[4]);
            Assert.assertEquals(9, monitor.getCurrent());
        }

        // Pro Byte[] Range
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), BYTES.length, monitor))
        {
            mos.write(BYTES, 2, 8);
            Assert.assertEquals(8, monitor.getCurrent());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030ReadableByteChannel() throws Exception
    {
        Monitor monitor = new Monitor();

        // Pro Byte
        try (MonitoringReadableByteChannel mrc = new MonitoringReadableByteChannel(Channels.newChannel(new ByteArrayInputStream(BYTES)), BYTES.length, monitor))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);

            for (int i = 0; i < BYTES.length; i++)
            {
                mrc.read(buffer);

                Assert.assertEquals(i + 1, monitor.getCurrent());

                buffer.clear();
            }
        }

        // Pro Byte[]
        try (MonitoringReadableByteChannel mrc = new MonitoringReadableByteChannel(Channels.newChannel(new ByteArrayInputStream(BYTES)), BYTES.length, monitor))
        {
            mrc.read(ByteBuffer.allocateDirect(2));
            Assert.assertEquals(2, monitor.getCurrent());

            mrc.read(ByteBuffer.allocateDirect(3));
            Assert.assertEquals(5, monitor.getCurrent());

            mrc.read(ByteBuffer.allocateDirect(4));
            Assert.assertEquals(9, monitor.getCurrent());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040WritableByteChannel() throws Exception
    {
        Monitor monitor = new Monitor();

        // Pro Byte
        try (MonitoringWritableByteChannel mwc = new MonitoringWritableByteChannel(Channels.newChannel(new ByteArrayOutputStream()), BYTES.length, monitor))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);

            for (int i = 0; i < BYTES.length; i++)
            {
                mwc.write(buffer);

                Assert.assertEquals(i + 1, monitor.getCurrent());

                buffer.clear();
            }
        }

        // Pro Byte[]
        try (MonitoringWritableByteChannel mwc = new MonitoringWritableByteChannel(Channels.newChannel(new ByteArrayOutputStream()), BYTES.length, monitor))
        {
            mwc.write(ByteBuffer.wrap(new byte[2]));
            Assert.assertEquals(2, monitor.getCurrent());

            mwc.write(ByteBuffer.wrap(new byte[3]));
            Assert.assertEquals(5, monitor.getCurrent());

            mwc.write(ByteBuffer.wrap(new byte[4]));
            Assert.assertEquals(9, monitor.getCurrent());
        }
    }
}
