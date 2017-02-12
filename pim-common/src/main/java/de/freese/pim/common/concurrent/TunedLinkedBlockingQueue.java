/**
 * Created: 12.02.2017
 */

package de.freese.pim.common.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * @see TunedThreadPoolExecutor
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class TunedLinkedBlockingQueue<T> extends LinkedBlockingQueue<T>
{
    /**
     *
     */
    private static final long serialVersionUID = 6374300294609033461L;

    /**
     *
     */
    private IntSupplier currentSize = null;

    /**
     *
     */
    private IntSupplier maxSize = null;

    /**
     * Erstellt ein neues {@link TunedLinkedBlockingQueue} Object.
     *
     * @param capacity int
     */
    public TunedLinkedBlockingQueue(final int capacity)
    {
        super(capacity);
    }

    /**
     * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object)
     */
    @Override
    public boolean offer(final T e)
    {
        if (this.currentSize.getAsInt() < this.maxSize.getAsInt())
        {
            // FALSE triggert den ThreadPoolExecutor neue Threads zu erzeugen.
            return false;
        }

        return super.offer(e);
    }

    /**
     * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean offer(final T e, final long timeout, final TimeUnit unit) throws InterruptedException
    {
        if (this.currentSize.getAsInt() < this.maxSize.getAsInt())
        {
            // FALSE triggert den ThreadPoolExecutor neue Threads zu erzeugen.
            return false;
        }

        return super.offer(e, timeout, unit);
    }

    /**
     * @param currentSize {@link IntSupplier}
     */
    public void setCurrentSize(final IntSupplier currentSize)
    {
        this.currentSize = currentSize;
    }

    /**
     * @param maxSize {@link IntSupplier}
     */
    public void setMaxSize(final IntSupplier maxSize)
    {
        this.maxSize = maxSize;
    }
}