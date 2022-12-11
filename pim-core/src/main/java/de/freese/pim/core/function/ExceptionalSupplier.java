// Created: 16.02.2017
package de.freese.pim.core.function;

import java.util.function.Supplier;

/**
 * Interface eines {@link Supplier} mit einer Exception.<br>
 *
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ExceptionalSupplier<R, E extends Exception>
{
    /**
     * @see java.util.function.Supplier#get()
     */
    R get() throws E;
}
