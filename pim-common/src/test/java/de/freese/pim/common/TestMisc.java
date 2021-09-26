package de.freese.pim.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

/**
 * Testklasse für Sonstiges.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestMisc
{
    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(Runtime.getRuntime().availableProcessors() * 2));

        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", "de.freese.pim.common.concurrent.PIMForkJoinWorkerThreadFactory");
    }

    /**
     * http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html<br>
     * http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test010CompletableFuture() throws Exception
    {
        List<String> results = new ArrayList<>();

        // @formatter:off
        CompletableFuture.supplyAsync(() ->
        {
            System.out.println("supplyAsync: "+ Thread.currentThread().getName());
            return "Test";
        })
        .thenApplyAsync(result ->
        {
            System.out.println("thenApplyAsync: "+ Thread.currentThread().getName());
            return result + "-1";
        })
        .thenApply(result ->
        {
            System.out.println("thenApply: "+ Thread.currentThread().getName());
            return result + "-2";
        })
        .exceptionally(ex -> {
            System.out.println("exceptionally: "+ Thread.currentThread().getName());
            ex.printStackTrace();
            return null;
        })
        .thenAccept(results::add)
        ;
//        .whenCompleteAsync((result, ex) ->
//        {
//            // Wird nur ohne thenAccept ausgeführt.
//            System.out.println("whenCompleteAsync: "+ Thread.currentThread().getName());
////            results.add(result);
//
//            // ExceptionHandling
//            if(ex != null)
//            {
//                ex.printStackTrace();
//            }
//        });
        // @formatter:on

        Thread.sleep(100);

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("Test-1-2", results.get(0));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test020ThreadPool() throws Exception
    {
        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(4);
        bean.setMaxPoolSize(8);
        bean.setQueueCapacity(16);
        bean.setKeepAliveSeconds(60);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("test-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        bean.setExposeUnconfigurableExecutor(true);
        bean.afterPropertiesSet();

        ExecutorService executorService = bean.getObject();

        // BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(16);
        // // BlockingQueue<Runnable> queue = new SynchronousQueue<>(true);
        // ExecutorService executorService = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, queue, bean,
        // new ThreadPoolExecutor.CallerRunsPolicy());

        // CompletionService<Void> ecs = new ExecutorCompletionService<>(executorService);
        int size = 24;
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < size; i++)
        {
            int n = i;
            Callable<Void> callable = () -> {
                System.out.printf("%s: %d%n", Thread.currentThread().getName(), n);
                Thread.sleep(3000);
                return null;
            };

            futures.add(executorService.submit(callable));

            // ecs.submit();
        }

        // Warten bis alle fertig sind.
        for (int i = 0; i < size; ++i)
        {
            futures.get(i).get();
            // ecs.take().get();
        }

        assertTrue(true);
    }
}
