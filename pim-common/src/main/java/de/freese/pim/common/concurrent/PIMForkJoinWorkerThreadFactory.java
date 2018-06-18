/**
 * Created: 21.02.2017
 */

package de.freese.pim.common.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dient nur dazu die Threads des {@link ForkJoinPool#commonPool()} umzubenennen.
 * 
 * @author Thomas Freese
 */
public class PIMForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory
{
    /**
     * @author Thomas Freese
     */
    private static class PIMForkJoinWorkerThread extends ForkJoinWorkerThread
    {
        /**
         * Erzeugt eine neue Instanz von {@link PIMForkJoinWorkerThread}
         *
         * @param pool {@link ForkJoinPool}
         */
        public PIMForkJoinWorkerThread(final ForkJoinPool pool)
        {
            super(pool);
        }
    }

    /**
     *
     */
    private final AtomicInteger counter = new AtomicInteger(1);

    /**
     *
     */
    private final String namePrefix;

    /**
     * Erzeugt eine neue Instanz von {@link PIMForkJoinWorkerThreadFactory}
     */
    public PIMForkJoinWorkerThreadFactory()
    {
        super();

        this.namePrefix = "fork-join-";
    }

    /**
     * @see java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory#newThread(java.util.concurrent.ForkJoinPool)
     */
    @Override
    public ForkJoinWorkerThread newThread(final ForkJoinPool pool)
    {
        ForkJoinWorkerThread thread = new PIMForkJoinWorkerThread(pool);

        String threadName = String.format("%s%02d", this.namePrefix, this.counter.getAndIncrement());
        thread.setName(threadName);

        return thread;
    }
}