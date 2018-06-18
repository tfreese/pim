/**
 * Created: 18.02.2017
 */

package de.freese.pim.common.concurrent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Das Default-Verhalten eines {@link ThreadPoolExecutor} mit einer Bounded-Queue ist, dass erst neue Threads erzeugt werden, wenn die
 * corePoolSize erreicht und die Queue voll ist.<br>
 * <br>
 * Dieser ThreadPool hat ein etwas anderes Verhalten:<br>
 * <ul>
 * <li>Neue Threads werden erzeugt, wenn<br>
 *
 * <pre>
 * - maxSize noch nicht erreicht
 * - und keine freien Threads zur Verfügung stehen
 * - und Tasks in der Queue sind
 * </pre>
 *
 * </li>
 * <li>Thread-Nummern werden intern ermittelt und gesetzt</li></li>
 * </ul>
 *
 * @author Thomas Freese
 * @see "http://tutorials.jenkov.com/java-concurrency/thread-pools.html"
 */
public class SimpleExecutorService extends AbstractExecutorService
{
    /**
     * @author Thomas Freese
     */
    private final class Worker implements Runnable
    {
        /**
         *
         */
        private Runnable firstTask = null;

        /**
         *
         */
        private boolean isCoreWorker = false;

        /**
         *
         */
        private Thread thread = null;

        /**
         * Erstellt ein neues {@link Worker} Object.
         */
        public Worker()
        {
            super();
        }

        /**
         * @return String
         */
        public String getName()
        {
            return this.thread.getName();
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            // Thread thread = Thread.currentThread();

            while (!this.thread.isInterrupted())
            {
                runWorker(this);
            }

            removeWorker(this);
        }
    }

    /**
     *
     */
    private static final boolean DEBUG = false;

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
     * 0 = running<br>
     * 1 = isShutdown<br>
     * 2 = isTerminated<br>
     */
    private AtomicInteger state = new AtomicInteger(0);

    /**
     *
     */
    private final ThreadFactory threadFactory;

    /**
    *
    */
    private final AtomicInteger threadNumber = new AtomicInteger(0);

    /**
    *
    */
    private Set<Worker> workers = new TreeSet<>(Comparator.comparing(Worker::getName));

