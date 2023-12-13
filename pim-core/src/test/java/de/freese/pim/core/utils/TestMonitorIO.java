// Created: 11.01.2017
package de.freese.pim.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

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
class TestMonitorIO {
    private static final byte[] BYTES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    /**
     * <pre>{@code
     * BiConsumer<Long, Long> monitor = (size, current) -> System.out.printf("\r%d / %d%n", current, size);
     * }</pre>
     *
     * @author Thomas Freese
     */
    private static final class Monitor implements IOMonitor {
        private long current;

        public long getCurrent() {
            return this.current;
        }

        @Override
        public void monitor(final long current, final long size) {
            this.current = current;
        }
    }

    @Test
    void test010InputStream() throws Exception {
        // BiConsumer<Long, Long> monitor = (size, current) -> System.out.printf("\r%d / %d%n", current, size);

        // Pro Byte
        Monitor monitor = new Monitor();
        InputStream is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, monitor, is.available())) {
            for (int i = 0; i < BYTES.length; i++) {
                mis.read();

                assertEquals(i + 1, monitor.getCurrent());
            }
        }

        // Pro Byte[]
        monitor = new Monitor();
        is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, monitor, is.available())) {
            mis.read(new byte[2]);
            assertEquals(2, monitor.getCurrent());

            mis.read(new byte[3]);
            assertEquals(5, monitor.getCurrent());

            mis.read(new byte[4]);
            assertEquals(9, monitor.getCurrent());
        }

        // Pro Byte[] Range
        monitor = new Monitor();
        is = new ByteArrayInputStream(BYTES);

        try (MonitorInputStream mis = new MonitorInputStream(is, monitor, is.available())) {
            mis.read(new byte[10], 2, 8);
            assertEquals(8, monitor.getCurrent());
        }
    }

    @Test
    void test020OutputStream() throws Exception {

        // Pro Byte
        Monitor monitor = new Monitor();
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), monitor, BYTES.length)) {
            for (int i = 0; i < BYTES.length; i++) {
                mos.write(BYTES[i]);

                assertEquals(i + 1, monitor.getCurrent());
            }
        }

        // Pro Byte[]
        monitor = new Monitor();
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), monitor, BYTES.length)) {
            mos.write(new byte[2]);
            assertEquals(2, monitor.getCurrent());

            mos.write(new byte[3]);
            assertEquals(5, monitor.getCurrent());

            mos.write(new byte[4]);
            assertEquals(9, monitor.getCurrent());
        }

        // Pro Byte[] Range
        try (MonitorOutputStream mos = new MonitorOutputStream(new ByteArrayOutputStream(), monitor, BYTES.length)) {
            mos.write(BYTES, 2, 8);
            assertEquals(8, monitor.getCurrent());
        }
    }

    @Test
    void test030ReadableByteChannel() throws Exception {

        // Pro Byte
        Monitor monitor = new Monitor();
        try (MonitoringReadableByteChannel mrc = new MonitoringReadableByteChannel(Channels.newChannel(new ByteArrayInputStream(BYTES)), monitor, BYTES.length)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);

            for (int i = 0; i < BYTES.length; i++) {
                mrc.read(buffer);

                assertEquals(i + 1, monitor.getCurrent());

                buffer.clear();
            }
        }

        // Pro Byte[]
        monitor = new Monitor();
        try (MonitoringReadableByteChannel mrc = new MonitoringReadableByteChannel(Channels.newChannel(new ByteArrayInputStream(BYTES)), monitor, BYTES.length)) {
            mrc.read(ByteBuffer.allocateDirect(2));
            assertEquals(2, monitor.getCurrent());

            mrc.read(ByteBuffer.allocateDirect(3));
            assertEquals(5, monitor.getCurrent());

            mrc.read(ByteBuffer.allocateDirect(4));
            assertEquals(9, monitor.getCurrent());
        }
    }

    @Test
    void test040WritableByteChannel() throws Exception {

        // Pro Byte
        Monitor monitor = new Monitor();
        try (MonitoringWritableByteChannel mwc = new MonitoringWritableByteChannel(Channels.newChannel(new ByteArrayOutputStream()), monitor, BYTES.length)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);

            for (int i = 0; i < BYTES.length; i++) {
                mwc.write(buffer);

                assertEquals(i + 1, monitor.getCurrent());

                buffer.clear();
            }
        }

        // Pro Byte[]
        monitor = new Monitor();
        try (MonitoringWritableByteChannel mwc = new MonitoringWritableByteChannel(Channels.newChannel(new ByteArrayOutputStream()), monitor, BYTES.length)) {
            mwc.write(ByteBuffer.wrap(new byte[2]));
            assertEquals(2, monitor.getCurrent());

            mwc.write(ByteBuffer.wrap(new byte[3]));
            assertEquals(5, monitor.getCurrent());

            mwc.write(ByteBuffer.wrap(new byte[4]));
            assertEquals(9, monitor.getCurrent());
        }
    }
}
