/**
 * Created: 12.02.2017
 */

package de.freese.pim.common.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * Das Default-Verhalten eines {@link ThreadPoolExecutor} mit einer Bounded-Queue ist, dass erst neue Threads erzeugt werden, wenn die corePoolSize erreicht und
 * die Queue voll ist.<br>
 * Bei folgender Konfiguration
 *
 * <pre>
 * corePoolSize = 3
 * maximumPoolSize = 10
 * queueCapacity = 20
 * </pre>
 *
 * würden erst die Threads 4 - 10 erzeugt werden, wenn in der Queue 20 Tasks liegen.<br>
 * Somit läuft der ThreadPool immer nur mit 3 Threads und nicht mit max. 10 wie erwartet, wenn z.B. 11 Tasks bearbeitet werden müssen.<br>
 * <br>
 * Die Lösung ist, die Methode {@link LinkedBlockingQueue#offer(Object)} so zu implementieren, dass FALSE gelifert wird, wenn die maximumPoolSize noch nicht
 * erreicht ist.<br>
 * Dies zwingt den {@link ThreadPoolExecutor} dazu neue Threads zu erzeugen, auch wenn die Queue nocht nicht voll ist.<br>
 * <br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see "org.apache.tomcat.util.threads.TaskQueue"
 * @see "http://tutorials.jenkov.com/java-concurrency/thread-pools.html"
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
    private IntSupplier currentSize;

    /**
     *
     */
    private IntSupplier maxSize;

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
        if ((this.currentSize == null) || (this.maxSize == null))
        {
            return super.offer(e);
        }

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
        if ((this.currentSize == null) || (this.maxSize == null))
        {
            return super.offer(e, timeout, unit);
        }

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
