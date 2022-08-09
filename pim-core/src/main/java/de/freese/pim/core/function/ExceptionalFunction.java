// Created: 16.02.2017
package de.freese.pim.core.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Interface einer {@link Function} mit einer Exception.<br>
 *
 * @param <T> Konkreter Parameter-Typ
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ExceptionalFunction<T, R, E extends Exception>
{
    /**
     * @return {@link Function}
     *
     * @see java.util.function.Function#identity()
     */
    static <T> Function<T, T> identity()
    {
        return t -> t;
    }

    /**
     * @param after {@link ExceptionalFunction}
     *
     * @return {@link ExceptionalFunction}
     *
     * @see java.util.function.Function#andThen(Function)
     */
    default <V> ExceptionalFunction<T, V, E> andThen(final ExceptionalFunction<R, V, E> after)
    {
        Objects.requireNonNull(after);

        return t -> after.apply(apply(t));
    }

    // /**
    // * @see java.util.function.Function#andThen(Function)
    // * @param after {@link Function}
    // * @return {@link ExceptionalFunction}
    // */
    // public default <V> ExceptionalFunction<T, V, E> andThen(final Function<R, V> after)
    // {
    // Objects.requireNonNull(after);
    //
    // return t -> after.apply(apply(t));
    // }

    /**
     * @param t Object
     *
     * @return Object
     *
     * @throws Exception Falls was schiefgeht.
     * @see java.util.function.Function#apply(Object)
     */
    R apply(T t) throws E;

    /**
     * @param before {@link ExceptionalFunction}
     *
     * @return {@link ExceptionalFunction}
     *
     * @see java.util.function.Function#compose(Function)
     */
    default <V> ExceptionalFunction<V, R, E> compose(final ExceptionalFunction<V, T, E> before)
    {
        Objects.requireNonNull(before);

        return v -> apply(before.apply(v));
    }

    // /**
    // * @see java.util.function.Function#compose(Function)
    // * @param before {@link Function}
    // * @return {@link ExceptionalFunction}
    // */
    // public default <V> ExceptionalFunction<V, R, E> compose(final Function<V, T> before)
    // {
    // Objects.requireNonNull(before);
    //
    // return v -> apply(before.apply(v));
    // }
}
