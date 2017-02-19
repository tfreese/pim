/**
 * Created: 18.02.2017
 */

package de.freese.pim.common.concurrent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Das Default-Verhalten eines {@link ThreadPoolExecutor} mit einer Bounded-Queue ist, dass erst neue Threads erzeugt werden, wenn die corePoolSize erreicht und
 * die Queue voll ist.<br>
 *
 * @author Thomas Freese
 * @see "http://tutorials.jenkov.com/java-concurrency/thread-pools.html"
 */
public class SimpleExecutorService extends AbstractExecutorService
{
    /**
     * @author Thomas Freese
     */
    private final class WorkerThread extends Thread
    {
        /**
         *
         */
        boolean coreThread = false;

        /**
         *
         */
        private boolean isStopped = false;

        /**
         * Erstellt ein neues {@link WorkerThread} Object.
         *
         * @param threadGroup {@link ThreadGroup}
         * @param name String
         */
        public WorkerThread(final ThreadGroup threadGroup, final String name)
        {
            super(threadGroup, name);
        }

        /**
         *
         */
        public void doStop()
        {
            this.isStopped = true;

            interrupt();
        }

        /**
         * @return boolean
         */
        public boolean isStopped()
        {
            return this.isStopped;
        }

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            while (!isStopped())
            {
                runWorker(this);
            }

