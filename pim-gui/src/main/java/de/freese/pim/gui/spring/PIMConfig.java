// Created: 10.02.2017
package de.freese.pim.gui.spring;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import de.freese.pim.core.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.core.addressbook.service.DefaultAddressBookService;
import de.freese.pim.core.addressbook.service.IAddressBookService;
import de.freese.pim.core.mail.dao.DefaultMailDAO;
import de.freese.pim.core.mail.service.DefaultMailService;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.core.thread.PIMThreadFactory;

/**
 * Spring-Konfiguration von PIM.
 *
 * @author Thomas Freese
 */
@Configuration
@ComponentScan(basePackages = "de.freese.pim.core.spring")
public class PIMConfig
{
    /**
     * Erzeugt eine neue Instanz von {@link PIMConfig}
     */
    public PIMConfig()
    {
        super();
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link IAddressBookService}
     */
    @Bean
    public IAddressBookService addressBookService(final DataSource dataSource)
    {
        DefaultAddressBookService defaultAddressBookService = new DefaultAddressBookService();
        defaultAddressBookService.setAddressBookDAO(new DefaultAddressBookDAO().dataSource(dataSource));

        // IAddressBookService addressBookService = (IAddressBookService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
        // {
        // IAddressBookService.class
        // }, new TransactionalInvocationHandler(dataSource, new DefaultAddressBookService(new DefaultAddressBookDAO().dataSource(dataSource))));

        return defaultAddressBookService;
    }

    /**
     * @param pimHome String
     * @return {@link Path}
     */
    @Bean
    public Path basePath(@Value("${pim.home}") final String pimHome)
    {
        Path basePath = Paths.get(pimHome);

        return basePath;
    }

    // /**
    // * @return {@link ThreadPoolExecutorFactoryBean}
    // */
    // @Bean
    // public ThreadPoolExecutorFactoryBean executorService()
    // {
    // ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
    // bean.setCorePoolSize(3);
    // bean.setMaxPoolSize(10);
    // bean.setKeepAliveSeconds(60);
    // bean.setQueueCapacity(20);
    // bean.setThreadPriority(5);
    // bean.setThreadNamePrefix("pimthread-");
    // bean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    // bean.setExposeUnconfigurableExecutor(true);
    //
    // return bean;
    // }
    /**
     * http://www.angelikalanger.com/Articles/EffectiveJava/20.ThreadPools/20.ThreadPools.html
     *
     * @return {@link ExecutorService}
     */
    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService executorService()
    {
        int coreSize = 1;
        // int maxSize = Runtime.getRuntime().availableProcessors() + 1;
        int maxSize = 10;

        // Threads leben max. 60 Sekunden, wenn es nix zu tun gibt, min. 3 Threads, max. 10.
        // BlockingQueue<Runnable> workQueue = new SynchronousQueue<>(false);

        // BoundedQueue
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(20);
        // BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);

        // Der ThreadPool erzeugt erst neue Threads, wenn die Queue voll ist maxSize nocht nicht erreicht !
        // javafx.concurrent.Service#IO_QUEUE
        // BlockingQueue<Runnable> IO_QUEUE = new LinkedBlockingQueue<Runnable>()
        // {
        // @Override
        // public boolean offer(final Runnable runnable)
        // {
        // if (EXECUTOR.getPoolSize() < THREAD_POOL_SIZE)
        // {
        // return false;
        // }
        // return super.offer(runnable);
        // }
        // };

        ThreadFactory threadFactory = new PIMThreadFactory("pimthread", Thread.NORM_PRIORITY);

        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(coreSize, maxSize, 60, TimeUnit.SECONDS, workQueue, threadFactory, new ThreadPoolExecutor.AbortPolicy());
        // executor.allowCoreThreadTimeOut(true);
        // ExecutorService executor = Executors.newCachedThreadPool(threadFactory);
        ExecutorService executorService = Executors.unconfigurableExecutorService(executor);

        return executorService;
    }

    // /**
    // * FlywayAutoConfiguration.class
    // * @param dataSource {@link DataSource}
    // * @return {@link Flyway}
    // */
    // @Bean(initMethod = "migrate")
    // // @DependsOn("dataSource")
    // public Flyway flyway(final DataSource dataSource)
    // {
    // Flyway flyway = new Flyway();
    // flyway.setEncoding("UTF-8");
    // flyway.setBaselineOnMigrate(true);
    // flyway.setDataSource(dataSource);
    // // flyway.setLocations("filesystem:/path/to/migrations/");
    // flyway.setLocations("classpath:db/hsqldb");
    //
    // return flyway;
    // }

    /**
     * @param dataSource {@link DataSource}
     * @param executorService {@link ExecutorService}
     * @param basePath {@link Path}
     * @return {@link IMailService}
     */
    @Bean(destroyMethod = "disconnectAccounts")
    public IMailService mailService(final DataSource dataSource, final ExecutorService executorService, final Path basePath)
    {
        DefaultMailService defaultMailService = new DefaultMailService();
        defaultMailService.setBasePath(basePath);
        defaultMailService.setExecutorService(executorService);
        defaultMailService.setMailDAO(new DefaultMailDAO().dataSource(dataSource));
        //
        // IMailService mailService = (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
        // {
        // IMailService.class
        // }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));

        return defaultMailService;

    }

    /**
     * @return {@link ExecutorService}
     */
    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService scheduledExecutorService()
    {
        ThreadFactory threadFactory = new PIMThreadFactory("pimscheduler", Thread.NORM_PRIORITY);

        ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(3, threadFactory, new ThreadPoolExecutor.AbortPolicy());
        ScheduledExecutorService scheduledExecutorService = Executors.unconfigurableScheduledExecutorService(scheduledExecutor);

        return scheduledExecutorService;
    }

    /**
     * Wird für {@link Async} benötigt.
     *
     * @param executorService {@link ExecutorService}
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     * @return {@link TaskScheduler}
     */
    @Bean
    @Primary
    public TaskScheduler taskScheduler(final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService)
    {
        // LOGGER.info("Create TaskScheduler");

        ConcurrentTaskScheduler bean = new ConcurrentTaskScheduler(executorService, scheduledExecutorService);

        return bean;
    }
}
