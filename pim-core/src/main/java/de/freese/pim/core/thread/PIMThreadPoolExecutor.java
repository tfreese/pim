package de.freese.pim.core.thread;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Threads leben max. 60 Sekunden, wenn es nix zu tun gibt.
 *
 * @author Thomas Freese
 */
public class PIMThreadPoolExecutor extends ThreadPoolExecutor
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PIMThreadPoolExecutor.class);

    /**
     * Erzeugt eine neue Instanz von {@link PIMThreadPoolExecutor}
     *
     * @param coreSize int
     * @param maxSize int
     */
    public PIMThreadPoolExecutor(final int coreSize, final int maxSize)
    {
        super(coreSize, maxSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new PIMThreadFactory("PIM"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * @see java.util.concurrent.ThreadPoolExecutor#shutdown()
     */
    @Override
    public void shutdown()
    {
        LOGGER.info("Close ExecutorService");

        super.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate
            if (!awaitTermination(10, TimeUnit.SECONDS))
            {
                shutdownNow();

                // Cancel currently executing tasks
                // Wait a while for tasks to respond to being
                // cancelled
                if (!awaitTermination(10, TimeUnit.SECONDS))
                {
                    LOGGER.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException ie)
        {
            // (Re-)Cancel if current thread also interrupted
            shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
