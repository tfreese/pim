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
 */
@FunctionalInterface
public interface ExceptionalFunction<T, R, E extends Exception> {
    static <T> Function<T, T> identity() {
        return t -> t;
    }

    default <V> ExceptionalFunction<T, V, E> andThen(final ExceptionalFunction<R, V, E> after) {
        Objects.requireNonNull(after);

        return t -> after.apply(apply(t));
    }

    // public default <V> ExceptionalFunction<T, V, E> andThen(final Function<R, V> after)
    // {
    // Objects.requireNonNull(after);
    //
    // return t -> after.apply(apply(t));
    // }

    R apply(T t) throws E;

    default <V> ExceptionalFunction<V, R, E> compose(final ExceptionalFunction<V, T, E> before) {
        Objects.requireNonNull(before);

        return v -> apply(before.apply(v));
    }

    // public default <V> ExceptionalFunction<V, R, E> compose(final Function<V, T> before)
    // {
    // Objects.requireNonNull(before);
    //
    // return v -> apply(before.apply(v));
    // }
}
