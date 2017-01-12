// Created: 11.01.2017
package de.freese.pim.core.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Objects;

import javax.sql.DataSource;

/**
 * Steuert eine Connection auf Methoden-Ebene wenn diese mit {@link Connectional} annotiert ist.<br>
 * Die {@link Connection} wird dabei im {@link ThreadLocal} {@link ConnectionHolder} abgelegt, wenn diese noch nicht vorhanden ist.
 *
 * @author Thomas Freese
 */
public class ConnectionalInvocationHandler implements InvocationHandler
{
    /**
     *
     */
    private final Object bean;

    /**
    *
    */
    private final DataSource dataSource;

    /**
     * Erzeugt eine neue Instanz von {@link ConnectionalInvocationHandler}
     *
     * @param dataSource {@link DataSource}
     * @param bean Object
     */
    public ConnectionalInvocationHandler(final DataSource dataSource, final Object bean)
    {
        super();

        Objects.requireNonNull(dataSource, "dataSource required");
        Objects.requireNonNull(bean, "bean required");

        this.dataSource = dataSource;
        this.bean = bean;
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        switch (method.getName())
        {
            case "equals":
                return (proxy == args[0]);
            case "hashCode":
                return System.identityHashCode(proxy);
            default:
                break;
        }

        Method beanMethod = this.bean.getClass().getMethod(method.getName(), method.getParameterTypes());

        if (beanMethod == null)
        {
            throw new RuntimeException(
                    "no bean method found: " + method.getName() + " with " + Arrays.toString(method.getParameterTypes()));
        }

        Connectional connectional = beanMethod.getAnnotation(Connectional.class);

        if (connectional != null)
        {
            if (ConnectionHolder.isEmpty())
            {
                ConnectionHolder.set(this.dataSource.getConnection());
            }

            if (connectional.transactional())
            {
                ConnectionHolder.beginTX();
            }
        }

        Object result = null;

        try
        {
            result = method.invoke(this.bean, args);

            if ((connectional != null) && connectional.transactional())
            {
                ConnectionHolder.commitTX();
            }
        }
        catch (InvocationTargetException ex)
        {
            if ((connectional != null) && connectional.transactional())
            {
                ConnectionHolder.rollbackTX();
            }

            throw ex.getTargetException();
        }
        finally
        {
            // Nested-Aufrufe werden nicht unterstützt (Hierachische Transactionen) !
            if (connectional != null)
            {
                ConnectionHolder.close();
            }

            // Remove geht immer, auch wenn nichts drin ist.
            ConnectionHolder.remove();
        }

        return result;
    }
}
