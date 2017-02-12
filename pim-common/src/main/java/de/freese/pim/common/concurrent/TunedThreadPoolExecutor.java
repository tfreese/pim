/**
 * Created: 12.02.2017
 */

package de.freese.pim.common.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
 */
public class TunedThreadPoolExecutor extends ThreadPoolExecutor
{
    /**
     * Erstellt ein neues {@link TunedThreadPoolExecutor} Object.
     *
     * @param corePoolSize int
     * @param maximumPoolSize int
     * @param keepAliveTime long
     * @param unit {@link TimeUnit}
     * @param queueCapacity int
     */
    public TunedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final int queueCapacity)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity, new AbortPolicy());
    }

    /**
     * Erstellt ein neues {@link TunedThreadPoolExecutor} Object.
     *
     * @param corePoolSize int
     * @param maximumPoolSize int
     * @param keepAliveTime long
     * @param unit {@link TimeUnit}
     * @param queueCapacity int
     * @param handler {@link RejectedExecutionHandler}
     */
    public TunedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final int queueCapacity,
            final RejectedExecutionHandler handler)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity, Executors.defaultThreadFactory(), handler);
    }

    /**
     * Erstellt ein neues {@link TunedThreadPoolExecutor} Object.
     *
     * @param corePoolSize int
     * @param maximumPoolSize int
     * @param keepAliveTime long
     * @param unit {@link TimeUnit}
     * @param queueCapacity int
     * @param threadFactory {@link ThreadFactory}
     */
    public TunedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final int queueCapacity,
            final ThreadFactory threadFactory)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity, threadFactory, new AbortPolicy());
    }

    /**
     * Erstellt ein neues {@link TunedThreadPoolExecutor} Object.
     *
     * @param corePoolSize int
     * @param maximumPoolSize int
     * @param keepAliveTime long
     * @param unit {@link TimeUnit}
     * @param queueCapacity int
     * @param threadFactory {@link ThreadFactory}
     * @param handler {@link RejectedExecutionHandler}
     */
    public TunedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final int queueCapacity,
            final ThreadFactory threadFactory, final RejectedExecutionHandler handler)
    {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, new TunedLinkedBlockingQueue<Runnable>(queueCapacity), threadFactory, handler);
    }

    /**
     * Erstellt ein neues {@link TunedThreadPoolExecutor} Object.
     *
     * @param corePoolSize int
     * @param maximumPoolSize int
     * @param keepAliveTime long
     * @param unit {@link TimeUnit}
     * @param workQueue {@link TunedLinkedBlockingQueue}
     * @param threadFactory {@link ThreadFactory}
     * @param handler {@link RejectedExecutionHandler}
     */
    private TunedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit,
            final TunedLinkedBlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        workQueue.setCurrentSize(this::getPoolSize);
        workQueue.setMaxSize(this::getMaximumPoolSize);
    }
}
