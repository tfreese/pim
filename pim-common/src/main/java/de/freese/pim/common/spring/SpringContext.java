// Created: 06.03.2017
package de.freese.pim.common.spring;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Statischer Zugang zum {@link ApplicationContext}.
 *
 * @author Thomas Freese
 */
@Component
public class SpringContext implements ApplicationContextAware, ResourceLoaderAware, EnvironmentAware, InitializingBean
{
    /**
    *
    */
    private static ApplicationContext applicationContext = null;

    /**
    *
    */
    private static Environment environment = null;

    /**
     *
     */
    private static ResourceLoader resourceLoader = null;

    /**
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * {@link AsyncTaskExecutor} als Wrapper für einen {@link ExecutorService} oder eigener ThreadPool.
     *
     * @return {@link AsyncTaskExecutor}
     */
    public static AsyncTaskExecutor getAsyncTaskExecutor()
    {
        return getApplicationContext().getBean("taskExecutor", AsyncTaskExecutor.class);
    }

    /**
     * Liefert die Bean mit der betreffenden BeanID.
     *
     * @param <T> Konkreter Typ des empfangenen Objects
     * @param beanID String
     * @return Object
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(final String beanID)
    {
        return (T) getApplicationContext().getBean(beanID);
    }

    /**
     * Liefert die Bean mit der betreffenden BeanID und erwartetem Typ.
     *
     * @param <T> Konkreter Typ des empfangenen Objects
     * @param beanID String
     * @param requiredType {@link Class}
     * @return Object
     */
    public static <T> T getBean(final String beanID, final Class<T> requiredType)
    {
        return getApplicationContext().getBean(beanID, requiredType);
    }

    /**
     * Liefert die erste gefundene Bean eines bestimmten Types mit einer bestimmten Annotation.
     *
     * @param <T> Konkreter Return-Typ
     * @param clazz Class
     * @param annotationType Class
     * @return {@link Optional}
     */
    public static <T> Optional<T> getBeanByTypeAndAnnotation(final Class<T> clazz, final Class<? extends Annotation> annotationType)
    {
        Collection<T> beans = getBeansByTypeAndAnnotation(clazz, annotationType);

        return beans.stream().findFirst();
    }

    /**
     * Liefert die erste gefundene Bean eines bestimmten Types mit einem bestimmten {@link Qualifier}.<br>
     * Die Bean muss die Spring-Annotation {@link Qualifier} mit einem Value verwenden.
     *
     * @param <T> Konkreter Return-Typ
     * @param clazz Class
     * @param qualifier String
     * @return {@link Optional}
     */
    public static <T> Optional<T> getBeanByTypeAndQualifier(final Class<T> clazz, final String qualifier)
    {
        Collection<T> beans = getBeansByTypeAndAnnotation(clazz, Qualifier.class);

        return beans.stream().filter(bean -> {
            Qualifier q = bean.getClass().getAnnotation(Qualifier.class);

            return qualifier.equals(q.value());
        }).findFirst();
    }

    /**
     * Liefert alle Beans eines bestimmten Types mit einer bestimmten Annotation.
     *
     * @param <T> Konkreter Return-Typ
     * @param clazz Class
     * @param annotationType Class
     * @return {@link Collection}
     */
    public static <T> Collection<T> getBeansByTypeAndAnnotation(final Class<T> clazz, final Class<? extends Annotation> annotationType)
    {
        Map<String, T> typedBeans = getApplicationContext().getBeansOfType(clazz);
        Map<String, Object> annotatedBeans = getApplicationContext().getBeansWithAnnotation(annotationType);

        // Schnittmenge ermitteln.
        typedBeans.keySet().retainAll(annotatedBeans.keySet());

        return typedBeans.values();
    }

    /**
     * @return {@link Environment}
     */
    public static Environment getEnvironment()
    {
        return environment;
    }

    /**
     * {@link ExecutorService} für die parallele Ausführung von Tasks.
     *
     * @return {@link ExecutorService}
     */
    public static ExecutorService getExecutorService()
    {
        return getApplicationContext().getBean(ExecutorService.class);
    }

    /**
     * Liefert die Resource.
     *
     * @param location String
     * @return {@link Resource}
     * @see ResourceLoader#getResource(String)
     */
    public static Resource getResource(final String location)
    {
        return getResourceLoader().getResource(location);
    }

    /**
     * @return {@link Resource}
     */
    public static ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    /**
     * {@link ScheduledExecutorService} für zeitgesteuerte Ausführung von Tasks.
     *
     * @return {@link ScheduledExecutorService}
     */
    public static ScheduledExecutorService getScheduledExecutorService()
    {
        return getApplicationContext().getBean(ScheduledExecutorService.class);
    }

    /**
     * {@link TaskScheduler} als Wrapper für einen {@link ScheduledExecutorService} oder eigener ThreadPool.
     *
     * @return {@link TaskScheduler}
     */
    public static TaskScheduler getTaskScheduler()
    {
        return getApplicationContext().getBean(TaskScheduler.class);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SpringContext}
     */
    SpringContext()
    {
        super();
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(applicationContext,
                "An ApplicationContext is required. Use setApplicationContext(org.springframework.context.ApplicationContext) to provide one.");
        Assert.notNull(resourceLoader, "A ResourceLoader is required. Use setResourceLoader(org.springframework.core.io.ResourceLoader) to provide one.");
        Assert.notNull(environment, "An Environment is required. Use setEnvironment(org.springframework.core.env.Environment) to provide one.");
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        Objects.requireNonNull(applicationContext, "applicationContext required");

        if (SpringContext.applicationContext != null)
        {
            throw new IllegalStateException("ApplicationContext already set !");
        }

        SpringContext.applicationContext = applicationContext;
    }

    /**
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(final Environment environment)
    {
        Objects.requireNonNull(environment, "environment required");

        if (SpringContext.environment != null)
        {
            throw new IllegalStateException("Environment already set !");
        }

        SpringContext.environment = environment;
    }

    /**
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        Objects.requireNonNull(resourceLoader, "resourceLoader required");

        if (SpringContext.resourceLoader != null)
        {
            throw new IllegalStateException("ResourceLoader already set !");
        }

        SpringContext.resourceLoader = resourceLoader;
    }

    // /**
    // * Destroy-Lifecycle ist nicht für Prototypes verfügbar !
    // *
    // * @param url String
    // * @return {@link DataSource}
    // */
    // @Bean(destroyMethod = "destroy")
    // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    // @Lazy(true)
    // public DataSource dataSource(final String url)
    // {
    // SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
    // dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
    // dataSource.setUrl(url);
    // // dataSource.setUsername(user);
    // // dataSource.setPassword(password);
    // dataSource.setSuppressClose(true);
    //
    // return dataSource;
    // }
}
