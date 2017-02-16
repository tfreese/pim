// Created: 16.02.2017
package de.freese.pim.common.function;

import java.util.function.Supplier;

/**
 * Interface eines {@link Supplier} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 */
public interface ExceptionalSupplier<R, E extends Exception>
{
    /**
     * Applies this function to the given argument.
     *
     * @return Object
     * @throws Exception Falls was schief geht.
     */
    public R get() throws E;
}