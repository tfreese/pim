package de.freese.pim.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class PIMThreadFactory implements ThreadFactory
{
    /**
     *
     */
    private final String namePrefix;

    /**
     * Thread.NORM_PRIORITY
     */
    private final int priority;

    /**
     *
     */
    private final ThreadGroup threadGroup;

    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Erzeugt eine neue Instanz von {@link PIMThreadFactory}
     *
     * @param namePrefix String
     */
    public PIMThreadFactory(final String namePrefix)
    {
        this(namePrefix, Thread.NORM_PRIORITY);
    }

    /**
     * Erzeugt eine neue Instanz von {@link PIMThreadFactory}
     *
     * @param namePrefix String
     * @param priority int
     */
    public PIMThreadFactory(final String namePrefix, final int priority)
    {
        super();

        this.namePrefix = namePrefix;
        this.priority = priority;

        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable task)
    {
        String threadName = String.format("%s%02d", this.namePrefix, this.threadNumber.getAndIncrement());

        Thread thread = new Thread(this.threadGroup, task, threadName);

        if (thread.isDaemon())
        {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != this.priority)
        {
            thread.setPriority(this.priority);
        }

        return thread;
    }
}
