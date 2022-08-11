package de.freese.pim.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import de.freese.pim.core.concurrent.PIMForkJoinWorkerThreadFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Testklasse für Sonstiges.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled("Funktioniert nur in der IDE")
class TestMisc
{
    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(Runtime.getRuntime().availableProcessors() * 2));

        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", PIMForkJoinWorkerThreadFactory.class.getName());
    }

    /**
     * <a href="http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html">Java8.CompletableFuture</a><br>
     * <a href="http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html">java-8-definitive-guide-to</a>
     *
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void test010CompletableFuture() throws Exception
    {
        List<String> results = new ArrayList<>();

        // @formatter:off
        CompletableFuture.supplyAsync(() ->
        {
            String threadName = Thread.currentThread().getName();
            System.out.println("supplyAsync: "+ threadName);
            return threadName;
        })
        .thenApplyAsync(result ->
        {
            String threadName = Thread.currentThread().getName();
            System.out.println("thenApplyAsync: "+ threadName);
            return result + "-2";
        })
        .thenApply(result ->
        {
            String threadName = Thread.currentThread().getName();
            System.out.println("thenApply: "+ threadName);
            return result + "-3";
        })
        .exceptionally(ex -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("exceptionally: "+ threadName);
            ex.printStackTrace();
            return null;
        })
        .thenAccept(results::add)
        ;
//        .whenCompleteAsync((result, ex) ->
//        {
//            // Wird nur ohne thenAccept ausgeführt.
//            String threadName = Thread.currentThread().getName();
//            System.out.println("whenCompleteAsync: "+ threadName);
//            results.add(result);
//
//            // ExceptionHandling
//            if(ex != null)
//            {
//                ex.printStackTrace();
//            }
//        });
        // @formatter:on

        TimeUnit.MILLISECONDS.sleep(100);

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("fork-join-01-2-3", results.get(0));
    }
}