            // System.out.println("SimpleExecutorService.WorkerThread.run(): stopped " + getName());
            SimpleExecutorService.this.threadNumber.decrementAndGet();
            SimpleExecutorService.this.workersIdle.remove(this);
        }
    }

    /**
     *
     */
    private final int coreSize;

    /**
     * [ms]
     */
    private final long keepAliveTime;

    /**
     *
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     *
     */
    private final int maxSize;

    /**
    *
    */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     *
     */
    private TreeSet<Thread> workersIdle = new TreeSet<>(Comparator.comparing(Thread::getName));

    /**
     *
     */
    private TreeSet<Thread> workersWorking = new TreeSet<>(Comparator.comparing(Thread::getName));

    /**
     *
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * Erstellt ein neues {@link SimpleExecutorService} Object.<br>
     * <br>
     * Defaults:<br>
     * coreSize = {@link Runtime#availableProcessors()}<br>
     * maxSize = coreSize * 2<br>
     * queueSize = maxSize * 10<br>
     * <br>
     * Beispiel:<br>
     * coreSize = 4<br>
     * maxSize = 8<br>
     * queueSize = 80<br>
     */
    public SimpleExecutorService()
    {
        this(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 2 * 10,
                60, TimeUnit.SECONDS);
    }

    /**
     * Erstellt ein neues {@link SimpleExecutorService} Object.
     *
     * @param coreSize int
     * @param maxSize int
     * @param queueSize int
     * @param keepAliveTime int
     * @param timeUnit {@link TimeUnit}
     */
    public SimpleExecutorService(final int coreSize, final int maxSize, final int queueSize, final int keepAliveTime, final TimeUnit timeUnit)
    {
        super();

        if (coreSize <= 0)
        {
            throw new IllegalArgumentException("coreSize must be > 0");
        }

        if (maxSize <= 0)
        {
            throw new IllegalArgumentException("maxSize must be > 0");
        }

        if (maxSize < coreSize)
        {
            throw new IllegalArgumentException("maxSize must be > coreSize");
        }

        if (queueSize <= 0)
        {
            throw new IllegalArgumentException("queueSize must be > 0");
        }

        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.workQueue = new LinkedBlockingQueue<>(queueSize);
        this.keepAliveTime = timeUnit.toMillis(keepAliveTime);

        // CoreWorker starten
        for (int i = 0; i < coreSize; i++)
        {
            WorkerThread wt = createWorker();
            wt.coreThread = true;
            wt.start();
            this.workersIdle.add(wt);
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Erzeugt einen neuen {@link WorkerThread}.
     *
     * @return {@link WorkerThread}
     */
    protected WorkerThread createWorker()
    {
        // TODO Über ThreadFactory machen !
        SecurityManager sm = System.getSecurityManager();
        ThreadGroup threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();

        WorkerThread thread = new WorkerThread(threadGroup, "thread-" + this.threadNumber.getAndIncrement());

        if (thread.isDaemon())
        {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != Thread.NORM_PRIORITY)
        {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        return thread;
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    @Override
    public void execute(final Runnable command)
    {
        if (command == null)
        {
            throw new NullPointerException();
        }

        this.mainLock.lock();

        try
        {
            boolean added = getWorkQueue().offer(command);

            if (!added)
            {
                // Queue voll.
                reject(command);

                return;
            }

            // Gibs Idle-Workers ?
            // System.out.println("SimpleExecutorService.execute(): idlesEmpty=" + this.workersIdle.isEmpty());
            if ((getSize() < getMaxSize()) && this.workersIdle.isEmpty())
            {
                WorkerThread wt = createWorker();
                wt.coreThread = false;
                this.workersIdle.add(wt);
                wt.start();
            }
        }
        finally
        {
            this.mainLock.unlock();
        }
    }

    /**
     * Liefert die minimale Größe thes ThreadPools.
     *
     * @return int
     */
    public int getCoreSize()
    {
        return this.coreSize;
    }

    /**
     * Liefert die maximale Größe thes ThreadPools.
     *
     * @return int
     */
    public int getMaxSize()
    {
        return this.maxSize;
    }

    /**
     * Liefert die aktuelle Größe des ThreadPools.
     *
     * @return int
     */
    public int getSize()
    {
        this.mainLock.lock();

        try
        {
            return this.workersIdle.size() + this.workersWorking.size();
        }
        finally
        {
            this.mainLock.unlock();
        }
    }

    /**
     * Liefert die Anzahl der Idle-Threads.
     *
     * @return int
     */
    public int getSizeThreadsIdle()
    {
        return this.workersIdle.size();
    }

    /**
     * Liefert die Anzahl der Working-Threads.
     *
     * @return int
     */
    public int getSizeThreadsWorking()
    {
        return this.workersWorking.size();
    }

    /**
     * Liefert den nächsten Task aus der Qeue.
     *
     * @param forCoreTask boolean
     * @return {@link Runnable}
     */
    protected Runnable getTask(final boolean forCoreTask)
    {
        Runnable task = null;

        try
        {
            if (forCoreTask)
            {
                task = getWorkQueue().take();
            }
            else
            {
                task = getWorkQueue().poll(this.keepAliveTime, TimeUnit.MILLISECONDS);
            }
        }
        catch (InterruptedException iex)
        {
            // Ignore
        }

        return task;
    }

    /**
     * @return {@link BlockingQueue}<Runnable>
     */
    protected BlockingQueue<Runnable> getWorkQueue()
    {
        return this.workQueue;
    }

    /**
     * @see java.util.concurrent.ExecutorService#isShutdown()
     */
    @Override
    public boolean isShutdown()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see java.util.concurrent.ExecutorService#isTerminated()
     */
    @Override
    public boolean isTerminated()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Markiert den Thread.
     *
     * @param working boolean
     */
    protected void markThreadAsWorking(final boolean working)
    {
        this.mainLock.lock();

        try
        {
            Thread thread = Thread.currentThread();
            // System.out.println("SimpleExecutorService.markThreadAsWorking(): " + working + ", " + thread.getName());

            if (working)
            {
                this.workersIdle.remove(thread);
                this.workersWorking.add(thread);
            }
            else
            {
                this.workersWorking.remove(thread);
                this.workersIdle.add(thread);
            }
        }
        finally
        {
            this.mainLock.unlock();
        }
    }

    /**
     * @param task {@link Runnable}
     */
    protected void reject(final Runnable task)
    {
        // Caller runs
        task.run();
    }

    /**
     * @param workerThread {@link WorkerThread}
     */
    protected void runWorker(final WorkerThread workerThread)
    {
        Runnable task = null;

        while ((task = getTask(workerThread.coreThread)) != null)
        {
            markThreadAsWorking(true);

            try
            {
                task.run();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                markThreadAsWorking(false);
            }
        }

        if (!workerThread.coreThread)
        {
            workerThread.doStop();
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#shutdown()
     */
    @Override
    public void shutdown()
    {
        shutdownNow();
    }

    /**
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    @Override
    public List<Runnable> shutdownNow()
    {
        BlockingQueue<Runnable> q = getWorkQueue();
        ArrayList<Runnable> taskList = new ArrayList<>();

        q.drainTo(taskList);

        if (!q.isEmpty())
        {
            for (Runnable r : q.toArray(new Runnable[0]))
            {
                if (q.remove(r))
                {
                    taskList.add(r);
                }
            }
        }

        this.workersIdle.stream().map(t -> (WorkerThread) t).forEach(WorkerThread::doStop);
        this.workersWorking.stream().map(t -> (WorkerThread) t).forEach(WorkerThread::doStop);

        return taskList;
    }
}
