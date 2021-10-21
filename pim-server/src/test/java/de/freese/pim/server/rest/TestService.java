// Created: 14.02.2017
package de.freese.pim.server.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * https://spring.io/guides/tutorials/bookmarks/<br>
 * https://twilblog.github.io/java/spring/rest/file/stream/2015/08/14/return-a-file-stream-from-spring-rest.html<br>
 * http://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers<br>
 * http://stackoverflow.com/questions/15781885/how-to-forward-large-files-with-resttemplate<br>
 * <br>
 * *view-source:http:// localhost:61222/pim/info<br>
 **
 * @see ResponseBodyEmitter
 * @see StreamingResponseBody
 *
 * @author Thomas Freese
 */
@RestController
@RequestMapping(path = "/test", produces =
{
        MediaType.APPLICATION_JSON_VALUE
}, headers = "Accept=application/json")
// @MultipartConfig(fileSizeThreshold = 20971520)
public class TestService
{
    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(TestService.class);
    /**
     * @see ThreadPoolTaskExecutor
     * @see ConcurrentTaskExecutor
     */
    @Resource
    private AsyncTaskExecutor taskExecutor;

    /**
     * Läuft im ThreadPool "MvcAsync" des RequestMappingHandlerAdapter, wenn über {@link WebMvcConfigurationSupport} nicht anders konfiguriert.
     *
     * @return {@link Future}
     */
    @GetMapping("/asyncDateCallable")
    public Callable<String> asycDateCallable()
    {
        return () -> {
            getLogger().info("asyncDateCallable: thread={}", Thread.currentThread().getName());
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));

            return LocalDateTime.now().toString();
        };
    }

    /**
     * Läuft im ForkJoin-ThreadPool, wenn kein Executor übergeben.
     *
     * @return {@link DeferredResult}
     */
    @GetMapping("/asyncDateDeferredResult")
    public DeferredResult<String> asycDateDeferredResult()
    {
        DeferredResult<String> deferredResult = new DeferredResult<>();

        CompletableFuture.supplyAsync(() -> {
            try
            {
                getLogger().info("supplyAsync: thread={}", Thread.currentThread().getName());
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));

                return LocalDateTime.now().toString();
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }, getTaskExecutor()).whenCompleteAsync((result, throwable) -> {
            getLogger().info("whenCompleteAsync: thread={}", Thread.currentThread().getName());

            if (throwable != null)
            {
                deferredResult.setErrorResult(throwable);
            }
            else
            {
                deferredResult.setResult(result);
            }
        }, getTaskExecutor());

        return deferredResult;
    }

    /**
     * Läuft im ThreadPool "MvcAsync" des RequestMappingHandlerAdapter, wenn über {@link WebMvcConfigurationSupport} nicht anders konfiguriert.
     *
     * @return {@link WebAsyncTask}
     */
    @GetMapping("/asyncDateWebAsyncTask")
    public WebAsyncTask<String> asycDateWebAsyncTask()
    {
        Callable<String> callable = () -> {
            getLogger().info("asycDateWebAsyncTask: thread={}", Thread.currentThread().getName());
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));

            return LocalDateTime.now().toString();
        };

        return new WebAsyncTask<>(callable);
        // return new WebAsyncTask<>(TimeUnit.SECONDS.toMillis(5), getTaskExecutor(), callable);
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link AsyncTaskExecutor}
     */
    protected AsyncTaskExecutor getTaskExecutor()
    {
        return this.taskExecutor;
    }

    /**
     * http://localhost:61222/greeter/test/?name=World
     *
     * @param name String
     *
     * @return {@link Map}
     */
    @GetMapping("/greeting")
    // @RequestMapping(path = "/greeting/{name}", method = RequestMethod.GET);
    // @PathVariable("name")
    public Map<String, String> greeting(@RequestParam(value = "name", defaultValue = "World") final String name)
    {
        Map<String, String> map = new HashMap<>();
        map.put("hello", name);

        return map;
    }
}
