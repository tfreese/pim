// Created: 14.02.2017
package de.freese.pim.server.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * <a href="https://spring.io/guides/tutorials/bookmarks/">guides</a><br>
 * <a href="https://twilblog.github.io/java/spring/rest/file/stream/2015/08/14/return-a-file-stream-from-spring-rest.html">return-a-file-stream-from-spring-rest</a><br>
 * <a href="http://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers">downloading-a-file-from-spring-controllers</a><br>
 * <a href="http://stackoverflow.com/questions/15781885/how-to-forward-large-files-with-resttemplate">how-to-forward-large-files-with-resttemplate</a><br>
 * <br>
 * *view-source:http:// localhost:61222/pim/info<br>
 * *
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/test", produces = {MediaType.APPLICATION_JSON_VALUE}, headers = "Accept=application/json")
// @MultipartConfig(fileSizeThreshold = 20971520)
public class TestService {
    private final Logger logger = LoggerFactory.getLogger(TestService.class);

    @Resource
    private AsyncTaskExecutor taskExecutor;

    /**
     * Läuft im ThreadPool "MvcAsync" des RequestMappingHandlerAdapter, wenn über {@link WebMvcConfigurationSupport} nicht anders konfiguriert.
     */
    @GetMapping("/asyncDateCallable")
    public Callable<String> asyncDateCallable() {
        return () -> {
            getLogger().info("asyncDateCallable: thread={}", Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);

            return LocalDateTime.now().toString();
        };
    }

    /**
     * Läuft im ForkJoin-ThreadPool, wenn kein Executor übergeben.
     */
    @GetMapping("/asyncDateDeferredResult")
    public DeferredResult<String> asyncDateDeferredResult() {
        final DeferredResult<String> deferredResult = new DeferredResult<>();

        CompletableFuture.supplyAsync(() -> {
            try {
                getLogger().info("supplyAsync: thread={}", Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(1);

                return LocalDateTime.now().toString();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }, getTaskExecutor()).whenCompleteAsync((result, throwable) -> {
            getLogger().info("whenCompleteAsync: thread={}", Thread.currentThread().getName());

            if (throwable != null) {
                deferredResult.setErrorResult(throwable);
            }
            else {
                deferredResult.setResult(result);
            }
        }, getTaskExecutor());

        return deferredResult;
    }

    /**
     * Läuft im ThreadPool "MvcAsync" des RequestMappingHandlerAdapter, wenn über {@link WebMvcConfigurationSupport} nicht anders konfiguriert.
     */
    @GetMapping("/asyncDateWebAsyncTask")
    public WebAsyncTask<String> asyncDateWebAsyncTask() {
        final Callable<String> callable = () -> {
            getLogger().info("asyncDateWebAsyncTask: thread={}", Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);

            return LocalDateTime.now().toString();
        };

        return new WebAsyncTask<>(callable);
        // return new WebAsyncTask<>(TimeUnit.SECONDS.toMillis(5), getTaskExecutor(), callable);
    }

    /**
     * http://localhost:61222/greeter/test/?name=World
     */
    @GetMapping("/greeting")
    // @RequestMapping(path = "/greeting/{name}", method = RequestMethod.GET);
    // @PathVariable("name")
    public Map<String, String> greeting(@RequestParam(value = "name", defaultValue = "World") final String name) {
        final Map<String, String> map = new HashMap<>();
        map.put("hello", name);

        return map;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected AsyncTaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }
}
