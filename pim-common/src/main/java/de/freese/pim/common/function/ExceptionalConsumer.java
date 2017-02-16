// Created: 16.02.2017
package de.freese.pim.common.function;

import java.util.function.Consumer;

/**
 * Interface eines {@link Consumer} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 */
public interface ExceptionalConsumer<T, E extends Exception>
{
    /**
     * Performs this operation on the given argument.
     *
     * @param t Object
     * @throws E Falls was schief geht.
     */
    public void accept(T t) throws E;
}
