// Created: 16.02.2017
package de.freese.pim.core.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Interface eines {@link Consumer} mit einer Exception.<br>
 *
 * @param <T> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Exception>
{
    /**
     * @param t Object
     *
     * @throws Exception Falls was schiefgeht.
     * @see java.util.function.Consumer#accept(Object)
     */
    void accept(T t) throws E;

    // /**
    // * @see java.util.function.Consumer#andThen(Consumer)
    // * @param after {@link Consumer}
    // * @return {@link ExceptionalConsumer}
    // */
    // public default ExceptionalConsumer<T, E> andThen(final Consumer<T> after)
    // {
    // Objects.requireNonNull(after);
    //
    // return t ->
    // {
    // accept(t);
    // after.accept(t);
    // };
    // }

    /**
     * @param after {@link ExceptionalConsumer}
     *
     * @return {@link ExceptionalConsumer}
     *
     * @see java.util.function.Consumer#andThen(Consumer)
     */
    default ExceptionalConsumer<T, E> andThen(final ExceptionalConsumer<T, E> after)
    {
        Objects.requireNonNull(after);

        return t ->
        {
            accept(t);
            after.accept(t);
        };
    }
}
