/**
 * Created: 19.02.2017
 */

package de.freese.pim.common.utils.concurrent;

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
        private final long sleepTimeInMillis;

        /**
         *
         */
        private final String text;

        /**
         * Erstellt ein neues {@link SleepingCallable} Object.
         *
         * @param text String
         * @param duration int
         * @param timeUnit {@link TimeUnit}
         */
        public SleepingCallable(final String text, final int duration, final TimeUnit timeUnit)
        {
            super();

            this.text = text;
            this.sleepTimeInMillis = timeUnit.toMillis(duration);
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Void call() throws Exception
        {
            // System.out.println(
            // "TestSimpleThreadPool.SleepingCallable.call(): thread=" + Thread.currentThread().getName() + "; " + toString());

            Thread.sleep(this.sleepTimeInMillis);

            return null;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("SleepingCallable [");
            builder.append("text=").append(this.text);
            builder.append("]");

            return builder.toString();
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
        executorService = new SimpleExecutorService(3, 6, 10, 1, TimeUnit.SECONDS, "test-", Thread.MIN_PRIORITY);
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
    public void test010() throws Exception
    {
        Assert.assertEquals(3, executorService.getCoreSize());
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(6, executorService.getMaxSize());
        Assert.assertEquals(0, executorService.getWorkingSize());
        Assert.assertEquals(3, executorService.getIdleSize());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020() throws Exception
    {
        Thread.sleep(200);

        Future<Void> future1 = executorService.submit(new SleepingCallable("020-1", 200, TimeUnit.MILLISECONDS));
        Thread.sleep(100);
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(1, executorService.getWorkingSize());
        Assert.assertEquals(2, executorService.getIdleSize());
        future1.get();

        Future<Void> future2 = executorService.submit(new SleepingCallable("020-2", 200, TimeUnit.MILLISECONDS));
        Future<Void> future3 = executorService.submit(new SleepingCallable("020-3", 200, TimeUnit.MILLISECONDS));
        Thread.sleep(100);
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(2, executorService.getWorkingSize());
        Assert.assertEquals(1, executorService.getIdleSize());
        future2.get();
        future3.get();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030() throws Exception
    {
        Thread.sleep(200);

        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(0, executorService.getWorkingSize());
        Assert.assertEquals(3, executorService.getIdleSize());

        Future<Void> future1 = executorService.submit(new SleepingCallable("030-1", 200, TimeUnit.MILLISECONDS));
        Future<Void> future2 = executorService.submit(new SleepingCallable("030-2", 200, TimeUnit.MILLISECONDS));
        Future<Void> future3 = executorService.submit(new SleepingCallable("030-3", 200, TimeUnit.MILLISECONDS));

        Thread.sleep(50);
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(3, executorService.getWorkingSize());
        Assert.assertEquals(0, executorService.getIdleSize());

        Future<Void> future4 = executorService.submit(new SleepingCallable("030-4", 200, TimeUnit.MILLISECONDS));

        Thread.sleep(50);
        Assert.assertEquals(4, executorService.getPoolSize());
        Assert.assertEquals(4, executorService.getWorkingSize());
        Assert.assertEquals(0, executorService.getIdleSize());

        future1.get();
        future2.get();
        future3.get();
        future4.get();

        // keepAliveTime
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(0, executorService.getWorkingSize());
        Assert.assertEquals(3, executorService.getIdleSize());
    }

    /**
     * Hier muss in der ausgabe die gleichen Logs kommen, da Thread-4 wiederverwendet wird.
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040() throws Exception
    {
        Thread.sleep(200);

        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(0, executorService.getWorkingSize());
        Assert.assertEquals(3, executorService.getIdleSize());

        Future<Void> future1 = executorService.submit(new SleepingCallable("040-1", 900, TimeUnit.MILLISECONDS));
        Future<Void> future2 = executorService.submit(new SleepingCallable("040-2", 900, TimeUnit.MILLISECONDS));
        Future<Void> future3 = executorService.submit(new SleepingCallable("040-3", 900, TimeUnit.MILLISECONDS));

        Thread.sleep(50);
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(3, executorService.getWorkingSize());
        Assert.assertEquals(0, executorService.getIdleSize());

        Future<Void> future4 = executorService.submit(new SleepingCallable("040-4", 600, TimeUnit.MILLISECONDS));
        Future<Void> future5 = executorService.submit(new SleepingCallable("040-5", 600, TimeUnit.MILLISECONDS));
        Future<Void> future6 = executorService.submit(new SleepingCallable("040-6", 600, TimeUnit.MILLISECONDS));

        Thread.sleep(100);
        Assert.assertEquals(4, executorService.getPoolSize());
        Assert.assertEquals(4, executorService.getWorkingSize());
        Assert.assertEquals(0, executorService.getIdleSize());

        future1.get();
        future2.get();
        future3.get();
        future4.get();
        future5.get();
        future6.get();

        // keepAliveTime
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        Assert.assertEquals(3, executorService.getPoolSize());
        Assert.assertEquals(0, executorService.getWorkingSize());
        Assert.assertEquals(3, executorService.getIdleSize());
    }
}