    /**
    *
    */
    private final AtomicInteger workersIdle = new AtomicInteger(0);

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
     * keepAliveTime = 60<br>
     * timeUnit = TimeUnit.SECONDS<br>
     * <br>
     * Beispiel:<br>
     * coreSize = 4<br>
     * maxSize = 8<br>
     * queueSize = 80<br>
     */
    public SimpleExecutorService()
    {
        this(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2,
                Runtime.getRuntime().availableProcessors() * 2 * 10, 60, TimeUnit.SECONDS);
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
    public SimpleExecutorService(final int coreSize, final int maxSize, final int queueSize, final int keepAliveTime,
            final TimeUnit timeUnit)
    {
        this(coreSize, maxSize, queueSize, keepAliveTime, timeUnit, "thread-");
    }

    /**
     * Erstellt ein neues {@link SimpleExecutorService} Object.
     *
     * @param coreSize int
     * @param maxSize int
     * @param queueSize int
     * @param keepAliveTime int
     * @param timeUnit {@link TimeUnit}
     * @param threadNamePrefix String
     */
    public SimpleExecutorService(final int coreSize, final int maxSize, final int queueSize, final int keepAliveTime,
            final TimeUnit timeUnit, final String threadNamePrefix)
    {
        this(coreSize, maxSize, queueSize, keepAliveTime, timeUnit, threadNamePrefix, Thread.NORM_PRIORITY);
    }

    /**
     * Erstellt ein neues {@link SimpleExecutorService} Object.
     *
     * @param coreSize int
     * @param maxSize int
     * @param queueSize int
     * @param keepAliveTime int
     * @param timeUnit {@link TimeUnit}
     * @param threadNamePrefix String
     * @param threadPriority int
     */
    public SimpleExecutorService(final int coreSize, final int maxSize, final int queueSize, final int keepAliveTime,
            final TimeUnit timeUnit, final String threadNamePrefix, final int threadPriority)
    {
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

        if (keepAliveTime <= 0)
        {
            throw new IllegalArgumentException("keepAliveTime must be > 0");
        }

        if ((threadPriority < Thread.MIN_PRIORITY) || (threadPriority > Thread.MAX_PRIORITY))
        {
            throw new IllegalArgumentException("Thread.MIN_PRIORITY > threadPriority > Thread.MAX_PRIORITY");
        }

        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.workQueue = new LinkedBlockingQueue<>(queueSize);
        this.keepAliveTime = timeUnit.toMillis(keepAliveTime);

        this.threadFactory = task ->
        {
            SecurityManager sm = System.getSecurityManager();
            ThreadGroup threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();

            String threadName = String.format("%s%02d", threadNamePrefix, this.threadNumber.incrementAndGet());

            Thread thread = new Thread(threadGroup, task, threadName);
            // Thread thread = new WorkerThread(threadGroup, threadName);

            if (thread.isDaemon())
            {
                thread.setDaemon(false);
            }

            if (thread.getPriority() != threadPriority)
            {
                thread.setPriority(threadPriority);
            }

            return thread;
        };

        // CoreWorker starten
        startCoreThreads();
    }

    /**
     * Liefert immer true.
     *
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException
    {
        // long nanos = unit.toNanos(timeout);
        // Condition termination = this.mainLock.newCondition();
        //
        // this.mainLock.lock();
        //
        // try
        // {
        // nanos = termination.awaitNanos(nanos);
        // }
        // finally
        //
        // {
        // this.mainLock.unlock();
        // }

        return true;
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

        if (isShutdown() || isTerminated())
        {
            throw new IllegalStateException("threadpool in shutdown or terminated");
        }

        this.mainLock.lock();

        try
        {
            int poolSize = getPoolSize();
            int idleWorkers = getIdleSize();
            int queueSize = getWorkQueue().size();

            if (DEBUG)
            {
                System.out.printf("SimpleExecutorService.execute(): poolSize=%d; workersIdle=%d; queueSize=%d, task=%s%n", poolSize,
                        idleWorkers, queueSize, command);
            }

            if (poolSize < getCoreSize())
            {
                if (DEBUG)
                {
                    System.out.printf("SimpleExecutorService.execute(): add core worker%n");
                }
                // Core-Worker erzeugen
                addWorker(command, true);

                return;
            }

            if ((poolSize < getMaxSize()) && (idleWorkers == 0))// && (queueSize > 0))// && !getWorkQueue().isEmpty())
            {
                if (DEBUG)
                {
                    System.out.printf("SimpleExecutorService.execute(): add worker%n");
                }
                // Neue Threads starten, wenn
                // - maxSize noch nicht erreicht
                // - und keine freien Threads zur Verfügung stehen
                // - und Tasks in der Queue sind
                addWorker(command, false);

                return;
            }

            if (DEBUG)
            {
                System.out.printf("SimpleExecutorService.execute(): offer task=%s%n", command);
            }

            boolean isInQueue = getWorkQueue().offer(command);

            if (!isInQueue)
            {
                // Queue voll.
                reject(command);

                return;
            }
        }
        finally
        {
            this.mainLock.unlock();
        }
    }

    /**
     * Liefert die minimale Größe des ThreadPools.
     *
     * @return int
     */
    public int getCoreSize()
    {
        return this.coreSize;
    }

    /**
     * Liefert die Anzahl der wartenden Threads.
     *
     * @return int
     */
    public int getIdleSize()
    {
        return this.workersIdle.get();
    }

    /**
     * Liefert die maximale Größe des ThreadPools.
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
    public int getPoolSize()
    {
        return this.threadNumber.get();
    }

    /**
     * Liefert die Anzahl der arbeitenden Threads.
     *
     * @return int
     */
    public int getWorkingSize()
    {
        return this.threadNumber.get() - this.workersIdle.get();
    }

    /**
     * @see java.util.concurrent.ExecutorService#isShutdown()
     */
    @Override
    public boolean isShutdown()
    {
        return this.state.get() == 1;
    }

    /**
     * @see java.util.concurrent.ExecutorService#isTerminated()
     */
    @Override
    public boolean isTerminated()
    {
        return this.state.get() == 2;
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
        this.state.set(1);

        this.mainLock.lock();

        ArrayList<Runnable> taskList = new ArrayList<>();

        try
        {
            // Interrupt Workers.
            for (Worker worker : this.workers.toArray(new Worker[0]))
            {
                removeWorker(worker);
            }

            // Drain Queue
            BlockingQueue<Runnable> queue = getWorkQueue();

            queue.drainTo(taskList);

            if (!queue.isEmpty())
            {
                for (Runnable task : queue.toArray(new Runnable[0]))
                {
                    if (queue.remove(task))
                    {
                        taskList.add(task);
                    }
                }
            }
        }
        finally
        {
            this.mainLock.unlock();
        }

        this.state.set(2);

        return taskList;
    }

    /**
     * Erzeugt einen neuen {@link Thread} und startet ihn.
     *
     * @param firstTask {@link Runnable}
     * @param coreWorker boolean
     */
    protected void addWorker(final Runnable firstTask, final boolean coreWorker)
    {
        Worker worker = new Worker();

        Thread thread = getThreadFactory().newThread(worker);

        worker.thread = thread;
        worker.isCoreWorker = coreWorker;
        worker.firstTask = firstTask;

        this.workers.add(worker);

        if (DEBUG)
        {
            System.out.printf("SimpleExecutorService.addWorker(): coreWorker=%b; workerName=%s; task=%s%n", coreWorker, thread.getName(),
                    (firstTask == null ? "" : firstTask.toString()));
        }

        // // Annahme: PREFIX-N; PREFIX_N
        // String threadName = thread.getName();
        // String[] splits = threadName.split("[-_]");
        // int index = Integer.parseInt(splits[splits.length - 1]);
        // worker.isCoreWorker = index <= getCoreSize();

        // ThreadNamen anpassen.
        // Pattern pattern = Pattern.compile("[a-zA-Z]+");
        // Matcher matcher = pattern.matcher(thread.getName());
        // matcher.find();
        //
        // String threadName = String.format("%s%02d", matcher.group(), this.threadNumber.incrementAndGet());
        // thread.setName(threadName);

        thread.start();
    }

    /**
     * Wird nach der Ausführung des Tasks aufgerufen.
     *
     * @param task {@link Runnable}
     * @param throwable {@link Throwable}
     * @see ThreadPoolExecutor#afterExecute
     */
    protected void afterExecute(final Runnable task, final Throwable throwable)
    {
    }

    /**
     * Wird vor der Ausführung des Tasks im Thread aufgerufen.
     *
     * @param thread {@link Thread}
     * @param task {@link Runnable}
     * @see ThreadPoolExecutor#beforeExecute
     */
    protected void beforeExecute(final Thread thread, final Runnable task)
    {
    }

    /**
     * Liefert den nächsten Task aus der Qeue.
     *
     * @param isCoreWorker boolean
     * @return {@link Runnable}
     */
    protected Runnable getTask(final boolean isCoreWorker)
    {
        Runnable task = null;

        try
        {
            if (isCoreWorker)
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
     * @return {@link ThreadFactory}
     */
    protected ThreadFactory getThreadFactory()
    {
        return this.threadFactory;
    }

    /**
     * @return {@link BlockingQueue}<Runnable>
     */
    protected BlockingQueue<Runnable> getWorkQueue()
    {
        return this.workQueue;
    }

    /**
     * @param task {@link Runnable}
     */
    protected void reject(final Runnable task)
    {
        // Caller runs
        // task.run();

        throw new RejectedExecutionException("Task " + task.toString() + " rejected from pool");
    }

    /**
     * Entfernt den Thread.
     *
     * @param worker {@link Worker}
     */
    protected void removeWorker(final Worker worker)
    {
        this.mainLock.lock();

        try
        {
            if (DEBUG)
            {
                System.out.printf("SimpleExecutorService2.removeWorker(): workerName=%s%n", worker.getName());
            }

            this.workers.remove(worker);
            this.threadNumber.decrementAndGet();
        }
        finally
        {
            this.mainLock.unlock();
        }
    }

    /**
     * Lauf-Methode des Workers.
     *
     * @param worker {@link Worker}
     */
    protected void runWorker(final Worker worker)
    {
        Thread thread = worker.thread;
        boolean isCoreWorker = worker.isCoreWorker;
        Runnable task = worker.firstTask;
        worker.firstTask = null;

        this.workersIdle.incrementAndGet();

        while ((task != null) || ((task = getTask(isCoreWorker)) != null))
        {
            this.workersIdle.decrementAndGet();
            Throwable thrown = null;

            try
            {
                beforeExecute(thread, task);

                task.run();
            }
            catch (RuntimeException x)
            {
                thrown = x;
                throw x;
            }
            catch (Error x)
            {
                thrown = x;
                throw x;
            }
            catch (Throwable x)
            {
                thrown = x;
                throw new Error(x);
            }
            finally
            {
                this.workersIdle.incrementAndGet();
                task = null;
                afterExecute(task, thrown);
            }
        }

        if (!isCoreWorker)
        {
            this.workersIdle.decrementAndGet();
            thread.interrupt();
        }
    }

    /**
     *
     */
    protected void startCoreThreads()
    {
        for (int i = 0; i < getCoreSize(); i++)
        {
            addWorker(null, true);
        }
    }
}
