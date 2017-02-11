// Created: 10.02.2017
package de.freese.pim.gui.spring;

import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
     * @return {@link ExecutorService}
     */
    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService executorService()
    {
        // Threads leben max. 60 Sekunden, wenn es nix zu tun gibt, min. 3 Threads, max. 10.
        BlockingQueue<Runnable> workQueue = new SynchronousQueue<>(true);

        // Max. 50 elemente in der Queue.
        // BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(50);

        ExecutorService executor =
                new ThreadPoolExecutor(3, 10, 60, TimeUnit.SECONDS, workQueue, new PIMThreadFactory("pimthread"), new ThreadPoolExecutor.AbortPolicy());
        ExecutorService executorService = Executors.unconfigurableExecutorService(executor);

        return executorService;
    }

    /**
     * @param dataSource {@link DataSource}
     * @param executorService {@link ExecutorService}
     * @param pimHome String
     * @return {@link IMailService}
     */
    @Bean(destroyMethod = "disconnectAccounts")
    public IMailService mailService(final DataSource dataSource, final ExecutorService executorService, @Value("${pim.home}") final String pimHome)
    {
        DefaultMailService defaultMailService = new DefaultMailService();
        defaultMailService.setBasePath(Paths.get(pimHome));
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
        ScheduledExecutorService scheduledExecutor =
                new ScheduledThreadPoolExecutor(3, new PIMThreadFactory("pimscheduler"), new ThreadPoolExecutor.AbortPolicy());
        ScheduledExecutorService scheduledExecutorService = Executors.unconfigurableScheduledExecutorService(scheduledExecutor);

        return scheduledExecutorService;
    }
}
