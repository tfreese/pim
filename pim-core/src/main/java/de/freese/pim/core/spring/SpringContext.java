// Created: 06.03.2017
package de.freese.pim.core.spring;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;

/**
 * Statischer Zugang zum {@link ApplicationContext}.
 *
 * @author Thomas Freese
 */
public final class SpringContext implements ApplicationContextAware, ResourceLoaderAware, EnvironmentAware, InitializingBean {
    private static SpringContext instance;

    @Configuration
    public static class Config {
        private static void setContext(final SpringContext instance) {
            SpringContext.instance = instance;
        }

        @Bean
        public SpringContext springContext() {
            setContext(new SpringContext());

            return SpringContext.instance;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return instance.applicationContext;
    }

    /**
     * {@link AsyncTaskExecutor} als Wrapper für einen {@link ExecutorService} oder eigener ThreadPool.
     */
    public static AsyncTaskExecutor getAsyncTaskExecutor() {
        return getApplicationContext().getBean("taskExecutor", AsyncTaskExecutor.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(final String beanID) {
        return (T) getApplicationContext().getBean(beanID);
    }

    public static <T> T getBean(final String beanID, final Class<T> requiredType) {
        return getApplicationContext().getBean(beanID, requiredType);
    }

    public static <T> Optional<T> getBeanByTypeAndAnnotation(final Class<T> clazz, final Class<? extends Annotation> annotationType) {
        final Collection<T> beans = getBeansByTypeAndAnnotation(clazz, annotationType);

        return beans.stream().findFirst();
    }

    /**
     * Liefert die erste gefundene Bean eines bestimmten Types mit einem bestimmten {@link Qualifier}.<br>
     * Die Bean muss die Spring-Annotation {@link Qualifier} mit einem Value verwenden.
     */
    public static <T> Optional<T> getBeanByTypeAndQualifier(final Class<T> clazz, final String qualifier) {
        final Collection<T> beans = getBeansByTypeAndAnnotation(clazz, Qualifier.class);

        return beans.stream().filter(bean -> {
            final Qualifier q = bean.getClass().getAnnotation(Qualifier.class);
            final String value = q != null ? q.value() : null;

            return qualifier.equals(value);
        }).findFirst();
    }

    public static <T> Collection<T> getBeansByTypeAndAnnotation(final Class<T> clazz, final Class<? extends Annotation> annotationType) {
        final Map<String, T> typedBeans = getApplicationContext().getBeansOfType(clazz);
        final Map<String, Object> annotatedBeans = getApplicationContext().getBeansWithAnnotation(annotationType);

        // Schnittmenge ermitteln.
        typedBeans.keySet().retainAll(annotatedBeans.keySet());

        return typedBeans.values();
    }

    public static Environment getEnvironment() {
        return instance.environment;
    }

    public static ExecutorService getExecutorService() {
        return getApplicationContext().getBean("executorService", ExecutorService.class);
    }

    public static Resource getResource(final String location) {
        return getResourceLoader().getResource(location);
    }

    public static ResourceLoader getResourceLoader() {
        return instance.resourceLoader;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return getApplicationContext().getBean(ScheduledExecutorService.class);
    }

    /**
     * {@link TaskScheduler} als Wrapper für einen {@link ScheduledExecutorService} oder eigener ThreadPool.
     */
    public static TaskScheduler getTaskScheduler() {
        return getApplicationContext().getBean(TaskScheduler.class);
    }

    private ApplicationContext applicationContext;
    private Environment environment;
    private ResourceLoader resourceLoader;

    private SpringContext() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(applicationContext, "An ApplicationContext is required. Use setApplicationContext(org.springframework.context.ApplicationContext) to provide one.");
        Assert.notNull(resourceLoader, "A ResourceLoader is required. Use setResourceLoader(org.springframework.core.io.ResourceLoader) to provide one.");
        Assert.notNull(environment, "An Environment is required. Use setEnvironment(org.springframework.core.env.Environment) to provide one.");
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext required");

        if (applicationContext instanceof AbstractApplicationContext ac) {
            ac.registerShutdownHook();
        }
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = Objects.requireNonNull(environment, "environment required");
    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = Objects.requireNonNull(resourceLoader, "resourceLoader required");
    }
}
