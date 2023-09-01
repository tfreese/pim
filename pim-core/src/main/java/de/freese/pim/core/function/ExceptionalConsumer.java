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
 */
@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Exception> {
    void accept(T t) throws E;

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

    default ExceptionalConsumer<T, E> andThen(final ExceptionalConsumer<T, E> after) {
        Objects.requireNonNull(after);

        return t -> {
            accept(t);
            after.accept(t);
        };
    }
}
