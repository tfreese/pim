// Created: 10.02.2017
package de.freese.pim.gui.spring;

import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import de.freese.pim.core.addressbook.dao.DefaultAddressBookDAO;
import de.freese.pim.core.addressbook.service.DefaultAddressBookService;
import de.freese.pim.core.addressbook.service.IAddressBookService;
import de.freese.pim.core.jdbc.tx.TransactionalInvocationHandler;
import de.freese.pim.core.mail.dao.DefaultMailDAO;
import de.freese.pim.core.mail.service.DefaultMailService;
import de.freese.pim.core.mail.service.IMailService;
import de.freese.pim.core.service.ISettingsService;
import de.freese.pim.core.service.SettingService;
import de.freese.pim.core.thread.PIMThreadFactory;
import de.freese.pim.gui.PIMApplication;

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
        IAddressBookService addressBookService = (IAddressBookService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
        {
                IAddressBookService.class
        }, new TransactionalInvocationHandler(dataSource, new DefaultAddressBookService(new DefaultAddressBookDAO().dataSource(dataSource))));

        return addressBookService;
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
        // registerCloseable(() ->
        // {
        // LOGGER.info("Close ExecutorService");
        // Utils.shutdown(PIMApplication.executorService);
        // PIMApplication.executorService = null;
        // });

        return executorService;
    }

    /**
     * @param dataSource {@link DataSource}
     * @param settingsService {@link ISettingsService}
     * @param executorService {@link ExecutorService}
     * @return {@link IMailService}
     */
    @Bean(destroyMethod = "disconnectAccounts")
    public IMailService mailService(final DataSource dataSource, final ISettingsService settingsService, final ExecutorService executorService)
    {
        DefaultMailService defaultMailService = new DefaultMailService();
        defaultMailService.setMailDAO(new DefaultMailDAO().dataSource(dataSource));
        defaultMailService.setSettingsService(settingsService);
        defaultMailService.setExecutorService(executorService);

        IMailService mailService = (IMailService) Proxy.newProxyInstance(PIMApplication.class.getClassLoader(), new Class<?>[]
        {
                IMailService.class
        }, new TransactionalInvocationHandler(PIMApplication.getDataSource(), defaultMailService));

        // PIMApplication.registerCloseable(() ->
        // {
        // PIMApplication.LOGGER.info("Close MailService");
        // defaultMailService.disconnectAccounts();
        // });

        return mailService;
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
        // registerCloseable(() ->
        // {
        // LOGGER.info("Close ScheduledExecutorService");
        // Utils.shutdown(PIMApplication.scheduledExecutorService);
        // PIMApplication.scheduledExecutorService = null;
        // });

        return scheduledExecutorService;
    }

    /**
     * @return {@link ISettingsService}
     */
    @Bean
    public ISettingsService settingsService()
    {
        ISettingsService settingsService = SettingService.getInstance();
        // settingsService.setDataSource(dataSource);

        return settingsService;
    }
}
