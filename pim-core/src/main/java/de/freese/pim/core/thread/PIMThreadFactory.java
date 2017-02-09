package de.freese.pim.core.thread;

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
        super();

        this.namePrefix = namePrefix + "-";

        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread thread = new Thread(this.threadGroup, r, this.namePrefix + this.threadNumber.getAndIncrement());
        thread.setDaemon(true);

        return thread;
    }
}
