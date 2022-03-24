// Created: 16.02.2017
package de.freese.pim.core.function;

/**
 * Interface eines {@link Runnable} mit einer Exception.<br>
 *
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ExceptionalRunnable<E extends Exception>
{
    /**
     * Performs this operation.
     *
     * @throws E Falls was schiefgeht.
     */
    void run() throws E;
}
