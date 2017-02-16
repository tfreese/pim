// Created: 16.02.2017
package de.freese.pim.common.function;

import java.util.function.Function;

/**
 * Interface einer {@link Function} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Parameter-Typ
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 */
public interface ExceptionalFunction<T, R, E extends Exception>
{
    /**
     * Applies this function to the given argument.
     *
     * @param t Object
     * @return Object
     * @throws E Falls was schief geht.
     * @throws Exception Falls was schief geht.
     */
    public R apply(T t) throws E;
}