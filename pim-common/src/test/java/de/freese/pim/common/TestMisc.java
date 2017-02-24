/**
 *
 */
package de.freese.pim.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Testklasse für Sonstiges.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMisc
{
    /**
     *
     */
    @BeforeClass
    public static void beforeclass()
    {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(Runtime.getRuntime().availableProcessors() * 2));

        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", "de.freese.pim.common.concurrent.PIMForkJoinWorkerThreadFactory");
    }

    /**
     * Erzeugt eine neue Instanz von {@link TestMisc}
     */
    public TestMisc()
    {
        super();
    }

    /**
     * http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html<br>
     * http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010CompletableFuture() throws Exception
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

        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Test-1-2", results.get(0));
    }
}