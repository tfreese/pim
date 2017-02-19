/**
 * Created: 19.02.2017
 */

package de.freese.pim.common.utils.io;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.pim.common.concurrent.SimpleExecutorService;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSimpleThreadPool
{
    /**
     * @author Thomas Freese
     */
    private static class SleepingCallable implements Callable<Void>
    {
        /**
         *
         */
        private final int number;

        /**
         *
         */
        private final long sleepTimeInMillis;

        /**
         * Erstellt ein neues {@link SleepingCallable} Object.
         *
         * @param number int
         * @param duration int
         * @param timeUnit {@link TimeUnit}
         */
        public SleepingCallable(final int number, final int duration, final TimeUnit timeUnit)
        {
            super();

            this.number = number;
            this.sleepTimeInMillis = timeUnit.toMillis(duration);
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Void call() throws Exception
        {
            // System.out.println("TestSimpleThreadPool.SleepingCallable.call(): " + this.number);
            Thread.sleep(this.sleepTimeInMillis);

            return null;
        }
    }

    /**
     *
     */
    private static SimpleExecutorService executorService = null;

    /**
    *
    */
    @AfterClass
    public static void afterClass()
    {
        executorService.shutdownNow();
        // List<Runnable> drainedRunnables = executorService.shutdownNow();
        // System.out.println("TestSimpleThreadPool.afterClass(): drainedRunnables=" + drainedRunnables.size());
    }

    /**
     *
     */
    @BeforeClass
    public static void beforeClass()
    {
        executorService = new SimpleExecutorService(3, 6, 10, 2, TimeUnit.SECONDS);
    }

    /**
     * Erstellt ein neues {@link TestSimpleThreadPool} Object.
     */
    public TestSimpleThreadPool()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010StartSize() throws Exception
    {
        Assert.assertEquals(3, executorService.getCoreSize());
        Assert.assertEquals(3, executorService.getSize());
        Assert.assertEquals(6, executorService.getMaxSize());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020Size() throws Exception
    {
        Assert.assertEquals(0, executorService.getSizeThreadsWorking());
        Assert.assertEquals(3, executorService.getSizeThreadsIdle());

        Future<Void> future1 = executorService.submit(new SleepingCallable(1, 200, TimeUnit.MILLISECONDS));
        Thread.sleep(100);
        Assert.assertEquals(1, executorService.getSizeThreadsWorking());
        Assert.assertEquals(2, executorService.getSizeThreadsIdle());
        future1.get();

        Future<Void> future2 = executorService.submit(new SleepingCallable(2, 200, TimeUnit.MILLISECONDS));
        Future<Void> future3 = executorService.submit(new SleepingCallable(3, 200, TimeUnit.MILLISECONDS));
        Thread.sleep(100);
        Assert.assertEquals(2, executorService.getSizeThreadsWorking());
        Assert.assertEquals(1, executorService.getSizeThreadsIdle());
        future2.get();
        future3.get();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030Size() throws Exception
    {
        Assert.assertEquals(0, executorService.getSizeThreadsWorking());
        Assert.assertEquals(3, executorService.getSizeThreadsIdle());

        Future<Void> future1 = executorService.submit(new SleepingCallable(1, 200, TimeUnit.MILLISECONDS));
        Future<Void> future2 = executorService.submit(new SleepingCallable(2, 200, TimeUnit.MILLISECONDS));
        Future<Void> future3 = executorService.submit(new SleepingCallable(3, 200, TimeUnit.MILLISECONDS));

        Thread.sleep(100);
        Future<Void> future4 = executorService.submit(new SleepingCallable(4, 200, TimeUnit.MILLISECONDS));

        Assert.assertEquals(3, executorService.getCoreSize());
        Assert.assertEquals(4, executorService.getSize());
        Assert.assertEquals(6, executorService.getMaxSize());

        future1.get();
        future2.get();
        future3.get();
        future4.get();

        // keepAliveTime
        Thread.sleep(TimeUnit.SECONDS.toMillis(3));
        Assert.assertEquals(3, executorService.getSize());
    }
}
