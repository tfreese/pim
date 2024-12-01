package de.freese.pim.core;

import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.pim.core.concurrent.PIMForkJoinWorkerThreadFactory;

/**
 * Testklasse für Sonstiges.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled("Funktioniert nur in der IDE")
class TestMisc {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMisc.class);

    @BeforeAll
    static void beforeAll() {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(Runtime.getRuntime().availableProcessors() * 2));

        System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", PIMForkJoinWorkerThreadFactory.class.getName());
    }

    /**
     * <a href="http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html">Java8.CompletableFuture</a><br>
     * <a href="http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html">java-8-definitive-guide-to</a>
     */
    @Test
    void test010CompletableFuture() {
        final List<String> results = new ArrayList<>();

        CompletableFuture.supplyAsync(() -> {
                    final String threadName = Thread.currentThread().getName();
                    LOGGER.info("supplyAsync: {}", threadName);
                    return threadName;
                })
                .thenApplyAsync(result -> {
                    final String threadName = Thread.currentThread().getName();
                    LOGGER.info("thenApplyAsync: {}", threadName);
                    return result + "-2";
                })
                .thenApply(result -> {
                    final String threadName = Thread.currentThread().getName();
                    LOGGER.info("thenApply: {}", threadName);
                    return result + "-3";
                })
                .exceptionally(ex -> {
                    final String threadName = Thread.currentThread().getName();
                    LOGGER.info("exceptionally: {}", threadName);
                    LOGGER.error(ex.getMessage(), ex);
                    return null;
                })
                .thenAccept(results::add)
        ;
        // .whenCompleteAsync((result, ex) -> {
        //     // Wird nur ohne thenAccept ausgeführt.
        //     String threadName = Thread.currentThread().getName();
        //     System.out.println("whenCompleteAsync: "+ threadName);
        //     results.add(result);
        //
        //     // ExceptionHandling
        //     if(ex != null) {
        //         LOGGER.error(ex.getMessage(), ex);
        //     }
        // });

        // TimeUnit.MILLISECONDS.sleep(100L);
        await().pollDelay(100L, TimeUnit.MILLISECONDS).until(() -> true);

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("fork-join-01-2-3", results.getFirst());
    }
}
